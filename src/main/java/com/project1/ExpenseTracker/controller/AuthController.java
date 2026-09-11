package com.project1.ExpenseTracker.controller;

import com.project1.ExpenseTracker.dto.LoginRequest;
import com.project1.ExpenseTracker.dto.LoginResponse;
import com.project1.ExpenseTracker.dto.RegisterRequest;
import com.project1.ExpenseTracker.dto.UserResponse;
import com.project1.ExpenseTracker.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "Authentication",
        description = "User registration and login APIs")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }



    @Operation(
            summary = "Register User",
            description = "Registers a new user in the Expense Tracker.")
    @PostMapping("/register")
    public UserResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return authService.register(request);
    }
    @Operation(
            summary = "Login User",
            description = "Authenticates a user and returns a JWT token.")
    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }
}
