package com.healthcare.claims.controller;

import com.healthcare.claims.dto.AuthDto;
import com.healthcare.claims.model.User;
import com.healthcare.claims.repository.UserRepository;
import com.healthcare.claims.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "JWT authentication endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public ResponseEntity<AuthDto.LoginResponse> login(
        @Valid @RequestBody AuthDto.LoginRequest request
    ) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = (User) auth.getPrincipal();
        String token = jwtService.generateToken(user);

        log.info("User logged in: {}", user.getUsername());

        return ResponseEntity.ok(AuthDto.LoginResponse.builder()
            .token(token)
            .username(user.getUsername())
            .role(user.getRole().name())
            .expiresIn(jwtService.getExpirationTime())
            .build());
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<String> register(
        @Valid @RequestBody AuthDto.RegisterRequest request
    ) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User.Role role = request.getRole() != null
            ? User.Role.valueOf(request.getRole().toUpperCase())
            : User.Role.CLAIMS_PROCESSOR;

        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .email(request.getEmail())
            .role(role)
            .build();

        userRepository.save(user);
        log.info("New user registered: {}", user.getUsername());

        return ResponseEntity.ok("User registered successfully");
    }
}
