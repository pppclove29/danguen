package com.example.danguen.service.api;

import com.example.danguen.argumentResolver.SessionUserId;
import com.example.danguen.config.exception.ArticleNotFoundException;
import com.example.danguen.config.exception.UserNotFoundException;
import com.example.danguen.domain.Address;
import com.example.danguen.domain.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.article.dto.response.ResponseArticleDto;
import com.example.danguen.service.service.ArticleService;
import com.example.danguen.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ArticleApiController {

    private final ArticleService articleService;

    @PostMapping("/article")
    public void register(@RequestBody RequestArticleSaveOrUpdateDto request,
                         @SessionUserId Long userId) {
        articleService.register(request, userId);
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

    @GetMapping("/articles/{city}/{street}/{zipcode}")
    public List<ResponseArticleDto> getArticlePage(@PageableDefault(sort = "createdTime", size = 6, direction = Sort.Direction.DESC) Pageable pageable,
                                                   @PathVariable(required = false) String city,
                                                   @PathVariable(required = false) String street,
                                                   @PathVariable(required = false) String zipcode) {

        Address address = new Address(city, street, zipcode);

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
    public String handleArticleNotFound(){return "articleNotFound";}
}
