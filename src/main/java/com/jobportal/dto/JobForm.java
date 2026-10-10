package com.jobportal.dto;

import com.jobportal.model.Job;
import com.jobportal.model.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Create / edit job data, used by both the employer web form and the REST API. */
public class JobForm {

    @NotBlank(message = "Job title is required")
    @Size(max = 150)
    private String title;

    @NotBlank(message = "Company name is required")
    @Size(max = 150)
    private String companyName;

    @NotBlank(message = "Location is required")
    @Size(max = 100)
    private String location;

    @NotNull(message = "Job type is required")
    private JobType jobType;

    @Size(max = 50)
    private String salary;

    @Size(max = 50)
    private String experience;

    @Size(max = 500)
    private String skills;

    @NotBlank(message = "Description is required")
    @Size(max = 4000, message = "Description must be at most 4000 characters")
    private String description;

    public static JobForm fromJob(Job job) {
        JobForm form = new JobForm();
        form.setTitle(job.getTitle());
        form.setCompanyName(job.getCompanyName());
        form.setLocation(job.getLocation());
        form.setJobType(job.getJobType());
        form.setSalary(job.getSalary());
        form.setExperience(job.getExperience());
        form.setSkills(job.getSkills());
        form.setDescription(job.getDescription());
        return form;
    }

    public void applyTo(Job job) {
        job.setTitle(title.trim());
        job.setCompanyName(companyName.trim());
        job.setLocation(location.trim());
        job.setJobType(jobType);
        job.setSalary(blankToNull(salary));
        job.setExperience(blankToNull(experience));
        job.setSkills(blankToNull(skills));
        job.setDescription(description.trim());
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public JobType getJobType() { return jobType; }
    public void setJobType(JobType jobType) { this.jobType = jobType; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
