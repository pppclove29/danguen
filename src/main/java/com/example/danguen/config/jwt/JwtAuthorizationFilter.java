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
        System.out.println("jwt Authorization Filter");

        String jwtHeader = request.getHeader(JwtProperties.HEADER);

        System.out.println("header = " + jwtHeader);

        if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.PREFIX)) {
            System.out.println("유효하지 않은 토큰");
            chain.doFilter(request, response);
            return;
        }
        System.out.println("유효한 토큰");

        String jwtToken = Objects.requireNonNull(jwtHeader).replace(JwtProperties.PREFIX, "");

        Long userId = Long.valueOf((JWT.require(JwtProperties.ALGORITHM).build()).verify(jwtToken).getClaim("userId").toString());

        System.out.println("userID : " + userId);

        User user = userService.getUserById(userId);

        System.out.println("user role : " + user.getRole());
        System.out.println("user naem : " + user.getName());
        PrincipalUserDetails principalUserDetails = new PrincipalUserDetails(user);

        System.out.println("권한 size " + principalUserDetails.getAuthorities().size());

        for (var a : principalUserDetails.getAuthorities()) {
            System.out.println("권한 내용 " + a);
        }
        Authentication authentication
                = new JwtAuthenticationToken(jwtToken, principalUserDetails, principalUserDetails.getAuthorities());

        Authentication pastAuthentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("이전 필터의 인증 " + pastAuthentication);
        System.out.println("이전 필터 인증 내용 "+pastAuthentication.getPrincipal());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("변경 이후 필터의 인증 " + currentAuthentication);
        System.out.println("변경 이후 필터 인증 내용 "+currentAuthentication.getPrincipal());

        System.out.println("--------정상적으로 유저 인증정보 저장--------");
        chain.doFilter(request, response);
    }
}
