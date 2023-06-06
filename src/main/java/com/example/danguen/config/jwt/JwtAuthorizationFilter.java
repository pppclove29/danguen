package com.example.danguen.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.danguen.config.oauth.CustomOAuth2UserService;
import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

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

        Long userId = Long.valueOf((JWT.require(JwtProperties.ALGORITHM).build()).verify(jwtToken).getClaim("userId").toString());

        User user = userService.getUserById(userId);

        PrincipalUserDetails principalUserDetails = new PrincipalUserDetails(user);

        Authentication authentication
                = new JwtAuthenticationToken(jwtToken, principalUserDetails, principalUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
