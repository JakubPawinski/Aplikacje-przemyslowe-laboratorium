package org.example.model;

public class CompanyStatistics {
    private  int totalEmployees;

    public CompanyStatistics() {

    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public void setAverageSalary(double averageSalary) {
        this.averageSalary = averageSalary;
    }

    public void setHighestPaidEmployeeName(String highestPaidEmployeeName) {
        this.highestPaidEmployeeName = highestPaidEmployeeName;
    }

    public void setHighestPaidEmployeeSurname(String highestPaidEmployeeSurname) {
        this.highestPaidEmployeeSurname = highestPaidEmployeeSurname;
    }

    private  double averageSalary;
    private  String highestPaidEmployeeName;
    private  String highestPaidEmployeeSurname;

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
