package com.example.salonflow.security.oauth;

import com.example.salonflow.dto.auth.AuthResponse;
import com.example.salonflow.services.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler
        implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    private final ObjectProvider<AuthenticationService> authenticationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) authentication;

        AuthResponse authResponse =
                authenticationService.getObject()
                        .loginWithOAuth2(
                        oauthToken.getAuthorizedClientRegistrationId(),
                        oauthToken.getPrincipal()
                );

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), authResponse);
    }
}
