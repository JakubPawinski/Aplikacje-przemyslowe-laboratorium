package org.example.controller;
import org.example.dto.EmployeeDTO;
import org.example.enums.EmploymentStatus;
import org.example.exception.EmployeeNotFoundException;
import org.example.exception.DuplicateEmailException;
import org.example.exception.InvalidDataException;
import org.example.service.EmployeeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.example.model.Employee;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(@RequestParam(required = false) String company) {
        Employee[] employees = employeeService.getEmployees();
        List<EmployeeDTO> employeeDTOs = new ArrayList<>();

        for (Employee emp : employees) {
            if (company != null && !emp.getCompanyName().equalsIgnoreCase(company)) {
                continue;
            }
            EmployeeDTO dto = new EmployeeDTO(
                    emp.getName(),
                    emp.getSurname(),
                    emp.getEmail(),
                    emp.getCompanyName(),
                    emp.getPosition(),
                    emp.getSalary(),
                    null
            );
            employeeDTOs.add(dto);
        }
        return ResponseEntity.ok(employeeDTOs);
    }

    @GetMapping("/{email}")
    public ResponseEntity<EmployeeDTO> getEmployeeByEmail(@PathVariable String email) {
        Employee employee = employeeService.getEmployeeByEmail(email);

        if (employee == null) {
            throw new EmployeeNotFoundException("Employee with email" + email + " not found");
        }

        EmployeeDTO dto = new EmployeeDTO(
                employee.getName(),
                employee.getSurname(),
                employee.getEmail(),
                employee.getCompanyName(),
                employee.getPosition(),
                employee.getSalary(),
                null
        );
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> addEmployee(@RequestBody EmployeeDTO employeeDTO) {
        if (employeeDTO.getEmail() == null || employeeDTO.getEmail().isEmpty()) {
            throw new InvalidDataException("Email cannot be null or empty");
        }

        if (employeeService.getEmployeeByEmail(employeeDTO.getEmail()) != null) {
            throw new DuplicateEmailException("Employee with email " + employeeDTO.getEmail() + " already exists");
        }

        Employee employee = new Employee(
                employeeDTO.getFirstName(),
                employeeDTO.getLastName(),
                employeeDTO.getEmail(),
                employeeDTO.getCompany(),
                employeeDTO.getPosition().name(),
                employeeDTO.getSalary()
        );
        employeeService.addEmployee(employee);

        EmployeeDTO resultDTO = new EmployeeDTO(
                employee.getName(),
                employee.getSurname(),
                employee.getEmail(),
                employee.getCompanyName(),
                employee.getPosition(),
                employee.getSalary(),
                null
        );
        return ResponseEntity.created(
                URI.create("/api/employees/" + employee.getEmail())
        ).body(resultDTO);
    }

    @PutMapping("/{email}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable String email, @RequestBody EmployeeDTO employeeDTO) {
        Employee employee = employeeService.getEmployeeByEmail(email);

        if (employee == null) {
            throw new EmployeeNotFoundException("Employee with email " + email + " not found");
        }

        employee.setName(employeeDTO.getFirstName());
        employee.setSurname(employeeDTO.getLastName());
        employee.setCompanyName(employeeDTO.getCompany());
        if (employeeDTO.getPosition() != null) {
            employee.setPosition(employeeDTO.getPosition());
        }
        if (employeeDTO.getSalary() != null) {
            employee.setSalary(employeeDTO.getSalary());
        }
        employeeService.updateEmployee(employee);

        EmployeeDTO resultDTO = new EmployeeDTO(
                employee.getName(),
                employee.getSurname(),
                employee.getEmail(),
                employee.getCompanyName(),
                employee.getPosition(),
                employee.getSalary(),
                null
        );
        return ResponseEntity.ok(resultDTO);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String email) {
        Employee employee = employeeService.getEmployeeByEmail(email);

        if (employee == null) {
            throw new EmployeeNotFoundException("Employee with email " + email + " not found");
        }

        employeeService.deleteEmployee(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByStatus(@PathVariable String status) {
        Employee[] employees = employeeService.getEmployees();
        List<EmployeeDTO> employeeDTOs = new ArrayList<>();

        for (Employee emp : employees) {
            if (emp.getPosition().name().equalsIgnoreCase(status)) {
                EmployeeDTO dto = new EmployeeDTO(
                        emp.getName(),
                        emp.getSurname(),
                        emp.getEmail(),
                        emp.getCompanyName(),
                        emp.getPosition(),
                        emp.getSalary(),
                        null
                );
                employeeDTOs.add(dto);
            }
        }
        return ResponseEntity.ok(employeeDTOs);
    }

    @PatchMapping("/{email}/status")
    public ResponseEntity<EmployeeDTO> updateEmployeeStatus(@PathVariable String email, @RequestBody(required = false) EmploymentStatus status) {
        Employee employee = employeeService.getEmployeeByEmail(email);
        if (employee == null) {
            throw new EmployeeNotFoundException("Employee with email " + email + " not found");
        }
        employee.setStatus(status);
        employeeService.updateEmployee(employee);
        EmployeeDTO resultDTO = new EmployeeDTO(
                employee.getName(),
                employee.getSurname(),
                employee.getEmail(),
                employee.getCompanyName(),
                employee.getPosition(),
                employee.getSalary(),
                employee.getStatus()
        );
        return ResponseEntity.ok(resultDTO);
    }
}
