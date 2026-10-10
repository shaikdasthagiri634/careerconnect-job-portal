package com.jobportal.api;

import com.jobportal.dto.ApiDtos.ApplicationResponse;
import com.jobportal.dto.ApiDtos.ApplyRequest;
import com.jobportal.dto.ApiDtos.JobResponse;
import com.jobportal.dto.ApiDtos.PageResponse;
import com.jobportal.dto.JobForm;
import com.jobportal.model.JobType;
import com.jobportal.model.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class JobApiController {

    private final JobService jobService;
    private final ApplicationService applicationService;
    private final UserService userService;

    public JobApiController(JobService jobService, ApplicationService applicationService, UserService userService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.userService = userService;
    }

    /** Public: search open jobs with paging. */
    @GetMapping("/jobs")
    public PageResponse<JobResponse> search(@RequestParam(name = "keyword", required = false) String keyword,
                                            @RequestParam(name = "location", required = false) String location,
                                            @RequestParam(name = "type", required = false) JobType type,
                                            @RequestParam(name = "page", defaultValue = "0") int page,
                                            @RequestParam(name = "size", defaultValue = "10") int size) {
        return PageResponse.from(jobService.search(keyword, location, type, page, size), JobResponse::from);
    }

    /** Public: one job by id. */
    @GetMapping("/jobs/{id}")
    public JobResponse get(@PathVariable("id") Long id) {
        return JobResponse.from(jobService.getById(id));
    }

    /** Employer: post a new job. */
    @PostMapping("/jobs")
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponse create(@Valid @RequestBody JobForm form, Principal principal) {
        return JobResponse.from(jobService.create(form, me(principal)));
    }

    /** Employer: update one of their jobs. */
    @PutMapping("/jobs/{id}")
    public JobResponse update(@PathVariable("id") Long id, @Valid @RequestBody JobForm form, Principal principal) {
        return JobResponse.from(jobService.update(id, form, me(principal)));
    }

    /** Employer: open / close a job for applications. */
    @PatchMapping("/jobs/{id}/toggle")
    public JobResponse toggle(@PathVariable("id") Long id, Principal principal) {
        return JobResponse.from(jobService.toggleActive(id, me(principal)));
    }

    /** Employer (own job) or admin: delete a job. */
    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id, Principal principal) {
        jobService.delete(id, me(principal));
        return ResponseEntity.noContent().build();
    }

    /** Employer: all jobs I posted (open and closed). */
    @GetMapping("/employer/jobs")
    public List<JobResponse> myJobs(Principal principal) {
        return jobService.findByEmployer(me(principal)).stream().map(JobResponse::from).toList();
    }

    /** Employer: applicants for one of my jobs. */
    @GetMapping("/jobs/{id}/applications")
    public List<ApplicationResponse> applicants(@PathVariable("id") Long id, Principal principal) {
        return applicationService.forJob(id, me(principal)).stream().map(ApplicationResponse::from).toList();
    }

    /** Candidate: apply to a job. Body is optional: {"coverLetter": "..."} */
    @PostMapping("/jobs/{id}/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse apply(@PathVariable("id") Long id,
                                     @Valid @RequestBody(required = false) ApplyRequest request,
                                     Principal principal) {
        String coverLetter = request == null ? null : request.coverLetter();
        return ApplicationResponse.from(applicationService.apply(id, me(principal), coverLetter));
    }

    private User me(Principal principal) {
        return userService.getByEmail(principal.getName());
    }
}
