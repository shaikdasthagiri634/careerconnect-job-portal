package com.jobportal.service;

import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.ApplicationStatus;
import com.jobportal.model.Job;
import com.jobportal.model.JobApplication;
import com.jobportal.model.User;
import com.jobportal.repository.JobApplicationRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobService jobService;

    public ApplicationService(JobApplicationRepository applicationRepository, JobService jobService) {
        this.applicationRepository = applicationRepository;
        this.jobService = jobService;
    }

    public JobApplication apply(Long jobId, User candidate, String coverLetter) {
        Job job = jobService.getById(jobId);
        if (!job.isActive()) {
            throw new IllegalStateException("This job is no longer accepting applications");
        }
        if (applicationRepository.existsByJobAndCandidate(job, candidate)) {
            throw new IllegalStateException("You have already applied for this job");
        }
        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setCandidate(candidate);
        application.setCoverLetter(coverLetter == null ? null : coverLetter.trim());
        application.setStatus(ApplicationStatus.APPLIED);
        return applicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public boolean hasApplied(Job job, User candidate) {
        return applicationRepository.existsByJobAndCandidate(job, candidate);
    }

    @Transactional(readOnly = true)
    public List<JobApplication> forCandidate(User candidate) {
        return applicationRepository.findByCandidateOrderByAppliedAtDesc(candidate);
    }

    /** Applicants for a job; only the employer who posted it may see them. */
    @Transactional(readOnly = true)
    public List<JobApplication> forJob(Long jobId, User employer) {
        Job job = jobService.getOwnedJob(jobId, employer);
        return applicationRepository.findByJobOrderByAppliedAtDesc(job);
    }

    public JobApplication updateStatus(Long applicationId, ApplicationStatus status, User employer) {
        JobApplication application = getById(applicationId);
        if (!application.getJob().getEmployer().getId().equals(employer.getId())) {
            throw new AccessDeniedException("You can only update applications for your own jobs");
        }
        application.setStatus(status);
        application.setUpdatedAt(LocalDateTime.now());
        return applicationRepository.save(application);
    }

    /** A candidate can withdraw their own application. */
    public void withdraw(Long applicationId, User candidate) {
        JobApplication application = getById(applicationId);
        if (!application.getCandidate().getId().equals(candidate.getId())) {
            throw new AccessDeniedException("You can only withdraw your own applications");
        }
        applicationRepository.delete(application);
    }

    @Transactional(readOnly = true)
    public JobApplication getById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id " + id));
    }

    /** Number of applicants per job id, in the order of the given jobs. */
    @Transactional(readOnly = true)
    public Map<Long, Long> applicantCounts(List<Job> jobs) {
        Map<Long, Long> counts = new LinkedHashMap<>();
        for (Job job : jobs) {
            counts.put(job.getId(), applicationRepository.countByJob(job));
        }
        return counts;
    }

    @Transactional(readOnly = true)
    public long countForEmployer(User employer) {
        return applicationRepository.countByJobEmployer(employer);
    }

    @Transactional(readOnly = true)
    public long count() {
        return applicationRepository.count();
    }

    @Transactional(readOnly = true)
    public Map<ApplicationStatus, Long> countByStatus() {
        Map<ApplicationStatus, Long> counts = new LinkedHashMap<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            counts.put(status, applicationRepository.countByStatus(status));
        }
        return counts;
    }
}
