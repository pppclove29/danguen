package com.example.danguen.domain.image.service;

import com.example.danguen.domain.image.entity.ArticleImage;
import com.example.danguen.domain.image.repository.ArticleImageRepository;
import com.example.danguen.domain.post.service.ArticleServiceImpl;
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
public class ArticleImageService {
    @Value("${file.article.image.path}")
    private String savePath;

    private final ArticleServiceImpl articleService;
    private final ArticleImageRepository articleImageRepository;

    @Transactional
    public void save(Long articleId, List<MultipartFile> images) {

        String articleImagePath = savePath + articleId;

        if (new File(articleImagePath).mkdirs()) {
            images.stream()
                    .map((image) -> saveToLocal(image, articleImagePath))
                    .filter(Optional::isPresent)
                    .map(uuid ->
                            ArticleImage.builder()
                                    .url(uuid.get())
                                    .articlePost(articleService.getArticleFromDB(articleId))
                                    .build())
                    .forEach(articleImageRepository::save);
        }
    }

    public Optional<String> saveToLocal(MultipartFile multipartFile, String articleImagePath) {
        UUID uuid = UUID.randomUUID();
        try {
            multipartFile.transferTo(new File(articleImagePath + "/" + uuid));
        } catch (IOException e) {
            return Optional.empty();
        }

        return Optional.of(articleImagePath + "/" + uuid);
    }

    public void update(List<MultipartFile> images){
        //todo 기존 사진 삭제를 하던 해서 새로운 사진 추가
    }
}
