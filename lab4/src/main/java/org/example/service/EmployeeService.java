package org.example.service;

import org.example.model.CompanyStatistics;
import org.example.model.Employee;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


@Service
public class EmployeeService {

    private Employee[] employees;

    public EmployeeService(Employee[] employees) {
        this.employees = employees != null ? employees.clone() : new Employee[0];
    }

    public Employee[] getEmployees() {
        return employees;
    }

    public void setEmployees(Employee[] employees) {
        this.employees = employees;
    }


    public void addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("employee is null");

        }
        for (Employee e : employees) {
            if (e != null && e.equals(employee)) {
                throw new IllegalArgumentException("employee already exists");
            }
        }
        Employee[] newEmployees = new Employee[employees.length + 1];

        System.arraycopy(employees, 0, newEmployees, 0, employees.length);

        newEmployees[newEmployees.length - 1] = employee;

        employees = newEmployees;
        System.out.println("Employee added successfully");
    }

    public void displayAllEmployees() {

        if (employees == null || employees.length == 0) {
            System.out.println("No employees to display.");
            return;
        }

        System.out.println("List of all employees:");
        for (Employee e : employees) {
            if (e != null) {
                System.out.println(e);
            }
        }
    }

    public Employee[] getEmployeeByCompanyName(String companyName) {
        if (companyName == null || companyName.isEmpty()) {
            throw new IllegalArgumentException("companyName is null or empty");
        }

        if (employees == null || employees.length == 0) {
            System.out.println("No employees to search.");
            return new Employee[0];
        }
        return Arrays.stream(employees).filter(e -> e != null && e.getCompanyName().equalsIgnoreCase(companyName)).toArray(Employee[]::new);
    }

    public Employee[] getEmployeesSortedByLastName() {
        if (employees == null || employees.length == 0) {
            System.out.println("No employees to sort.");
            return new Employee[0];
        }
        Comparator<Employee> bySurname = Comparator.comparing(Employee::getSurname, String.CASE_INSENSITIVE_ORDER);
        return Arrays.stream(employees).filter(Objects::nonNull).sorted(bySurname).toArray(Employee[]::new);
    }

    public Map<String, List<Employee>> getEmployeesGroupedByPosition() {
        if (employees == null || employees.length == 0) {
            System.out.println("No employees to group.");
            return Collections.emptyMap();
        }
        return Arrays.stream(employees)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(e -> e.getPosition().name()));
    }

    public Map<String, Integer> getPositionCounts() {
        if (employees == null || employees.length == 0) {
            System.out.println("No employees to count.");
            return Collections.emptyMap();
        }
        Map<String, Integer> counts = new HashMap<>();
        for (Employee e : employees) {
            if (e != null) {
                String position = e.getPosition().name();
                counts.put(position, counts.getOrDefault(position, 0) + 1);
            }
        }
        return counts;
    }

    public double getAverageSalary() {
        if (employees == null || employees.length == 0) {
            System.out.println("No employees to calculate average salary.");
            return 0.0;
        }
        double totalSalary = 0.0;
        int count = 0;
        for (Employee e : employees) {
            if (e != null) {
                totalSalary += e.getSalary();
                count++;
            }
        }
        return count == 0 ? 0.0 : totalSalary / count;
    }

    public Optional<Employee> getHighestPaidEmployee() {
        if (employees == null || employees.length == 0) {
            System.out.println("No employees to find highest paid.");
            return Optional.empty();
        }
        return Arrays.stream(employees)
                .filter(Objects::nonNull)
                .max(Comparator.comparingDouble(Employee::getSalary));
    }

    public List<Employee> validateSalaryConsistency() {
        if (employees == null || employees.length == 0) {
            System.out.println("No employees to validate salary consistency.");
            return Collections.emptyList();
        }

        return Arrays.stream(employees)
                .filter(Objects::nonNull)
                .filter(emp -> emp.getPosition() == null
                        || emp.getSalary() < emp.getPosition().getBaseSalary())
                .collect(Collectors.toList());
    }


    public Map<String, CompanyStatistics> getCompanyStatistics(){
        if (employees == null || employees.length == 0) {
            System.out.println("No employees to calculate statistics.");
            return Collections.emptyMap();
        }
        return Arrays.stream(employees)
                .filter(Objects::nonNull)
                .filter(e -> e.getCompanyName() != null && !e.getCompanyName().isEmpty())
                .collect(Collectors.groupingBy(
                        Employee::getCompanyName,
                        Collectors.collectingAndThen(Collectors.toList(), empList -> {
                            int count = empList.size();
                            double avgSalary = empList.stream().mapToDouble(Employee::getSalary).average().orElse(0.0);
                            Employee highestPaid = empList.stream().max(Comparator.comparingDouble(Employee::getSalary)).get();
                            return new CompanyStatistics(count, avgSalary, highestPaid.getName(), highestPaid.getSurname());
                        })));

    }
}
