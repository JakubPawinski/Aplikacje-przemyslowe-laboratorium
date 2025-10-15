package org.example.service;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.example.model.Employee;
import org.example.model.ImportSummary;
import org.example.model.Position;
import java.util.List;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class ImportService {
    private final EmployeeService employeeService;

    public ImportService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public ImportSummary importFromCsv(String filePath) {
        List<String> errors = new ArrayList<>();
        int importedCount = 0;

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> allRows = reader.readAll();
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);

                if (row.length == 0 || (row.length == 1 && row[0].trim().isEmpty())) {
                    continue; // Skip empty rows
                }
                if (row.length != 6) {
                    errors.add("Row " + (i + 1) + ": Incorrect number of columns");
                    continue;
                }

                String firstName = row[0].trim();
                String lastName = row[1].trim();
                String email = row[2].trim();
                String company = row[3].trim();
                String positionStr = row[4].trim();
                String salaryStr = row[5].trim();

                Position position;
                double salary;
                try {
                    position = Position.valueOf(positionStr.toUpperCase().replace(" ", "_"));
                } catch (IllegalArgumentException e) {
                    errors.add("Row " + (i + 1) + ": Invalid position '" + positionStr + "'");
                    continue;
                }
                try {
                    salary = Double.parseDouble(salaryStr);
                    if (salary <= 0 || salary < position.getBaseSalary()) {
                        errors.add("Row " + (i + 1) + ": Salary must be positive and at least " + position.getBaseSalary());
                        continue;
                    }
                } catch (NumberFormatException e) {
                    errors.add("Row " + (i + 1) + ": Invalid salary '" + salaryStr + "'");
                    continue;
                }

                try {
                    Employee employee = new Employee(firstName, lastName, email, company, position);
                    employee.setSalary(salary);
                    employeeService.addEmployee(employee);
                    importedCount++;
                } catch (Exception e) {
                    errors.add("Row " + (i + 1) + ": Failed to add employee - " + e.getMessage());
                }
            }
        } catch (IOException | CsvException e) {
            errors.add("Failed to read CSV file: " + e.getMessage());
        }
        return new ImportSummary(importedCount, errors);
    }
}
