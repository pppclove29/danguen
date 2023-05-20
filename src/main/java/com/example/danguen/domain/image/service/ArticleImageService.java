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

@RequiredArgsConstructor
@Service
public class ArticleImageService {
    @Value("${file.article.image.path}")
    private String savePath;

    private final ArticleServiceImpl articleService;
    private final ArticleImageRepository articleImageRepository;

    @Transactional
    public void save(Long articleId, List<MultipartFile> images) throws IOException {
        if (new File(savePath + articleId).mkdirs()) {
            images.stream()
                    .map(this::saveToLocal)
                    .map(image ->
                            ArticleImage.builder()
                                    .name(image.getOriginalFilename())
                                    .url(imageUUid)
                                    .article(articleService.getArticleFromDB(articleId))
                                    .build())
                    .forEach(articleImageRepository::save);
        }
    }

    public MultipartFile saveToLocal(MultipartFile multipartFile) {
        multipartFile.transferTo(new File());
    }
}
