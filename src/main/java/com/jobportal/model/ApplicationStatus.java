package com.jobportal.model;

public enum ApplicationStatus {
    APPLIED("Applied", "secondary"),
    SHORTLISTED("Shortlisted", "info"),
    INTERVIEW("Interview", "primary"),
    HIRED("Hired", "success"),
    REJECTED("Rejected", "danger");

    private final String label;
    private final String badge;

    ApplicationStatus(String label, String badge) {
        this.label = label;
        this.badge = badge;
    }

    public String getLabel() {
        return label;
    }

    /** Bootstrap colour name used for the status badge in the UI. */
    public String getBadge() {
        return badge;
    }
}
