package com.jobportal.service;

import com.jobportal.dto.JobForm;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.Job;
import com.jobportal.model.JobType;
import com.jobportal.model.User;
import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class JobService {

    public static final int MAX_PAGE_SIZE = 50;

    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;

    public JobService(JobRepository jobRepository, JobApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional(readOnly = true)
    public Page<Job> search(String keyword, String location, JobType jobType, int page, int size) {
        String kw = keyword == null ? "" : keyword.trim();
        String loc = location == null ? "" : location.trim();
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Direction.DESC, "postedAt"));
        return jobRepository.search(kw, loc, jobType, pageable);
    }

    @Transactional(readOnly = true)
    public List<Job> latest(int count) {
        return search(null, null, null, 0, count).getContent();
    }

    @Transactional(readOnly = true)
    public Job getById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id " + id));
    }

    /** Loads a job and checks that the given employer posted it. */
    @Transactional(readOnly = true)
    public Job getOwnedJob(Long id, User employer) {
        Job job = getById(id);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new AccessDeniedException("You can only manage your own job postings");
        }
        return job;
    }

    @Transactional(readOnly = true)
    public List<Job> findByEmployer(User employer) {
        return jobRepository.findByEmployerOrderByPostedAtDesc(employer);
    }

    @Transactional(readOnly = true)
    public List<Job> recentJobs() {
        return jobRepository.findTop10ByOrderByPostedAtDesc();
    }

    public Job create(JobForm form, User employer) {
        Job job = new Job();
        form.applyTo(job);
        job.setEmployer(employer);
        job.setActive(true);
        return jobRepository.save(job);
    }

    public Job update(Long id, JobForm form, User employer) {
        Job job = getOwnedJob(id, employer);
        form.applyTo(job);
        return jobRepository.save(job);
    }

    /** Opens or closes a job for new applications. */
    public Job toggleActive(Long id, User employer) {
        Job job = getOwnedJob(id, employer);
        job.setActive(!job.isActive());
        return jobRepository.save(job);
    }

    /** The employer who posted the job, or an admin, can delete it (its applications go with it). */
    public void delete(Long id, User actor) {
        Job job = actor.isAdmin() ? getById(id) : getOwnedJob(id, actor);
        applicationRepository.deleteByJob(job);
        jobRepository.delete(job);
    }

    @Transactional(readOnly = true)
    public long count() {
        return jobRepository.count();
    }

    @Transactional(readOnly = true)
    public long countActive() {
        return jobRepository.countByActiveTrue();
    }

    @Transactional(readOnly = true)
    public long countByEmployer(User employer) {
        return jobRepository.countByEmployer(employer);
    }
}
