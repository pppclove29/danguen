package com.example.danguen.config.jwt;

import com.auth0.jwt.JWT;
import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final UserServiceImpl userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwtHeader = request.getHeader(JwtProperties.HEADER);

        if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        String jwtToken = jwtHeader.replace(JwtProperties.PREFIX, "");

        //todo 토큰 만료시 에러표출
        Long userId = Long.valueOf((JWT.require(JwtProperties.ALGORITHM).build()).verify(jwtToken).getClaim("userId").toString());

        User user = userService.getUserById(userId);

        PrincipalUserDetails principalUserDetails = new PrincipalUserDetails(user);

        Authentication authentication
                = new JwtAuthenticationToken(jwtToken, principalUserDetails, principalUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
