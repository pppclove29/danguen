package com.example.danguen.config.oauth;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //실제로는 프론트로 리다이렉트 후 추가 인증정보 입력받고 백으로 업데이트 요청

    }
}
