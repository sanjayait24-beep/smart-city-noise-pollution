package com.smartcity.noisemonitor.service;

import com.smartcity.noisemonitor.dto.LoginRequest;
import com.smartcity.noisemonitor.dto.LoginResponse;
import com.smartcity.noisemonitor.model.Admin;
import com.smartcity.noisemonitor.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final AdminRepository adminRepository;

    public AuthService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public LoginResponse authenticate(LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return new LoginResponse(false, null, null, null, null, "Username and password cannot be empty");
        }

        Optional<Admin> adminOpt = adminRepository.findByUsername(request.getUsername().trim());
        if (adminOpt.isEmpty()) {
            return new LoginResponse(false, null, null, null, null, "Invalid username or password");
        }

        Admin admin = adminOpt.get();
        // Support direct comparison or plain text check
        if (!admin.getPassword().equals(request.getPassword().trim())) {
            return new LoginResponse(false, null, null, null, null, "Invalid username or password");
        }

        String simulatedToken = "ADM-" + UUID.randomUUID().toString();
        return new LoginResponse(true, admin.getUsername(), admin.getFullName(), admin.getRole(), simulatedToken, "Login successful");
    }
}
