package com.jobportal.repository;

import com.jobportal.model.ApplicationStatus;
import com.jobportal.model.Job;
import com.jobportal.model.JobApplication;
import com.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByJobAndCandidate(Job job, User candidate);

    List<JobApplication> findByCandidateOrderByAppliedAtDesc(User candidate);

    List<JobApplication> findByJobOrderByAppliedAtDesc(Job job);

    long countByJob(Job job);

    long countByStatus(ApplicationStatus status);

    long countByJobEmployer(User employer);

    void deleteByJob(Job job);

    void deleteByCandidate(User candidate);
}
