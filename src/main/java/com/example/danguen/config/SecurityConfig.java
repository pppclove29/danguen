package com.example.danguen.config;

import com.example.danguen.config.oauth.CustomAuthenticationFailureHandler;
import com.example.danguen.config.oauth.CustomAuthenticationSuccessHandler;
import com.example.danguen.config.oauth.CustomOAuth2UserService;
import com.example.danguen.config.jwt.JwtAuthorizationFilter;
import com.example.danguen.handler.CustomAccessDeniedHandler;
import com.example.danguen.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.persistence.Persistence;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    private final String[] permitUri = {
            "/auth", "/index", "/css/**", "/images/**", "/js/**", "/h2-console/**",
            "/profile", "/favicon.ico", "/resources/**", "/error"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .authorizeHttpRequests()
                .antMatchers(permitUri).permitAll()
                .antMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated();

        http
                .addFilterBefore(jwtAuthorizationFilter, FilterSecurityInterceptor.class);

        http
                .logout()
                .logoutSuccessUrl("/");

        http
                .exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());

        http
                .oauth2Login()
                .successHandler(new CustomAuthenticationSuccessHandler())
                .failureHandler(new CustomAuthenticationFailureHandler())
                .userInfoEndpoint()
                .userService(customOAuth2UserService);

        return http.build();
    }
}
