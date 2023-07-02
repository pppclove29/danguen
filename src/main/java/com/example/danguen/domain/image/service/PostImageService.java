package com.example.danguen.domain.image.service;

import com.example.danguen.domain.image.entity.PostImage;
import com.example.danguen.domain.image.repository.PostImageRepository;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.service.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostImageService implements ImageService {
    @Value("${file.article.image.path}")
    private String savePath;

    private final PostServiceImpl postServiceImpl;
    private final PostImageRepository postImageRepository;

    @Transactional
    public void save(Long postId, List<MultipartFile> images) {
        String folderPath = savePath + postId;
        Post post = postServiceImpl.getPostById(postId);

        images.stream()
                .map((image) -> saveToLocal(image, folderPath))
                .filter(Optional::isPresent)
                .map(uuid ->
                        PostImage.builder()
                                .uuid(uuid.get())
                                .post(post)
                                .build())
                .forEach(postImageRepository::save);
    }

    private Optional<String> saveToLocal(MultipartFile multipartFile, String folderPath) {
        UUID uuid = UUID.randomUUID();
        String finalPath = folderPath + "/" + uuid;
        try {
            new File(folderPath).mkdirs();
            multipartFile.transferTo(new File(finalPath));
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.of(finalPath);
    }

    @Transactional
    public void update(Long postId, List<MultipartFile> images) {
        Post post = postServiceImpl.getPostById(postId);
        postImageRepository.deleteArticleImageByPost(post);
        deleteFolder(savePath + postId);

        save(postId, images);
    }
}