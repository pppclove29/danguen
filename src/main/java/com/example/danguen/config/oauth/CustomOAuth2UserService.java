package com.example.danguen.config.oauth;

import com.example.danguen.domain.image.service.UserImageService;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserServiceImpl userService;
    private final UserImageService userImageService;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");
        String imageUrl = oAuth2User.getAttribute("picture");

        User user = userService.getUserByEmail(email)
                .orElseGet(() ->
                {
                    User newUser = userService.save(name, email);
                    userImageService.userImageSave(newUser, imageUrl);
                    return newUser;
                });


        return new PrincipalUserDetails(user);
    }
}