package com.jobportal.dto;

import com.jobportal.model.Job;
import com.jobportal.model.JobApplication;
import com.jobportal.model.User;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * JSON request/response shapes for the REST API. Entities are never returned directly,
 * so passwords and internal relations are never exposed.
 */
public final class ApiDtos {

    private ApiDtos() {
    }

    public record UserResponse(Long id, String fullName, String email, String role,
                               String phone, String headline, boolean enabled, LocalDateTime createdAt) {
        public static UserResponse from(User u) {
            return new UserResponse(u.getId(), u.getFullName(), u.getEmail(), u.getRole().name(),
                    u.getPhone(), u.getHeadline(), u.isEnabled(), u.getCreatedAt());
        }
    }

    public record JobResponse(Long id, String title, String companyName, String location, String jobType,
                              String salary, String experience, String skills, String description,
                              boolean active, LocalDateTime postedAt, Long employerId, String postedBy) {
        public static JobResponse from(Job j) {
            return new JobResponse(j.getId(), j.getTitle(), j.getCompanyName(), j.getLocation(),
                    j.getJobType().name(), j.getSalary(), j.getExperience(), j.getSkills(), j.getDescription(),
                    j.isActive(), j.getPostedAt(), j.getEmployer().getId(), j.getEmployer().getFullName());
        }
    }

    public record ApplicationResponse(Long id, Long jobId, String jobTitle, String companyName,
                                      Long candidateId, String candidateName, String candidateEmail,
                                      String coverLetter, String status, LocalDateTime appliedAt) {
        public static ApplicationResponse from(JobApplication a) {
            return new ApplicationResponse(a.getId(), a.getJob().getId(), a.getJob().getTitle(),
                    a.getJob().getCompanyName(), a.getCandidate().getId(), a.getCandidate().getFullName(),
                    a.getCandidate().getEmail(), a.getCoverLetter(), a.getStatus().name(), a.getAppliedAt());
        }
    }

    public record ApplyRequest(@Size(max = 2000, message = "Cover letter must be at most 2000 characters")
                               String coverLetter) {
    }

    public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
        public static <E, T> PageResponse<T> from(Page<E> page, Function<E, T> mapper) {
            return new PageResponse<>(page.getContent().stream().map(mapper).toList(),
                    page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
        }
    }

    public record ApiError(LocalDateTime timestamp, int status, String error, String message,
                           Map<String, String> fieldErrors) {
    }
}
