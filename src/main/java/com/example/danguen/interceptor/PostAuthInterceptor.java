package com.example.danguen.interceptor;

import com.example.danguen.domain.post.service.PostService;
import com.example.danguen.domain.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Component
public class PostAuthInterceptor implements HandlerInterceptor, CustomPathCheckInterceptor {
    private final UserServiceImpl userService;
    private final PostService postService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            String method = request.getMethod();

            if (HttpMethod.PUT.matches(method) || HttpMethod.DELETE.matches(method)) {
                Long postId = getIdFromPath(request.getRequestURI(),
                        (path) ->
                                Long.parseLong(
                                        new AntPathMatcher().extractUriTemplateVariables(
                                                "/secured/article/{id}", path).get("id")
                                ) // todo 나중에 추가 post 하위 객체가 나오면 바꿔야할듯
                );

                boolean hasAuth = userService.isUsersCreation(
                        () -> postService.getPostById(postId)
                );

                if (!hasAuth) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
