package com.example.danguen.domain.image.service;

import com.example.danguen.domain.image.dto.ImageDto;
import com.example.danguen.domain.image.entity.UserImage;
import com.example.danguen.domain.image.repository.UserImageRepository;
import com.example.danguen.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserImageService {
    private final UserImageRepository userImageRepository;

    @Value("${file.user.image.path}")
    private String savePath;

    public void UserImageSave(User user, String imageUrl) {
        try {
            URL url = new URL(Objects.requireNonNull(imageUrl));

            BufferedImage image = ImageIO.read(url);

            File file = new File(savePath + user.getEmail() + "/image.jpg");
            file.mkdirs();

            ImageIO.write(image, "jpg", file);

            UserImage userImage = new ImageDto(savePath + user.getEmail() + "/image.jpg").toUserImage(user);

            userImageRepository.save(userImage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

