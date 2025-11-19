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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllEmployees() throws Exception {
        Employee emp1 = new Employee("Jan", "Kowalski", "jan@example.com", "CompanyA", "Developer", 5000.0);
        emp1.setStatus(EmploymentStatus.ACTIVE);
        Employee emp2 = new Employee("Anna", "Nowak", "anna@example.com", "CompanyB", "Manager", 6000.0);
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
        Employee emp = new Employee("Jan", "Kowalski", "jan@example.com", "CompanyA", "Developer", 5000.0);
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
        EmployeeDTO dto = new EmployeeDTO("Jan", "Kowalski", "jan@example.com", "CompanyA", Position.valueOf("Developer"), 5000.0, null);
        Employee emp = new Employee("Jan", "Kowalski", "jan@example.com", "CompanyA", "Developer", 5000.0);
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
        EmployeeDTO dto = new EmployeeDTO("Jan", "Kowalski", "jan@example.com", "CompanyA", Position.valueOf("Developer"), 5000.0, null);
        Mockito.when(employeeService.getEmployeeByEmail("jan@example.com")).thenReturn(new Employee());

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testDeleteEmployee() throws Exception {
        Employee emp = new Employee("Jan", "Kowalski", "jan@example.com", "CompanyA", "Developer", 5000.0);
        Mockito.when(employeeService.getEmployeeByEmail("jan@example.com")).thenReturn(emp);
        Mockito.doNothing().when(employeeService).deleteEmployee("jan@example.com");

        mockMvc.perform(delete("/api/employees/jan@example.com"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetEmployeesByCompany() throws Exception {
        Employee emp1 = new Employee("Jan", "Kowalski", "jan@example.com", "CompanyA", "Developer", 5000.0);
        emp1.setStatus(EmploymentStatus.ACTIVE);
        Employee emp2 = new Employee("Anna", "Nowak", "anna@example.com", "CompanyA", "Manager", 6000.0);
        emp2.setStatus(EmploymentStatus.ACTIVE);
        Mockito.when(employeeService.getEmployees()).thenReturn(new Employee[]{emp1, emp2});

        mockMvc.perform(get("/api/employees").param("company", "CompanyA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].company").value("CompanyA"));
    }

    @Test
    void testUpdateEmployeeStatus() throws Exception {
        Employee emp = new Employee("Jan", "Kowalski", "jan@example.com", "CompanyA", "Developer", 5000.0);
        emp.setStatus(EmploymentStatus.ACTIVE);
        Mockito.when(employeeService.getEmployeeByEmail("jan@example.com")).thenReturn(emp);
        Mockito.doNothing().when(employeeService).updateEmployee(any(Employee.class));

        mockMvc.perform(patch("/api/employees/jan@example.com/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ON_LEAVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ON_LEAVE"));
    }
}
