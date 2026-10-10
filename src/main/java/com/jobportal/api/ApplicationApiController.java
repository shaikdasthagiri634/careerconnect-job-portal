package com.jobportal.api;

import com.jobportal.dto.ApiDtos.ApplicationResponse;
import com.jobportal.model.ApplicationStatus;
import com.jobportal.model.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationApiController {

    private final ApplicationService applicationService;
    private final UserService userService;

    public ApplicationApiController(ApplicationService applicationService, UserService userService) {
        this.applicationService = applicationService;
        this.userService = userService;
    }

    /** Candidate: my applications with their current status. */
    @GetMapping("/me")
    public List<ApplicationResponse> myApplications(Principal principal) {
        return applicationService.forCandidate(me(principal)).stream().map(ApplicationResponse::from).toList();
    }

    /** Candidate: withdraw one of my applications. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdraw(@PathVariable("id") Long id, Principal principal) {
        applicationService.withdraw(id, me(principal));
        return ResponseEntity.noContent().build();
    }

    /** Employer: move an applicant through the pipeline, e.g. ?status=SHORTLISTED */
    @PatchMapping("/{id}/status")
    public ApplicationResponse updateStatus(@PathVariable("id") Long id,
                                            @RequestParam("status") ApplicationStatus status,
                                            Principal principal) {
        return ApplicationResponse.from(applicationService.updateStatus(id, status, me(principal)));
    }

    private User me(Principal principal) {
        return userService.getByEmail(principal.getName());
    }
}
