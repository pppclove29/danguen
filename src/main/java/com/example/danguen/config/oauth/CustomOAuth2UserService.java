package com.example.danguen.config.oauth;

import com.example.danguen.domain.model.user.User;
import com.example.danguen.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Value("${file.user.image.path}")
    private String savePath;
    private final UserRepository userRepository;

    // oAuth 로그인 버튼 클릭 후 자동 호출, 세션에 저장할 값 반환
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");

        // save & update
        User user = userRepository.findByEmail(email)
                .map(entity -> entity.updateOAuth(name, email))
                .orElse(User.builder()
                        .name(name)
                        .email(email)
                        .build());

        userRepository.save(user);

        if (user.getImage() == null) {
            String imageUrl = oAuth2User.getAttribute("picture");
            System.out.println(imageUrl);
            System.out.println(savePath);

            try {
                URL url = new URL(Objects.requireNonNull(imageUrl));

                BufferedImage image = ImageIO.read(url);
                File file = new File(savePath + user.getName() + ".jpg");

                ImageIO.write(image, "jpg", file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //UserImage userImage = UserImage.builder()
            //        .name().build();
        }

        return new PrincipalUserDetails(user, oAuth2User);
    }
}