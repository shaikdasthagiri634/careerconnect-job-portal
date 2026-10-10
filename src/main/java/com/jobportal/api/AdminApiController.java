package com.jobportal.api;

import com.jobportal.dto.ApiDtos.UserResponse;
import com.jobportal.model.Role;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    public AdminApiController(UserService userService, JobService jobService, ApplicationService applicationService) {
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    /** Platform numbers for the admin. */
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers", userService.count());
        stats.put("employers", userService.countByRole(Role.EMPLOYER));
        stats.put("candidates", userService.countByRole(Role.CANDIDATE));
        stats.put("totalJobs", jobService.count());
        stats.put("activeJobs", jobService.countActive());
        stats.put("totalApplications", applicationService.count());
        stats.put("applicationsByStatus", applicationService.countByStatus());
        return stats;
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return userService.findAll().stream().map(UserResponse::from).toList();
    }

    /** Block or unblock a user account. */
    @PatchMapping("/users/{id}/toggle")
    public UserResponse toggleUser(@PathVariable("id") Long id, Principal principal) {
        return UserResponse.from(userService.toggleEnabled(id, userService.getByEmail(principal.getName())));
    }
}
