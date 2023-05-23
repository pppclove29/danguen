package com.example.danguen.domain.image.service;

import com.example.danguen.domain.image.dto.ImageDto;
import com.example.danguen.domain.image.entity.UserImage;
import com.example.danguen.domain.image.repository.ImageRepository;
import com.example.danguen.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Service
public class UserImageService {
    private final ImageRepository userImageRepository;

    @Value("${file.user.image.path}")
    private String savePath;

    public void userImageSave(User user, String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            CompletableFuture<BufferedImage> imageFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return ImageIO.read(url);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read image from URL", e);
                }
            });

            String imagePath = savePath + user.getEmail() + "/image.jpg";
            File file = new File(imagePath);
            Path parentDirPath = file.toPath().getParent();
            Files.createDirectories(parentDirPath);

            BufferedImage image = imageFuture.get();
            ImageIO.write(image, "jpg", file);

            UserImage userImage = new ImageDto(imagePath).toUserImage(user);
            userImageRepository.save(userImage);

        } catch (IOException | InterruptedException | RuntimeException |ExecutionException e){
            throw new RuntimeException("Failed to save user image", e);
        }
    }
}
