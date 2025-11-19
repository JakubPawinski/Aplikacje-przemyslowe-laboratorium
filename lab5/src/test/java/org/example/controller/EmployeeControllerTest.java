package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.EmployeeDTO;
import org.example.enums.EmploymentStatus;
import org.example.exception.DuplicateEmailException;
import org.example.exception.EmployeeNotFoundException;
import org.example.model.Employee;
import org.example.model.Position;
import org.example.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.ContextConfiguration;

@WebMvcTest(controllers = EmployeeController.class)
@ContextConfiguration(classes = {EmployeeController.class, org.example.exception.GlobalExceptionHandler.class, EmployeeControllerTest.MockConfig.class})
public class EmployeeControllerTest {

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
            EmployeeService mock = Mockito.mock(EmployeeService.class);
            Mockito.when(mock.getCompanyStatistics()).thenReturn(java.util.Collections.emptyMap());
            Mockito.when(mock.validateSalaryConsistency()).thenReturn(java.util.Collections.emptyList());
            return mock;
        }
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        Mockito.reset(employeeService);
    }

    @Test
    void testGetAllEmployees() throws Exception {
        Employee emp1 = new Employee("Jan", "Kowalski", "jan@example.com", "CompanyA", "MANAGER", 5000.0);
        emp1.setStatus(EmploymentStatus.ACTIVE);
        Employee emp2 = new Employee("Anna", "Nowak", "anna@example.com", "CompanyB", "MANAGER", 6000.0);
        emp2.setStatus(EmploymentStatus.ACTIVE);
        Mockito.when(employeeService.getEmployees()).thenReturn(new Employee[]{emp1, emp2});

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("jan@example.com"))
                .andExpect(jsonPath("$[1].email").value("anna@example.com"));
    }

    @Test
    void testGetEmployeeByEmail() throws Exception {
        Employee emp = new Employee("Jan", "Kowalski", "jan@example.com", "CompanyA", "MANAGER", 5000.0);
        emp.setStatus(EmploymentStatus.ACTIVE);
        Mockito.when(employeeService.getEmployeeByEmail("jan@example.com")).thenReturn(emp);

        mockMvc.perform(get("/api/employees/jan@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jan@example.com"))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"));
    }

    @Test
    void testGetEmployeeByEmailNotFound() throws Exception {
        Mockito.when(employeeService.getEmployeeByEmail("nonexistent@example.com")).thenReturn(null);

        mockMvc.perform(get("/api/employees/nonexistent@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddEmployee() throws Exception {
        EmployeeDTO dto = new EmployeeDTO("Jan", "Kowalski", "jan@example.com", "CompanyA", Position.MANAGER, 5000.0, null);
        Mockito.when(employeeService.getEmployeeByEmail("jan@example.com")).thenReturn(null);
        Mockito.doNothing().when(employeeService).addEmployee(any(Employee.class));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/employees/jan@example.com"));
    }

    @Test
    void testAddEmployeeDuplicate() throws Exception {
        EmployeeDTO dto = new EmployeeDTO("Jan", "Kowalski", "jan@example.com", "CompanyA", Position.MANAGER, 5000.0, null);
        Mockito.when(employeeService.getEmployeeByEmail("jan@example.com")).thenReturn(new Employee());

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testDeleteEmployee() throws Exception {
        Employee emp = new Employee("Jan", "Kowalski", "jan@example.com", "CompanyA", "MANAGER", 5000.0);
        Mockito.when(employeeService.getEmployeeByEmail("jan@example.com")).thenReturn(emp);
        Mockito.doNothing().when(employeeService).deleteEmployee("jan@example.com");

        mockMvc.perform(delete("/api/employees/jan@example.com"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetEmployeesByCompany() throws Exception {
        Employee emp1 = new Employee("Jan", "Kowalski", "jan@example.com", "CompanyA", "MANAGER", 5000.0);
        emp1.setStatus(EmploymentStatus.ACTIVE);
        Employee emp2 = new Employee("Anna", "Nowak", "anna@example.com", "CompanyA", "MANAGER", 6000.0);
        emp2.setStatus(EmploymentStatus.ACTIVE);
        Mockito.when(employeeService.getEmployees()).thenReturn(new Employee[]{emp1, emp2});

        mockMvc.perform(get("/api/employees").param("company", "CompanyA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].company").value("CompanyA"));
    }

    @Test
    void testUpdateEmployeeStatus() throws Exception {
        Employee emp = new Employee("Jan", "Kowalski", "jan@example.com", "CompanyA", "MANAGER", 5000.0);
        emp.setStatus(EmploymentStatus.ACTIVE);
        Mockito.when(employeeService.getEmployeeByEmail("jan@example.com")).thenReturn(emp);
        Mockito.doNothing().when(employeeService).updateEmployee(any(Employee.class));

        mockMvc.perform(patch("/api/employees/jan@example.com/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(EmploymentStatus.ON_LEAVE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ON_LEAVE"));
    }
}
