package com.jobportal.api;

import com.jobportal.dto.ApiDtos.UserResponse;
import com.jobportal.dto.RegisterForm;
import com.jobportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final UserService userService;

    public AuthApiController(UserService userService) {
        this.userService = userService;
    }

    /** Public: create an EMPLOYER or CANDIDATE account. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterForm form) {
        return UserResponse.from(userService.register(form));
    }

    /** Returns the account for the Basic Auth credentials sent with the request. */
    @GetMapping("/me")
    public UserResponse me(Principal principal) {
        return UserResponse.from(userService.getByEmail(principal.getName()));
    }
}
