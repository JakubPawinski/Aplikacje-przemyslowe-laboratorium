package org.example.service;

import org.example.model.ImportSummary;
import org.example.model.Employee;
import org.example.model.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ImportServiceTest {

    private static final String HEADER = "firstName,lastName,email,company,position,salary";

    @Test
    void importFromCsv_validFile_importsAll(@TempDir Path tempDir) throws IOException {
        Path csv = tempDir.resolve("valid.csv");
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append(System.lineSeparator());
        sb.append("Adam,Nowy,adam.nowy@example.com,ACME,Manager,13000").append(System.lineSeparator());
        sb.append("Ewa,Stasiak,ewa.s@example.com,ACME,Intern,3000").append(System.lineSeparator());
        Files.writeString(csv, sb.toString(), StandardCharsets.UTF_8);

        EmployeeService employeeService = new EmployeeService(new Employee[0]);
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(csv.toString());

        assertNotNull(summary);
        assertEquals(2, summary.getImportedCount());
        assertTrue(summary.getErrors().isEmpty());

        // verify employees were added
        Employee[] arr = employeeService.getEmployees();
        assertEquals(2, arr.length);
        assertTrue(List.of(arr).stream().anyMatch(e -> "adam.nowy@example.com".equalsIgnoreCase(e.getEmail())));
        assertTrue(List.of(arr).stream().anyMatch(e -> "ewa.s@example.com".equalsIgnoreCase(e.getEmail())));
    }

    @Test
    void importFromCsv_invalidPosition_reportsErrorAndContinues(@TempDir Path tempDir) throws IOException {
        Path csv = tempDir.resolve("invalid_position.csv");
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append(System.lineSeparator());
        sb.append("John,Doe,john.doe@example.com,ACME,UnknownRole,5000").append(System.lineSeparator());
        sb.append("Ala,Kowal,ala.k@example.com,ACME,Manager,12000").append(System.lineSeparator());
        Files.writeString(csv, sb.toString(), StandardCharsets.UTF_8);

        EmployeeService employeeService = new EmployeeService(new Employee[0]);
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(csv.toString());

        assertNotNull(summary);
        assertEquals(1, summary.getImportedCount());
        assertFalse(summary.getErrors().isEmpty());
        assertTrue(summary.getErrors().stream().anyMatch(s -> s.contains("Invalid position")));

        // ensure the valid row was imported
        assertTrue(List.of(employeeService.getEmployees()).stream().anyMatch(e -> "ala.k@example.com".equalsIgnoreCase(e.getEmail())));
    }

    @Test
    void importFromCsv_negativeSalary_reportsError(@TempDir Path tempDir) throws IOException {
        Path csv = tempDir.resolve("negative_salary.csv");
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append(System.lineSeparator());
        // INTERN base is 3000, negative salary should be rejected
        sb.append("Tom,Minus,tom.m@example.com,ACME,Intern,-100").append(System.lineSeparator());
        Files.writeString(csv, sb.toString(), StandardCharsets.UTF_8);

        EmployeeService employeeService = new EmployeeService(new Employee[0]);
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(csv.toString());

        assertNotNull(summary);
        assertEquals(0, summary.getImportedCount());
        assertFalse(summary.getErrors().isEmpty());
        assertTrue(summary.getErrors().stream().anyMatch(s -> s.contains("Salary must be positive") && s.contains("3000.0")));
        assertEquals(0, employeeService.getEmployees().length);
    }

    @Test
    void importFromCsv_invalidSalaryFormat_reportsError(@TempDir Path tempDir) throws IOException {
        Path csv = tempDir.resolve("bad_salary.csv");
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append(System.lineSeparator());
        sb.append("Marta,Bad,marta.b@example.com,ACME,Manager,not_a_number").append(System.lineSeparator());
        Files.writeString(csv, sb.toString(), StandardCharsets.UTF_8);

        EmployeeService employeeService = new EmployeeService(new Employee[0]);
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(csv.toString());

        assertNotNull(summary);
        assertEquals(0, summary.getImportedCount());
        assertFalse(summary.getErrors().isEmpty());
        assertTrue(summary.getErrors().stream().anyMatch(s -> s.contains("Invalid salary")));
    }

    @Test
    void importFromCsv_duplicateEmail_reportsFailedToAdd(@TempDir Path tempDir) throws IOException {
        Path csv = tempDir.resolve("duplicate.csv");
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append(System.lineSeparator());
        sb.append("Existing,Person,exist@example.com,ACME,Manager,12000").append(System.lineSeparator());
        Files.writeString(csv, sb.toString(), StandardCharsets.UTF_8);

        // pre-populate service with employee that has same email
        Employee existing = new Employee("Existing","Person","exist@example.com","ACME", Position.MANAGER);
        EmployeeService employeeService = new EmployeeService(new Employee[]{existing});
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(csv.toString());

        assertNotNull(summary);
        assertEquals(0, summary.getImportedCount());
        assertFalse(summary.getErrors().isEmpty());
        assertTrue(summary.getErrors().stream().anyMatch(s -> s.contains("Failed to add employee") && s.contains("employee already exists")));
    }

    // --- Edge-case tests ---

    @Test
    void importFromCsv_missingColumns_reportsErrorAndContinues(@TempDir Path tempDir) throws IOException {
        Path csv = tempDir.resolve("missing_cols.csv");
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append(System.lineSeparator());
        sb.append("TooFew,Cols,only,4").append(System.lineSeparator());
        sb.append("Good,Row,good@example.com,ACME,Manager,12000").append(System.lineSeparator());
        Files.writeString(csv, sb.toString(), StandardCharsets.UTF_8);

        EmployeeService employeeService = new EmployeeService(new Employee[0]);
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(csv.toString());

        assertNotNull(summary);
        assertEquals(1, summary.getImportedCount());
        assertTrue(summary.getErrors().stream().anyMatch(s -> s.contains("Incorrect number of columns")));
        assertTrue(List.of(employeeService.getEmployees()).stream().anyMatch(e -> "good@example.com".equalsIgnoreCase(e.getEmail())));
    }

    @Test
    void importFromCsv_blankLines_ignored(@TempDir Path tempDir) throws IOException {
        Path csv = tempDir.resolve("blank_lines.csv");
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append(System.lineSeparator());
        sb.append("\n"); // explicit blank line
        sb.append("Bob,One,bob.o@example.com,ACME,Intern,3000").append(System.lineSeparator());
        Files.writeString(csv, sb.toString(), StandardCharsets.UTF_8);

        EmployeeService employeeService = new EmployeeService(new Employee[0]);
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(csv.toString());

        assertNotNull(summary);
        assertEquals(1, summary.getImportedCount());
        assertTrue(summary.getErrors().isEmpty());
        assertEquals(1, employeeService.getEmployees().length);
    }

    @Test
    void importFromCsv_fileNotFound_throwsIOException(@TempDir Path tempDir) {
        Path nonExistent = tempDir.resolve("nonexistent.csv");
        EmployeeService employeeService = new EmployeeService(new Employee[0]);
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(nonExistent.toString());

        assertNotNull(summary);
        assertEquals(0, summary.getImportedCount());
        assertFalse(summary.getErrors().isEmpty());
        assertTrue(summary.getErrors().stream().anyMatch(s -> s.contains("Failed to read CSV file")));
    }

    @Test
    void importFromCsv_invalidCsvFormat_reportsCsvException(@TempDir Path tempDir) throws IOException {
        Path csv = tempDir.resolve("invalid_csv.csv");
        // Create a malformed CSV (e.g., unclosed quote)
        Files.writeString(csv, HEADER + System.lineSeparator() + "\"unclosed,quote,field,ACME,Manager,12000", StandardCharsets.UTF_8);

        EmployeeService employeeService = new EmployeeService(new Employee[0]);
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(csv.toString());

        assertNotNull(summary);
        assertEquals(0, summary.getImportedCount());
        assertFalse(summary.getErrors().isEmpty());
        assertTrue(summary.getErrors().stream().anyMatch(s -> s.contains("Failed to read CSV file")));
    }

    @Test
    void importFromCsv_emptyFields_reportsErrors(@TempDir Path tempDir) throws IOException {
        Path csv = tempDir.resolve("empty_fields.csv");
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append(System.lineSeparator());
        sb.append(",,,ACME,Manager,12000").append(System.lineSeparator()); // empty names and email
        sb.append("Valid,Name,valid@example.com,,Intern,3000").append(System.lineSeparator()); // empty company
        Files.writeString(csv, sb.toString(), StandardCharsets.UTF_8);

        EmployeeService employeeService = new EmployeeService(new Employee[0]);
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(csv.toString());

        assertNotNull(summary);
        assertEquals(0, summary.getImportedCount()); // no valid rows
        assertFalse(summary.getErrors().isEmpty());
        assertTrue(summary.getErrors().stream().anyMatch(s -> s.contains("Required fields (firstName, lastName, email, company) cannot be empty")));
    }



    @Test
    void importFromCsv_salaryEqualToBaseSalary_importsSuccessfully(@TempDir Path tempDir) throws IOException {
        Path csv = tempDir.resolve("equal_salary.csv");
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append(System.lineSeparator());
        sb.append("Equal,Salary,equal@example.com,ACME,Intern,3000").append(System.lineSeparator()); // INTERN base is 3000
        Files.writeString(csv, sb.toString(), StandardCharsets.UTF_8);

        EmployeeService employeeService = new EmployeeService(new Employee[0]);
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(csv.toString());

        assertNotNull(summary);
        assertEquals(1, summary.getImportedCount());
        assertTrue(summary.getErrors().isEmpty());
    }

    @Test
    void importFromCsv_salaryBelowBaseSalary_reportsError(@TempDir Path tempDir) throws IOException {
        Path csv = tempDir.resolve("low_salary.csv");
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append(System.lineSeparator());
        sb.append("Low,Salary,low@example.com,ACME,Manager,10000").append(System.lineSeparator()); // MANAGER base is 12000
        Files.writeString(csv, sb.toString(), StandardCharsets.UTF_8);

        EmployeeService employeeService = new EmployeeService(new Employee[0]);
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(csv.toString());

        assertNotNull(summary);
        assertEquals(0, summary.getImportedCount());
        assertFalse(summary.getErrors().isEmpty());
        assertTrue(summary.getErrors().stream().anyMatch(s -> s.contains("Salary must be positive and at least 12000.0")));
    }

    @Test
    void importFromCsv_tooManyColumns_reportsError(@TempDir Path tempDir) throws IOException {
        Path csv = tempDir.resolve("too_many_cols.csv");
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append(System.lineSeparator());
        sb.append("Extra,Cols,extra@example.com,ACME,Manager,12000,extra_field").append(System.lineSeparator());
        Files.writeString(csv, sb.toString(), StandardCharsets.UTF_8);

        EmployeeService employeeService = new EmployeeService(new Employee[0]);
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(csv.toString());

        assertNotNull(summary);
        assertEquals(0, summary.getImportedCount());
        assertFalse(summary.getErrors().isEmpty());
        assertTrue(summary.getErrors().stream().anyMatch(s -> s.contains("Incorrect number of columns")));
    }

    @Test
    void importFromCsv_emptyFile_importsNothing(@TempDir Path tempDir) throws IOException {
        Path csv = tempDir.resolve("empty.csv");
        Files.writeString(csv, HEADER, StandardCharsets.UTF_8); // only header

        EmployeeService employeeService = new EmployeeService(new Employee[0]);
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(csv.toString());

        assertNotNull(summary);
        assertEquals(0, summary.getImportedCount());
        assertTrue(summary.getErrors().isEmpty());
    }

    @Test
    void importFromCsv_multipleErrorsInFile_reportsAll(@TempDir Path tempDir) throws IOException {
        Path csv = tempDir.resolve("multiple_errors.csv");
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER).append(System.lineSeparator());
        sb.append("Valid,Row,valid@example.com,ACME,Manager,13000").append(System.lineSeparator());
        sb.append("Invalid,Pos,invalid@example.com,ACME,Unknown,5000").append(System.lineSeparator());
        sb.append("Bad,Salary,bad@example.com,ACME,Intern,abc").append(System.lineSeparator());
        sb.append("Low,Sal,low@example.com,ACME,Team Lead,7000").append(System.lineSeparator()); // TEAM_LEAD base is 8000
        Files.writeString(csv, sb.toString(), StandardCharsets.UTF_8);

        EmployeeService employeeService = new EmployeeService(new Employee[0]);
        ImportService importService = new ImportService(employeeService);

        ImportSummary summary = importService.importFromCsv(csv.toString());

        assertNotNull(summary);
        assertEquals(1, summary.getImportedCount()); // only valid
        assertEquals(3, summary.getErrors().size()); // three errors
    }

}
