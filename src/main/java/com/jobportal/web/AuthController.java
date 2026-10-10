package com.jobportal.web;

import com.jobportal.dto.RegisterForm;
import com.jobportal.model.Role;
import com.jobportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Principal principal) {
        if (principal != null) {
            return "redirect:/dashboard";
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(@RequestParam(name = "role", required = false) Role role, Model model) {
        RegisterForm form = new RegisterForm();
        if (role == Role.EMPLOYER) {
            form.setRole(Role.EMPLOYER);
        }
        model.addAttribute("form", form);
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult result,
                           RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.register(form);
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "email.invalid", e.getMessage());
            return "auth/register";
        }
        redirect.addFlashAttribute("success", "Account created. Please log in.");
        return "redirect:/login";
    }
}
