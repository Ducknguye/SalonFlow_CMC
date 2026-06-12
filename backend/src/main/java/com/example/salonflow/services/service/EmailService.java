package com.example.salonflow.services.service;

public interface EmailService {

    void sendVerificationOtp(
            String email,
            String otp
    );

    void sendResetPasswordEmail(
            String email,
            String resetLink
    );
}
