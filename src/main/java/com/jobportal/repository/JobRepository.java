package com.jobportal.repository;

import com.jobportal.model.Job;
import com.jobportal.model.JobType;
import com.jobportal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    /**
     * Search active jobs. Empty keyword / location match everything; a null job type means "any type".
     */
    @Query("SELECT j FROM Job j WHERE j.active = true "
            + "AND (LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "     OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "     OR LOWER(j.skills) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
            + "AND LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')) "
            + "AND (:jobType IS NULL OR j.jobType = :jobType)")
    Page<Job> search(@Param("keyword") String keyword,
                     @Param("location") String location,
                     @Param("jobType") JobType jobType,
                     Pageable pageable);

    List<Job> findByEmployerOrderByPostedAtDesc(User employer);

    List<Job> findTop10ByOrderByPostedAtDesc();

    long countByActiveTrue();

    long countByEmployer(User employer);
}
