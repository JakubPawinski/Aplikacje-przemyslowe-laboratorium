package org.example.service;

import org.example.model.CompanyStatistics;
import org.example.model.Employee;
import org.example.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeServiceTest {

    private EmployeeService service;
    private Employee e1;
    private Employee e2;
    private Employee e3;

    @BeforeEach
    void setup() {
        e1 = new Employee("Jan", "Kowalski", "jan.k@example.com", "ABC", Position.MANAGER);
        e2 = new Employee("Anna", "Nowak", "anna.n@example.com", "ABC", Position.TEAM_LEAD);
        e3 = new Employee("Piotr", "Zalewski", "piotr.z@example.com", "XYZ", Position.INTERN);
        service = new EmployeeService(new Employee[]{e1, e2, e3});
    }

    @Test
    void addEmployee_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.addEmployee(null));
    }

    @Test
    void addEmployee_duplicateEmail_throwsIllegalArgumentException() {
        // duplicate by email (case-insensitive)
        Employee dup = new Employee("Jan", "Kowalski2", "JAN.K@example.com", "ABC", Position.MANAGER);
        assertThrows(IllegalArgumentException.class, () -> service.addEmployee(dup));
    }

    @Test
    void getEmployeeByCompanyName_nonExistingCompany_returnsEmptyArray() {
        Employee[] result = service.getEmployeeByCompanyName("NON_EXISTENT");
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void getEmployeeByCompanyName_nullParam_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getEmployeeByCompanyName(null));
        assertThrows(IllegalArgumentException.class, () -> service.getEmployeeByCompanyName(""));
    }

    @Test
    void getAverageSalary_emptyEmployees_returnsZero() {
        EmployeeService empty = new EmployeeService(new Employee[0]);
        double avg = empty.getAverageSalary();
        assertEquals(0.0, avg, 0.0001);
    }

    @Test
    void getHighestPaidEmployee_empty_returnsOptionalEmpty() {
        EmployeeService empty = new EmployeeService(new Employee[0]);
        Optional<Employee> opt = empty.getHighestPaidEmployee();
        assertTrue(opt.isEmpty());
    }

    @Test
    void getHighestPaidEmployee_returnsCorrectEmployee() {
        // ensure different salaries: set one salary higher
        e3.setSalary(20000.0);
        Optional<Employee> opt = service.getHighestPaidEmployee();
        assertTrue(opt.isPresent());
        assertEquals(e3.getEmail().toLowerCase(), opt.get().getEmail().toLowerCase());
    }

    @Test
    void validateSalaryConsistency_detectsLowerThanBaseSalary() {
        // make e2 salary lower than base
        e2.setSalary(1000.0); // TEAM_LEAD base is 8000
        List<Employee> inconsistent = service.validateSalaryConsistency();
        assertNotNull(inconsistent);
        assertTrue(inconsistent.stream().anyMatch(emp -> emp.getEmail().equalsIgnoreCase(e2.getEmail())));
    }

    @Test
    void validateSalaryConsistency_allConsistent_returnsEmptyList() {
        List<Employee> inconsistent = service.validateSalaryConsistency();
        assertNotNull(inconsistent);
        // by default constructor sets salary == baseSalary, so list should be empty
        assertEquals(0, inconsistent.size());
    }

    // --- Additional tests added below ---

    @Test
    void constructor_null_initializesEmptyEmployees() {
        EmployeeService nullService = new EmployeeService(null);
        assertNotNull(nullService.getEmployees());
        assertEquals(0, nullService.getEmployees().length);
    }

    @Test
    void addEmployee_toEmptyArray_addsEmployee() {
        EmployeeService empty = new EmployeeService(new Employee[0]);
        Employee newE = new Employee("Adam", "Nowy", "adam.nowy@example.com", "NEW", Position.INTERN);
        empty.addEmployee(newE);
        Employee[] arr = empty.getEmployees();
        assertEquals(1, arr.length);
        assertEquals("adam.nowy@example.com", arr[0].getEmail());
    }

    @Test
    void getEmployeesSortedByLastName_returnsSorted() {
        Employee[] sorted = service.getEmployeesSortedByLastName();
        assertEquals(3, sorted.length);
        assertEquals("Kowalski", sorted[0].getSurname());
        assertEquals("Nowak", sorted[1].getSurname());
        assertEquals("Zalewski", sorted[2].getSurname());
    }

    @Test
    void getEmployeesGroupedByPosition_and_getPositionCounts() {
        Map<String, List<Employee>> grouped = service.getEmployeesGroupedByPosition();
        assertNotNull(grouped);
        assertTrue(grouped.containsKey(Position.MANAGER.name()));
        assertTrue(grouped.containsKey(Position.TEAM_LEAD.name()));
        assertTrue(grouped.containsKey(Position.INTERN.name()));
        assertEquals(1, grouped.get(Position.MANAGER.name()).size());

        Map<String, Integer> counts = service.getPositionCounts();
        assertNotNull(counts);
        assertEquals(1, counts.get(Position.MANAGER.name()));
        assertEquals(1, counts.get(Position.TEAM_LEAD.name()));
        assertEquals(1, counts.get(Position.INTERN.name()));
    }

    @Test
    void getCompanyStatistics_returnsCorrectStats() {
        Map<String, CompanyStatistics> stats = service.getCompanyStatistics();
        assertNotNull(stats);
        CompanyStatistics abc = stats.get("ABC");
        assertNotNull(abc);
        assertEquals(2, abc.getTotalEmployees());
        assertEquals(10000.0, abc.getAverageSalary(), 0.0001); // (12000 + 8000) / 2
        assertEquals("Jan", abc.getHighestPaidEmployeeName());
        assertEquals("Kowalski", abc.getHighestPaidEmployeeSurname());

        CompanyStatistics xyz = stats.get("XYZ");
        assertNotNull(xyz);
        assertEquals(1, xyz.getTotalEmployees());
        assertEquals(3000.0, xyz.getAverageSalary(), 0.0001);
        assertEquals("Piotr", xyz.getHighestPaidEmployeeName());
    }


    @Test
    void setEmployees_setsNewArray() {
        Employee[] newEmployees = new Employee[]{e1};
        service.setEmployees(newEmployees);
        assertArrayEquals(newEmployees, service.getEmployees());
    }

    @Test
    void addEmployee_valid_addsSuccessfully() {
        Employee newEmp = new Employee("Marek", "Test", "marek.test@example.com", "NEW", Position.INTERN);
        service.addEmployee(newEmp);
        Employee[] employees = service.getEmployees();
        assertEquals(4, employees.length);
        assertTrue(Arrays.asList(employees).contains(newEmp));
    }

    @Test
    void getEmployeeByCompanyName_existingCompany_returnsEmployees() {
        Employee[] result = service.getEmployeeByCompanyName("ABC");
        assertNotNull(result);
        assertEquals(2, result.length);
        assertTrue(Arrays.stream(result).anyMatch(e -> e.getEmail().equalsIgnoreCase(e1.getEmail())));
        assertTrue(Arrays.stream(result).anyMatch(e -> e.getEmail().equalsIgnoreCase(e2.getEmail())));
    }

    @Test
    void getAverageSalary_withEmployees_returnsCorrectAverage() {
        double avg = service.getAverageSalary();
        double expected = (e1.getSalary() + e2.getSalary() + e3.getSalary()) / 3;
        assertEquals(expected, avg, 0.0001);
    }

    @Test
    void validateSalaryConsistency_withNullPosition_detectsInconsistency() {
        e3.setPosition(null);
        List<Employee> inconsistent = service.validateSalaryConsistency();
        assertNotNull(inconsistent);
        assertTrue(inconsistent.stream().anyMatch(emp -> emp.getEmail().equalsIgnoreCase(e3.getEmail())));
    }

    @Test
    void getCompanyStatistics_empty_returnsEmptyMap() {
        EmployeeService empty = new EmployeeService(new Employee[0]);
        Map<String, CompanyStatistics> stats = empty.getCompanyStatistics();
        assertNotNull(stats);
        assertTrue(stats.isEmpty());
    }

