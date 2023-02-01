package com.example.danguen.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CurrentPostIdCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 현재 댓글을 다려는 페이지는 무엇인지, 객체를 판단하고 id를 얻어와라

        return true;
    }
}
