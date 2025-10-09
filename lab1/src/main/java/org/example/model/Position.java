package org.example.model;

public enum Position {
    PRESIDENT(25000, 5),
    VICE_PRESIDENT(18000, 4),
    MANAGER(12000, 3),
    TEAM_LEAD(8000, 2),
    INTERN(3000, 1);

    private double baseSalary;
    private int hierarchyLevel;

    public double getBaseSalary() {
        return baseSalary;
    }

    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    Position(double baseSalary, int hierarchyLevel) {
        this.baseSalary = baseSalary;
        this.hierarchyLevel = hierarchyLevel;
    }
}
