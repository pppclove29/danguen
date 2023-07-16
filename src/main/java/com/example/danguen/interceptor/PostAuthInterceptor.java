package com.example.danguen.interceptor;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.danguen.domain.post.service.PostServiceImpl;
import com.example.danguen.domain.user.service.UserServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PostAuthInterceptor implements HandlerInterceptor, CustomPathCheckInterceptor {
	private final UserServiceImpl userService;
	private final PostServiceImpl postServiceImpl;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (handler instanceof HandlerMethod) {
			String method = request.getMethod();

			if (HttpMethod.PUT.matches(method) || HttpMethod.DELETE.matches(method)) {
				Long postId = getIdFromPath(request.getRequestURI(), (path) -> Long.parseLong(
						new AntPathMatcher().extractUriTemplateVariables("/secured/post/{post}/{id}", path).get("id")));

				boolean hasAuth = userService.isUsersCreation(() -> postServiceImpl.getPostById(postId));

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
