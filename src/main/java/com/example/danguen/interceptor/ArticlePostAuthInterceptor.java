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
        //참 드럽다
        if (handler instanceof HandlerMethod) {
            Long postId = getPostIdFromPath(request.getRequestURI());

            boolean hasAuth = articleService.isUsersCreation(postId);

            if (!hasAuth) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return false;
            }
            return true;
        }
        return false;
    }

    private Long getPostIdFromPath(String path) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String postId = pathMatcher.extractUriTemplateVariables("/secured/article/{articleId}", path).get("articleId");

        return Long.parseLong(postId);
    }
}
