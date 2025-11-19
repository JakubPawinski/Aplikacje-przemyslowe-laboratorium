package org.example.dto;

import org.example.enums.EmploymentStatus;
import org.example.model.Position;

public class EmployeeDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String company;
    private Position position;
    private Double salary;
    private String status;
        
    public EmployeeDTO() {
    }

    public EmployeeDTO(String firstName, String lastName, String email, String company, Position position, Double salary, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.company = company;
        this.position = position;
        this.salary = salary;
        this.status = status;
    }

    public EmployeeDTO(String name, String surname, String email, String companyName, Position position, double salary, EmploymentStatus status) {
    }

    // Gettery i settery
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
