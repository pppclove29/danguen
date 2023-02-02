package com.example.danguen.service.api;

import com.example.danguen.argumentResolver.SessionUserId;
import com.example.danguen.config.exception.ArticleNotFoundException;
import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.post.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.model.post.article.dto.response.ResponseArticleDto;
import com.example.danguen.service.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ArticleController {

    @Value("${file.dir}")
    private String savePath;

    private final ArticleService articleService;

    @PostMapping(value = "/article", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public void save(@RequestPart("request") RequestArticleSaveOrUpdateDto request,
                     @RequestPart("images") List<MultipartFile> images,
                     @SessionUserId Long userId) {

        articleService.save(request, userId);
    }

    @PutMapping("/article/{articleId}")
    public void update(@RequestBody RequestArticleSaveOrUpdateDto request,
                       @PathVariable Long articleId) {
        articleService.update(request, articleId);
    }

    @DeleteMapping("/article/{articleId}")
    public void delete(@PathVariable Long articleId,
                       @SessionUserId Long userId) {
        articleService.delete(articleId, userId);
    }

    @GetMapping("/article/{articleId}")
    public ResponseArticleDto getArticle(@PathVariable Long articleId) {

        return articleService.getArticle(articleId);
    }

    @GetMapping("/address/**")
    public List<ResponseArticleDto> getArticlePage(@PageableDefault(size = 6) Pageable pageable,
                                                   HttpServletRequest servletRequest) {

        Address address = new Address(
                (String) servletRequest.getAttribute("city"),
                (String) servletRequest.getAttribute("street"),
                (String) servletRequest.getAttribute("zipcode"));

        System.out.println(address.getCity());
        System.out.println(address.getStreet());
        System.out.println(address.getZipcode());

        return articleService.getArticlePage(pageable, address);
    }

    @GetMapping("/hot-articles")
    public List<ResponseArticleDto> getHotArticlePage(@PageableDefault(size = 6) Pageable pageable) {
        return articleService.getHotArticlePage(pageable);
    }

    @GetMapping("/search")
    public List<ResponseArticleDto> getSearchPage(@PageableDefault(size = 6) Pageable pageable,
                                                  @RequestParam("keyword") String title) {

        return articleService.getSearchArticlePage(pageable, title);
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    public String handleArticleNotFound() {
        return "articleNotFound";
    }
}