// Dodaj na końcu klasy EmployeeServiceTest

    @Test
    void getEmployeesSortedByLastName_empty_returnsEmptyArray() {
        EmployeeService empty = new EmployeeService(new Employee[0]);
        Employee[] sorted = empty.getEmployeesSortedByLastName();
        assertNotNull(sorted);
        assertEquals(0, sorted.length);
    }

    @Test
    void getEmployeesGroupedByPosition_empty_returnsEmptyMap() {
        EmployeeService empty = new EmployeeService(new Employee[0]);
        Map<String, List<Employee>> grouped = empty.getEmployeesGroupedByPosition();
        assertNotNull(grouped);
        assertTrue(grouped.isEmpty());
    }

    @Test
    void getPositionCounts_empty_returnsEmptyMap() {
        EmployeeService empty = new EmployeeService(new Employee[0]);
        Map<String, Integer> counts = empty.getPositionCounts();
        assertNotNull(counts);
        assertTrue(counts.isEmpty());
    }

    @Test
    void displayAllEmployees_empty_printsNoEmployees() {
        EmployeeService empty = new EmployeeService(new Employee[0]);
        // Metoda drukuje do System.out, więc test sprawdza, czy nie rzuca wyjątku
        assertDoesNotThrow(() -> empty.displayAllEmployees());
    }

    @Test
    void getEmployeeByCompanyName_emptyEmployees_returnsEmptyArray() {
        EmployeeService empty = new EmployeeService(new Employee[0]);
        Employee[] result = empty.getEmployeeByCompanyName("ABC");
        assertNotNull(result);
        assertEquals(0, result.length);
    }


}
