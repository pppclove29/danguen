package com.example.danguen.config;

import com.example.danguen.argumentResolver.LoginUserNameArgumentResolver;
import com.example.danguen.argumentResolver.SessionUserIdArgumentResolver;
import com.example.danguen.interceptor.AddressCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final LoginUserNameArgumentResolver loginUserArgumentResolver;
    private final SessionUserIdArgumentResolver sessionUserIdArgumentResolver;
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
        resolvers.add(sessionUserIdArgumentResolver);
    }
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/test").setViewName("articleRegisterForm");
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AddressCheckInterceptor())
                .addPathPatterns("/address/**");
    }

}
