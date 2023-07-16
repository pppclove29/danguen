package com.example.danguen.domain.image.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.example.danguen.domain.image.entity.Image;
import com.example.danguen.domain.image.entity.PostImage;
import com.example.danguen.domain.image.repository.PostImageRepository;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.service.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostImageService implements ImageService {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final PostServiceImpl postServiceImpl;
    private final PostImageRepository postImageRepository;

    @Transactional
    public void save(Long postId, List<MultipartFile> images) {
        Post post = postServiceImpl.getPostById(postId);

        images.stream()
                .map((image) -> {
                    try {
                        return saveToS3(image, postId);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(Optional::isPresent)
                .map(uuid -> PostImage.builder()
                        .uuid(uuid.get())
                        .post(post)
                        .build())
                .forEach(postImageRepository::save);
    }

    private Optional<String> saveToS3(MultipartFile multipartFile, Long postId) throws IOException {
        UUID uuid = UUID.randomUUID();
        String finalPath = String.format("post/[%d]/[%s]", postId, uuid);

        File file = new File(uuid.toString());
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        }
        amazonS3Client.putObject(bucket, uuid.toString(), file);

        return Optional.of(finalPath);
    }

    @Transactional
    public void update(Long postId, List<MultipartFile> images) {
        Post post = postServiceImpl.getPostById(postId);
        postImageRepository.deleteArticleImageByPost(post);

        delete(post.getImages());
        save(postId, images);
    }

    @Transactional
    public void delete(Long postId) {
        Post post = postServiceImpl.getPostById(postId);

        delete(post.getImages());
    }

    @Transactional
    public void delete(List<? extends Image> images) {
        for (var image : images) {
            amazonS3Client.deleteObject(bucket, image.getUuid());
        }
    }
}