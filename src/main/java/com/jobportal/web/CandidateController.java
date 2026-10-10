package com.jobportal.web;

import com.jobportal.model.User;
import com.jobportal.service.ApplicationService;
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
@RequestMapping("/candidate")
public class CandidateController {

    private final ApplicationService applicationService;
    private final UserService userService;

    public CandidateController(ApplicationService applicationService, UserService userService) {
        this.applicationService = applicationService;
        this.userService = userService;
    }

    @GetMapping("/applications")
    public String myApplications(Principal principal, Model model) {
        User candidate = userService.getByEmail(principal.getName());
        model.addAttribute("applications", applicationService.forCandidate(candidate));
        return "candidate/applications";
    }

    @PostMapping("/applications/{id}/withdraw")
    public String withdraw(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        applicationService.withdraw(id, userService.getByEmail(principal.getName()));
        redirect.addFlashAttribute("success", "Application withdrawn.");
        return "redirect:/candidate/applications";
    }
}
