package com.example.danguen.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;

public class AddressCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String url = request.getRequestURL().toString();

        // 너무 하드코딩이다 많이 보기 싫다
        url = url.replace("http://localhost/address","");

        String[] s = url.split("/"); // "", "city","street","zipcode"

        request.setAttribute("city",    Arrays.stream(s).count() > 1 ? URLDecoder.decode(s[1],"utf-8") : "");
        request.setAttribute("street",  Arrays.stream(s).count() > 2 ? URLDecoder.decode(s[2],"utf-8") : "");
        request.setAttribute("zipcode", Arrays.stream(s).count() > 3 ? URLDecoder.decode(s[3],"utf-8") : "");

        return true;
    }
}
