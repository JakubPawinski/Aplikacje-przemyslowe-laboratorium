package org.example;

import org.example.model.CompanyStatistics;
import org.example.model.Employee;
import org.example.service.EmployeeService;
import org.example.service.ImportService;
import org.example.service.ApiService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@ImportResource("classpath:employees-beans.xml")
public class EmployeeManagementApplication implements CommandLineRunner {

    private final ImportService importService;
    private final EmployeeService employeeService;
    private final ApiService apiService;
    private final List<Employee> xmlEmployees;
    private final String csvFilePath;

    public EmployeeManagementApplication(
            ImportService importService,
            EmployeeService employeeService,
            ApiService apiService,
            @Qualifier("xmlEmployees") List<Employee> xmlEmployees,
            @Value("${app.csv.filepath}") String csvFilePath) {
        this.importService = importService;
        this.employeeService = employeeService;
        this.apiService = apiService;
        this.xmlEmployees = xmlEmployees;
        this.csvFilePath = csvFilePath;
    }

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Employee Management Application ===");

        System.out.println("\nImporting employees from CSV file: " + csvFilePath);
        importService.importFromCsv(csvFilePath);
        System.out.println("Employees after CSV import:");
        employeeService.displayAllEmployees();

        System.out.println("\nEmployees loaded from XML configuration:");
        for (Employee emp : xmlEmployees) {
            System.out.println(emp);
            try {
                employeeService.addEmployee(emp);
            } catch (IllegalArgumentException e) {
                System.out.println("Employee already exists: " + emp.getEmail());
            }
        }
        System.out.println("Employees after adding XML employees:");
        employeeService.displayAllEmployees();

        System.out.println("\nFetching employees from external API:");
        try {
            List<Employee> apiEmployees = apiService.fetchEmployeesFromApi();
            for (Employee emp : apiEmployees) {
                System.out.println(emp);
                try {
                    employeeService.addEmployee(emp);
                } catch (IllegalArgumentException e) {
                    System.out.println("Employee already exists: " + emp.getEmail());
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching from API: " + e.getMessage());
        }
        System.out.println("Employees after adding API employees:");
        employeeService.displayAllEmployees();

        System.out.println("Company statistics:");
        Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
        for (Map.Entry<String, CompanyStatistics> entry : stats.entrySet()) {
            System.out.println("Company: " + entry.getKey() + " -> " + entry.getValue());
        }

        System.out.println("Salary validation");

        List<Employee> invalidEmployees = employeeService.validateSalaryConsistency();
        if (invalidEmployees == null || invalidEmployees.isEmpty()) {
            System.out.println("Brak niezgodnych wynagrodzeń.");
        } else {
            System.out.println("Pracownicy zarabiający poniżej bazowej stawki:");
            for (Employee emp : invalidEmployees) {
                System.out.println(emp);
            }
        }

        System.out.println("\n=== End of Application ===");
    }
}
