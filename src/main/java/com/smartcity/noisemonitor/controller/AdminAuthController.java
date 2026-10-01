package com.smartcity.noisemonitor.controller;

import com.smartcity.noisemonitor.dto.ApiResponse;
import com.smartcity.noisemonitor.dto.LoginRequest;
import com.smartcity.noisemonitor.dto.LoginResponse;
import com.smartcity.noisemonitor.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AdminAuthController {

    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.authenticate(loginRequest);
        if (response.isAuthenticated()) {
            return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
        } else {
            return ResponseEntity.status(401).body(ApiResponse.error(response.getMessage()));
        }
    }
}
