package com.jobportal.web;

import com.jobportal.model.Job;
import com.jobportal.model.JobType;
import com.jobportal.model.Role;
import com.jobportal.model.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class HomeController {

    private static final int PAGE_SIZE = 8;

    private final JobService jobService;
    private final ApplicationService applicationService;
    private final UserService userService;

    public HomeController(JobService jobService, ApplicationService applicationService, UserService userService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("latestJobs", jobService.latest(6));
        model.addAttribute("activeJobs", jobService.countActive());
        model.addAttribute("companies", userService.countByRole(Role.EMPLOYER));
        model.addAttribute("candidates", userService.countByRole(Role.CANDIDATE));
        return "index";
    }

    @GetMapping("/jobs")
    public String jobs(@RequestParam(name = "keyword", required = false) String keyword,
                       @RequestParam(name = "location", required = false) String location,
                       @RequestParam(name = "type", required = false) JobType type,
                       @RequestParam(name = "page", defaultValue = "0") int page,
                       Model model) {
        Page<Job> jobs = jobService.search(keyword, location, type, page, PAGE_SIZE);
        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("type", type);
        return "jobs/list";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable("id") Long id, Principal principal, Model model) {
        Job job = jobService.getById(id);
        model.addAttribute("job", job);
        boolean applied = false;
        if (principal != null) {
            User user = userService.getByEmail(principal.getName());
            if (user.getRole() == Role.CANDIDATE) {
                applied = applicationService.hasApplied(job, user);
            }
        }
        model.addAttribute("alreadyApplied", applied);
        return "jobs/detail";
    }

    @PostMapping("/jobs/{id}/apply")
    public String apply(@PathVariable("id") Long id,
                        @RequestParam(name = "coverLetter", required = false) String coverLetter,
                        Principal principal,
                        RedirectAttributes redirect) {
        User candidate = userService.getByEmail(principal.getName());
        if (coverLetter != null && coverLetter.length() > 2000) {
            redirect.addFlashAttribute("error", "Cover letter must be at most 2000 characters");
            return "redirect:/jobs/" + id;
        }
        try {
            applicationService.apply(id, candidate, coverLetter);
            redirect.addFlashAttribute("success", "Application submitted. Good luck!");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/jobs/" + id;
    }

    /** After login every user lands here and is sent to the dashboard for their role. */
    @GetMapping("/dashboard")
    public String dashboard(Principal principal) {
        User user = userService.getByEmail(principal.getName());
        return switch (user.getRole()) {
            case ADMIN -> "redirect:/admin/dashboard";
            case EMPLOYER -> "redirect:/employer/dashboard";
            case CANDIDATE -> "redirect:/candidate/applications";
        };
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
