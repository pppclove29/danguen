package com.example.danguen.config.oauth;

import com.auth0.jwt.JWT;
import com.example.danguen.config.jwt.JwtProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        PrincipalUserDetails principalUserDetails = ((PrincipalUserDetails) authentication.getPrincipal());

        String jwtValue = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("userName",  principalUserDetails.getName())
                .withClaim("userId",    principalUserDetails.getUserId())
                .sign(JwtProperties.ALGORITHM);

        response.addHeader(JwtProperties.HEADER, JwtProperties.PREFIX + jwtValue);
    }
}
