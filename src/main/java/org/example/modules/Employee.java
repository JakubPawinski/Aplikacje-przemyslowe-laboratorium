package org.example.modules;

import org.example.model.Position;

public class Employee {
    private String name;
    private String surname;
    private String email;
    private String companyName;
    private Position position;
    private double salary;

    public Employee(String name, String surname, String email, String companyName, double salary) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.companyName = companyName;
        this.salary = 0.0;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
        if (position != null) {
            this.salary = position.getBaseSalary();
        }
    }

    @Override
    public String toString() {
        return "Employee{name='" + name + "', surname='" + surname + "', email='" + email + "', companyName='" + companyName + "', position=" + position + ", salary=" + salary + "}";
    }

    @Override
    public int hashCode() {
        return email == null ? 0 : email.toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Employee other = (Employee) obj;
        if (this.email == null) {
            return other.email == null;
        }
        return this.email.equalsIgnoreCase(other.email);
    }

}
