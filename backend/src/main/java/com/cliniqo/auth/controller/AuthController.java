package com.cliniqo.auth.controller;

import com.cliniqo.auth.dto.AuthResponse;
import com.cliniqo.auth.dto.LoginRequest;
import com.cliniqo.auth.dto.LogoutRequest;
import com.cliniqo.auth.dto.RefreshRequest;
import com.cliniqo.auth.service.AuthService;
import com.cliniqo.common.dto.ApiResponse;
import com.cliniqo.config.RequestIdFilter;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request.getEmail(), request.getPassword()), RequestIdFilter.currentRequestId());
    }

    @PostMapping("/refresh")
    ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.success(authService.refresh(request.getRefreshToken()), RequestIdFilter.currentRequestId());
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody(required = false) LogoutRequest request) {
        authService.logout(request == null ? null : request.getRefreshToken());
        return ApiResponse.empty(RequestIdFilter.currentRequestId());
    }
}
