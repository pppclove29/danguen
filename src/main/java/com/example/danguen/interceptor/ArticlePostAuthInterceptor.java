package com.example.danguen.interceptor;

import com.example.danguen.domain.post.service.ArticleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Component
public class ArticlePostAuthInterceptor implements HandlerInterceptor {
    private final ArticleServiceImpl articleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            Long postId = getPostIdFromPath(request.getRequestURI());

            boolean hasAuth = articleService.isUsersCreation(postId);

            if (!hasAuth) {
                System.out.println("11");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return false;
            }
            System.out.println("22");
            return true;
        }
        System.out.println("33");
        return false;
    }

    private Long getPostIdFromPath(String path) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String postId = pathMatcher.extractUriTemplateVariables("/secured/article/{articleId}", path).get("articleId");

        return Long.parseLong(postId);
    }
}
