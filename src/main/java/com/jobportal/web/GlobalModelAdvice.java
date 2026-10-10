package com.jobportal.web;

import com.jobportal.model.JobType;
import com.jobportal.model.User;
import com.jobportal.repository.UserRepository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

/** Adds values every page needs (logged-in user, job types for the search filter). */
@ControllerAdvice(basePackages = "com.jobportal.web")
public class GlobalModelAdvice {

    private final UserRepository userRepository;

    public GlobalModelAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute("currentUser")
    public User currentUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        return userRepository.findByEmailIgnoreCase(principal.getName()).orElse(null);
    }

    @ModelAttribute("jobTypes")
    public JobType[] jobTypes() {
        return JobType.values();
    }
}
