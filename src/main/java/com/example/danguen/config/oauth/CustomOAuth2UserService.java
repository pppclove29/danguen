package com.example.danguen.config.oauth;

import com.example.danguen.domain.repository.UserRepository;
import com.example.danguen.domain.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");
        String picture = oAuth2User.getAttribute("picture");

        User user = userRepository.findByEmail(email)
                .map(entity -> entity.updateOAuth(name,picture,email))
                .orElse(User.builder()
                        .name(name)
                        .email(email)
                        .picture(picture)
                        .build());

        userRepository.save(user);

        return new PrincipalUserDetails(user, oAuth2User);
    }
}