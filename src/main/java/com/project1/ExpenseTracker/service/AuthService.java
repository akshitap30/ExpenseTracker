package com.project1.ExpenseTracker.service;

import com.project1.ExpenseTracker.dto.LoginRequest;
import com.project1.ExpenseTracker.dto.LoginResponse;
import com.project1.ExpenseTracker.dto.RegisterRequest;
import com.project1.ExpenseTracker.dto.UserResponse;
import com.project1.ExpenseTracker.entity.User;
import com.project1.ExpenseTracker.repository.UserRepository;
import com.project1.ExpenseTracker.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final EmailService emailService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    // =====================================================
    // REGISTER USER
    // =====================================================

    public UserResponse register(RegisterRequest request) {

        logger.info(
                "Registration attempt for username: {}",
                request.getUsername()
        );

        User user = new User();

        user.setUsername(
                request.getUsername()
        );

        user.setEmail(
                request.getEmail()
        );

        logger.debug(
                "Encoding password for user: {}",
                request.getUsername()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        User savedUser =
                userRepository.save(user);

        logger.info(
                "User {} registered successfully.",
                savedUser.getUsername()
        );

        // Send welcome email
        emailService.sendWelcomeEmail(
                savedUser.getEmail(),
                savedUser.getUsername()
        );

        logger.info(
                "Welcome email process completed for user: {}",
                savedUser.getUsername()
        );

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    // =====================================================
    // LOGIN USER
    // =====================================================

    public LoginResponse login(
            LoginRequest request) {

        logger.info(
                "Login attempt for username: {}",
                request.getUsername()
        );

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        logger.info(
                "Authentication successful for: {}",
                request.getUsername()
        );

        String token =
                jwtService.generateToken(
                        request.getUsername()
                );

        logger.info(
                "JWT generated successfully for: {}",
                request.getUsername()
        );

        return new LoginResponse(token);
    }
}