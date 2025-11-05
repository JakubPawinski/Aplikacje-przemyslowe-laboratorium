package org.example.model;

public class CompanyStatistics {
    private final int totalEmployees;

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public double getAverageSalary() {
        return averageSalary;
    }

    public String getHighestPaidEmployeeName() {
        return highestPaidEmployeeName;
    }

    public String getHighestPaidEmployeeSurname() {
        return highestPaidEmployeeSurname;
    }

    private final double averageSalary;
    private final String highestPaidEmployeeName;
    private final String highestPaidEmployeeSurname;

    public CompanyStatistics(int totalEmployees, double averageSalary, String highestPaidEmployeeName, String highestPaidEmployeeSurname) {
        this.totalEmployees = totalEmployees;
        this.averageSalary = averageSalary;
        this.highestPaidEmployeeName = highestPaidEmployeeName;
        this.highestPaidEmployeeSurname = highestPaidEmployeeSurname;
    }

    @Override
    public String toString() {
        return "CompanyStatistics{" +
                "totalEmployees=" + totalEmployees +
                ", averageSalary=" + averageSalary +
                ", highestPaidEmployeeName='" + highestPaidEmployeeName + '\'' +
                ", highestPaidEmployeeSurname='" + highestPaidEmployeeSurname + '\'' +
                '}';
    }
}
