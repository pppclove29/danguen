package com.example.danguen.config;

import com.example.danguen.argumentResolver.SessionUserIdArgumentResolver;
import com.example.danguen.interceptor.PostAuthInterceptor;
import com.example.danguen.interceptor.CommentAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final SessionUserIdArgumentResolver sessionUserIdArgumentResolver;
    private final PostAuthInterceptor postAuthInterceptor;
    private final CommentAuthInterceptor commentAuthInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(sessionUserIdArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(postAuthInterceptor)
                .addPathPatterns(
                        "/secured/post/*");

        registry.addInterceptor(commentAuthInterceptor)
                .addPathPatterns("/secured/comment/*");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("/postman/**")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
