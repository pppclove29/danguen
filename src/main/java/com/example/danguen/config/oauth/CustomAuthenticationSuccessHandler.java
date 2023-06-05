package com.example.danguen.config.oauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.danguen.config.jwt.JwtProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //실제로는 프론트로 리다이렉트 후 추가 인증정보 입력받고 백으로 업데이트 요청
        System.out.println("after loaduser");
        PrincipalUserDetails principalUserDetails = ((PrincipalUserDetails) authentication.getPrincipal());

        String jwtValue = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME * 1000))
                .withClaim("userName", principalUserDetails.getUsername())
                .withClaim("userId", principalUserDetails.getUserId())
                .sign(JwtProperties.ALGORITHM);

        response.addHeader(JwtProperties.HEADER, JwtProperties.PREFIX + jwtValue);
    }
}
