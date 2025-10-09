package org.example;

import org.example.modules.Employee;
import org.example.model.Position;
import org.example.service.EmployeeService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        Employee emp1 = new Employee("Jan", "Kowalski", "jan.kowalski@example.com", "ABC", Position.MANAGER);
        emp1.setPosition(Position.MANAGER);

        Employee emp2 = new Employee("Anna", "Nowak", "anna.nowak@example.com", "XYZ", Position.TEAM_LEAD);
        emp2.setPosition(Position.TEAM_LEAD);

        Employee emp3 = new Employee("Piotr", "Zieliński", "piotr.zielinski@example.com", "ABC", Position.PRESIDENT);
        emp3.setPosition(Position.PRESIDENT);


        Employee[] initialEmployees = new Employee[] { emp1, emp2 };
        EmployeeService employeeService = new EmployeeService(initialEmployees);

        //Display all employees
        employeeService.displayAllEmployees();

        // Add a new employee
        employeeService.addEmployee(emp3);

        // Display all employees again to see the new addition
        employeeService.displayAllEmployees();

        // Display ABC company employees
        System.out.println("\n ABC Company Employees:");

        Employee[] abcEmployees = employeeService.getEmployeeByCompanyName("ABC");
        for (Employee e : abcEmployees) {
            System.out.println(e);
        }

        // Display employees sorted by surname
        System.out.println("\n Employees sorted by surname:");
        Employee[] sortedBySurname = employeeService.getEmployeesSortedByLastName();
        for (Employee e : sortedBySurname) {
            System.out.println(e);
        }

        // Display employees grouped by position
        System.out.println("\n Employees grouped by position:");
        Map<String, List<Employee>> groupedByPosition = employeeService.getEmployeesGroupedByPosition();
        for (Map.Entry<String, List<Employee>> entry : groupedByPosition.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // Display number of employees by position
        System.out.println("\n Employees number by position:");
        Map<String, Integer> positionCounts = employeeService.getPositionCounts();
        for (Map.Entry<String, Integer> entry : positionCounts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // Display average salary
        System.out.println("\n Average salary: " + employeeService.getAverageSalary());

        // Display highest salary employee
        System.out.println("\n Highest salary employee:");
        System.out.println(employeeService.getHighestPaidEmployee());
    }
}
