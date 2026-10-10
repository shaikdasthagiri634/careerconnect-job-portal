package com.jobportal.model;

public enum Role {
    ADMIN("Administrator"),
    EMPLOYER("Employer"),
    CANDIDATE("Job Seeker");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
