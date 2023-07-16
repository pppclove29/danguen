package com.example.danguen.domain.image.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.danguen.domain.image.dto.ImageDto;
import com.example.danguen.domain.image.entity.UserImage;
import com.example.danguen.domain.image.repository.ImageRepository;
import com.example.danguen.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserImageService implements ImageService {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final ImageRepository userImageRepository;

    @Transactional
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

            String imagePath = String.format("user/[%s]", user.getEmail());

            UserImage userImage = new ImageDto(UUID.randomUUID().toString()).toUserImage(user);
            userImageRepository.save(userImage);

            File file = new File(imagePath);
            BufferedImage image = imageFuture.get();
            ImageIO.write(image, "jpg", file);

            amazonS3Client.putObject(bucket, imagePath, file);

        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Failed to save user image", e);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //todo update, delete
}
