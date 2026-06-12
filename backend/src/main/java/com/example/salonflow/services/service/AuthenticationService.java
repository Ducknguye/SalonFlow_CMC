package com.example.salonflow.services.service;

import com.example.salonflow.dto.auth.*;
import com.example.salonflow.dto.common.MessageResponse;

public interface AuthenticationService {

    AuthResponse login(LoginRequest request);

    RegisterResponse register(RegisterRequest request);

    RefreshTokenResponse refreshToken(
            RefreshTokenRequest request);

    void logout(Long userId);

    void sendVerificationOtp(
            String email);

    MessageResponse verifyEmail(
            String email,
            String otp);

    void forgotPassword(
            String email);

    void resetPassword(
            String token,
            String newPassword);
}