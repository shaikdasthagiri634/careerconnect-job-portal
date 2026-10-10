package com.jobportal.web;

import com.jobportal.dto.JobForm;
import com.jobportal.model.ApplicationStatus;
import com.jobportal.model.Job;
import com.jobportal.model.JobApplication;
import com.jobportal.model.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    private final JobService jobService;
    private final ApplicationService applicationService;
    private final UserService userService;

    public EmployerController(JobService jobService, ApplicationService applicationService, UserService userService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        User employer = me(principal);
        List<Job> jobs = jobService.findByEmployer(employer);
        model.addAttribute("myJobs", jobs);
        model.addAttribute("applicantCounts", applicationService.applicantCounts(jobs));
        model.addAttribute("totalJobs", jobs.size());
        model.addAttribute("openJobs", jobs.stream().filter(Job::isActive).count());
        model.addAttribute("totalApplicants", applicationService.countForEmployer(employer));
        return "employer/dashboard";
    }

    @GetMapping("/jobs/new")
    public String newJob(Principal principal, Model model) {
        JobForm form = new JobForm();
        form.setCompanyName(me(principal).getHeadline());
        model.addAttribute("jobForm", form);
        model.addAttribute("jobId", null);
        return "employer/job-form";
    }

    @PostMapping("/jobs")
    public String createJob(@Valid @ModelAttribute("jobForm") JobForm form, BindingResult result,
                            Principal principal, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("jobId", null);
            return "employer/job-form";
        }
        Job job = jobService.create(form, me(principal));
        redirect.addFlashAttribute("success", "Job \"" + job.getTitle() + "\" is now live.");
        return "redirect:/employer/dashboard";
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJob(@PathVariable("id") Long id, Principal principal, Model model) {
        Job job = jobService.getOwnedJob(id, me(principal));
        model.addAttribute("jobForm", JobForm.fromJob(job));
        model.addAttribute("jobId", job.getId());
        return "employer/job-form";
    }

    @PostMapping("/jobs/{id}")
    public String updateJob(@PathVariable("id") Long id, @Valid @ModelAttribute("jobForm") JobForm form,
                            BindingResult result, Principal principal, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("jobId", id);
            return "employer/job-form";
        }
        jobService.update(id, form, me(principal));
        redirect.addFlashAttribute("success", "Job updated.");
        return "redirect:/employer/dashboard";
    }

    @PostMapping("/jobs/{id}/toggle")
    public String toggleJob(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        Job job = jobService.toggleActive(id, me(principal));
        redirect.addFlashAttribute("success",
                job.isActive() ? "Job reopened for applications." : "Job closed for new applications.");
        return "redirect:/employer/dashboard";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        jobService.delete(id, me(principal));
        redirect.addFlashAttribute("success", "Job deleted.");
        return "redirect:/employer/dashboard";
    }

    @GetMapping("/jobs/{id}/applicants")
    public String applicants(@PathVariable("id") Long id, Principal principal, Model model) {
        User employer = me(principal);
        List<JobApplication> applications = applicationService.forJob(id, employer);
        model.addAttribute("job", jobService.getOwnedJob(id, employer));
        model.addAttribute("applications", applications);
        model.addAttribute("statuses", ApplicationStatus.values());
        return "employer/applicants";
    }

    @PostMapping("/applications/{id}/status")
    public String updateStatus(@PathVariable("id") Long id, @RequestParam("status") ApplicationStatus status,
                               Principal principal, RedirectAttributes redirect) {
        JobApplication application = applicationService.updateStatus(id, status, me(principal));
        redirect.addFlashAttribute("success", application.getCandidate().getFullName()
                + " marked as " + status.getLabel() + ".");
        return "redirect:/employer/jobs/" + application.getJob().getId() + "/applicants";
    }

    private User me(Principal principal) {
        return userService.getByEmail(principal.getName());
    }
}
