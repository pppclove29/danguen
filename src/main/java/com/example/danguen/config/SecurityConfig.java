package com.example.danguen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import com.example.danguen.config.jwt.JwtAuthorizationFilter;
import com.example.danguen.config.oauth.CustomAuthenticationFailureHandler;
import com.example.danguen.config.oauth.CustomAuthenticationSuccessHandler;
import com.example.danguen.config.oauth.CustomOAuth2UserService;
import com.example.danguen.handler.CustomAccessDeniedHandler;
import com.example.danguen.handler.CustomAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
	private final CustomOAuth2UserService customOAuth2UserService;

	private final JwtAuthorizationFilter jwtAuthorizationFilter;

	private final String[] permitUri = { "/test", "/index", "/css/**", "/images/**", "/js/**", "/h2-console/**",
			"/profile", "/favicon.ico", "/resources/**", "/error", "/public/**" };

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults());
		http.csrf(AbstractHttpConfigurer::disable);
		http.headers(config -> config.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
		http.formLogin(AbstractHttpConfigurer::disable);
		http.httpBasic(AbstractHttpConfigurer::disable);
		http.sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.authorizeHttpRequests(config -> config
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers(permitUri).permitAll()
				.anyRequest().authenticated());

		http.addFilterBefore(jwtAuthorizationFilter, AuthorizationFilter.class);

		http.logout(config -> config.logoutSuccessUrl("/"));

		http.exceptionHandling(config -> config
				.accessDeniedHandler(new CustomAccessDeniedHandler())
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

		http.oauth2Login(config -> config
				.successHandler(new CustomAuthenticationSuccessHandler())
				.failureHandler(new CustomAuthenticationFailureHandler())
				.userInfoEndpoint(endpoint -> endpoint
						.userService(customOAuth2UserService)));

		return http.build();
	}
}
