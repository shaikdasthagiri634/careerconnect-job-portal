package com.jobportal.web;

import com.jobportal.model.Role;
import com.jobportal.model.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    public AdminController(UserService userService, JobService jobService, ApplicationService applicationService) {
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.count());
        model.addAttribute("employers", userService.countByRole(Role.EMPLOYER));
        model.addAttribute("candidates", userService.countByRole(Role.CANDIDATE));
        model.addAttribute("totalJobs", jobService.count());
        model.addAttribute("activeJobs", jobService.countActive());
        model.addAttribute("totalApplications", applicationService.count());
        model.addAttribute("statusCounts", applicationService.countByStatus());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("recentJobs", jobService.recentJobs());
        return "admin/dashboard";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        try {
            User user = userService.toggleEnabled(id, userService.getByEmail(principal.getName()));
            redirect.addFlashAttribute("success",
                    user.getFullName() + (user.isEnabled() ? " has been activated." : " has been blocked."));
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        jobService.delete(id, userService.getByEmail(principal.getName()));
        redirect.addFlashAttribute("success", "Job removed.");
        return "redirect:/admin/dashboard";
    }
}
