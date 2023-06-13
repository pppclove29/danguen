package com.example.danguen.domain.image.service;

import com.example.danguen.domain.image.entity.ArticleImage;
import com.example.danguen.domain.image.repository.ArticleImageRepository;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.service.ArticleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ArticleImageService implements ImageService {
    @Value("${file.article.image.path}")
    private String savePath;

    private final ArticleServiceImpl articleService;
    private final ArticleImageRepository articleImageRepository;

    @Transactional
    public void save(Long articleId, List<MultipartFile> images) {
        String folderPath = savePath + articleId;
        ArticlePost articlePost = articleService.getArticleById(articleId);

        images.stream()
                .map((image) -> saveToLocal(image, folderPath))
                .filter(Optional::isPresent)
                .map(uuid ->
                        ArticleImage.builder()
                                .uuid(uuid.get())
                                .articlePost(articlePost)
                                .build())
                .forEach(articleImageRepository::save);
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
    public void update(Long articleId, List<MultipartFile> images) {
        ArticlePost articlePost = articleService.getArticleById(articleId);
        articleImageRepository.deleteArticleImageByArticlePost(articlePost);
        deleteFolder(savePath + articleId);

        save(articleId, images);
    }
}