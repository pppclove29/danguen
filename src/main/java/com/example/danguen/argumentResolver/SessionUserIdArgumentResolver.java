package com.example.danguen.argumentResolver;

import com.example.danguen.config.exception.UserNotFoundException;
import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class SessionUserIdArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean result = false;

        if (parameter.hasParameterAnnotation(SessionUserId.class))
            result = true;

        return result;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PrincipalUserDetails user = (PrincipalUserDetails) principal;

        if (user == null)
            throw new UserNotFoundException();

        Long id = userService.getUserIdByEmail(user.getUserEmail());

        return id;
    }
}
