package org.example.controller;

import org.example.dto.CompanyStatisticsDTO;
import org.example.exception.EmployeeNotFoundException;
import org.example.model.CompanyStatistics;
import org.example.model.Employee;
import org.example.service.EmployeeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final EmployeeService employeeService;

    public StatisticsController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/salary/average")
    public ResponseEntity<Map<String, Double>> getAverageSalary(
            @RequestParam(required = false) String company) {
        Map<String, Double> result = new HashMap<>();
        if (company != null) {
            Employee[] employees = employeeService.getEmployeeByCompanyName(company);
            double avg = 0.0;
            if (employees.length > 0) {
                double total = 0.0;
                for (Employee e : employees) {
                    if (e != null) {
                        total += e.getSalary();
                    }
                }
                avg = total / employees.length;
            }
            result.put("averageSalary", avg);
        } else {
            result.put("averageSalary", employeeService.getAverageSalary());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/company/{companyName}")
    public ResponseEntity<CompanyStatisticsDTO> getCompanyStatistics(
            @PathVariable String companyName) {
        Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
        CompanyStatistics companyStats = stats.get(companyName);

        if (companyStats == null) {
            throw new EmployeeNotFoundException("Company " + companyName + " not found");
        }

        CompanyStatisticsDTO dto = new CompanyStatisticsDTO();
        dto.setCompanyName(companyName);
        dto.setEmployeeCount(companyStats.getTotalEmployees());
        dto.setAverageSalary(companyStats.getAverageSalary());
        dto.setTopEarnerName(companyStats.getHighestPaidEmployeeName() + " " + companyStats.getHighestPaidEmployeeSurname());

        Employee[] employees = employeeService.getEmployeeByCompanyName(companyName);
        double maxSalary = 0.0;
        for (Employee e : employees) {
            if (e != null && e.getSalary() > maxSalary) {
                maxSalary = e.getSalary();
            }
        }
        dto.setHighestSalary(maxSalary);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/positions")
    public ResponseEntity<Map<String, Integer>> getEmployeesByPosition() {
        Map<String, Integer> result = employeeService.getPositionCounts();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> getEmployeesByStatus() {
        Map<String, Integer> result = new HashMap<>();
        result.put("employed", employeeService.getEmployees().length);
        return ResponseEntity.ok(result);
    }
}
