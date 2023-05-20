package com.example.danguen.interceptor;

import com.example.danguen.domain.base.Address;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class AddressExtractInterceptor implements HandlerInterceptor {
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String url = request.getPathInfo();

        if(url.equals("/address")){
            return false;
        }

        request.setAttribute("address", extractAddress(url));

        return true;
    }

    private Address extractAddress(String path) {
        Map<String, String> pathVariables =
                pathMatcher.extractUriTemplateVariables("/address/{city}/{street}/{zipcode}", path);

        return new Address(
                pathVariables.get("city"),
                pathVariables.get("street"),
                pathVariables.get("zipcode")
        );
    }
}
