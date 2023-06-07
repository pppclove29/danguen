package com.example.danguen.config;

import com.example.danguen.argumentResolver.SessionUserIdArgumentResolver;
import com.example.danguen.interceptor.ArticlePostAuthInterceptor;
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
    private final ArticlePostAuthInterceptor articlePostAuthInterceptor;
    @Value("${file.article.image.local}")
    private String articlePath;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(sessionUserIdArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(articlePostAuthInterceptor)
                .addPathPatterns("/secured/article/*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/src/main/resources/articleImage/**")
                .addResourceLocations(articlePath);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                //.allowCredentials(true) todo postman 요청 주소 허락
                .maxAge(3600);
    }
}
