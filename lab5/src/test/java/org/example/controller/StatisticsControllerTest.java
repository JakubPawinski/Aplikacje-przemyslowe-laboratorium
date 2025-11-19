package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CompanyStatisticsDTO;
import org.example.model.CompanyStatistics;
import org.example.model.Employee;
import org.example.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.example.model.Position;

@WebMvcTest(controllers = StatisticsController.class)
@ContextConfiguration(classes = {StatisticsController.class, org.example.exception.GlobalExceptionHandler.class, StatisticsControllerTest.MockConfig.class})
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeService employeeService;

    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        public EmployeeService employeeService() {
            return Mockito.mock(EmployeeService.class);
        }
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        Mockito.reset(employeeService);
    }

    @Test
    void testGetAverageSalary() throws Exception {
        Mockito.when(employeeService.getAverageSalary()).thenReturn(5500.0);

        mockMvc.perform(get("/api/statistics/salary/average"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageSalary").value(5500.0));
    }

    @Test
    void testGetAverageSalaryByCompany() throws Exception {
        Employee emp1 = new Employee("Jan", "Kowalski", "jan@example.com", "CompanyA", "MANAGER", 5000.0);
        Employee emp2 = new Employee("Anna", "Nowak", "anna@example.com", "CompanyA", "MANAGER", 6000.0);
        Mockito.when(employeeService.getEmployeeByCompanyName("CompanyA")).thenReturn(new Employee[]{emp1, emp2});

        mockMvc.perform(get("/api/statistics/salary/average").param("company", "CompanyA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageSalary").value(5500.0));
    }

    @Test
    void testGetCompanyStatistics() throws Exception {
        CompanyStatistics stats = new CompanyStatistics();
        stats.setTotalEmployees(2);
        stats.setAverageSalary(5500.0);
        stats.setHighestPaidEmployeeName("Anna");
        stats.setHighestPaidEmployeeSurname("Nowak");

        Map<String, CompanyStatistics> statsMap = new HashMap<>();
        statsMap.put("CompanyA", stats);

        Employee emp1 = new Employee("Jan", "Kowalski", "jan@example.com", "CompanyA", "MANAGER", 5000.0);
        Employee emp2 = new Employee("Anna", "Nowak", "anna@example.com", "CompanyA", "MANAGER", 6000.0);

        Mockito.when(employeeService.getCompanyStatistics()).thenReturn(statsMap);
        Mockito.when(employeeService.getEmployeeByCompanyName("CompanyA")).thenReturn(new Employee[]{emp1, emp2});

        mockMvc.perform(get("/api/statistics/company/CompanyA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("CompanyA"))
                .andExpect(jsonPath("$.employeeCount").value(2))
                .andExpect(jsonPath("$.averageSalary").value(5500.0))
                .andExpect(jsonPath("$.highestSalary").value(6000.0));
    }

    @Test
    void testGetCompanyStatisticsNotFound() throws Exception {
        Map<String, CompanyStatistics> statsMap = new HashMap<>();
        Mockito.when(employeeService.getCompanyStatistics()).thenReturn(statsMap);

        mockMvc.perform(get("/api/statistics/company/NonExistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetEmployeesByPosition() throws Exception {
        Map<String, Integer> positionCounts = new HashMap<>();
        positionCounts.put("MANAGER", 8);

        Mockito.when(employeeService.getPositionCounts()).thenReturn(positionCounts);

        mockMvc.perform(get("/api/statistics/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.MANAGER").value(8));
    }

    @Test
    void testGetEmployeesByStatus() throws Exception {
        Employee emp1 = new Employee("Jan", "Kowalski", "jan@example.com", "CompanyA", "MANAGER", 5000.0);
        Employee emp2 = new Employee("Anna", "Nowak", "anna@example.com", "CompanyA", "MANAGER", 6000.0);
        Mockito.when(employeeService.getEmployees()).thenReturn(new Employee[]{emp1, emp2});

        mockMvc.perform(get("/api/statistics/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employed").value(2));
    }
}
