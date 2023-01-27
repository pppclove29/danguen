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
import org.apache.el.stream.Optional;
import org.springframework.aop.scope.ScopedProxyUtils;
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

    @GetMapping("/address/{*addresses}")
    public List<ResponseArticleDto> getArticlePage(@PageableDefault(sort = "createdTime", size = 6, direction = Sort.Direction.DESC) Pageable pageable,
                                                   @PathVariable String addresses) {

        String[] str = addresses.split("/");
        int cnt = str.length;
        // -> "", "city", "street", "zipcode"

        String city = (cnt > 2)?str[1]:"";
        String street = str[2];
        String zipcode = str[3];

        System.out.println("add : " + city);
        System.out.println(street);
        System.out.println(zipcode);


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
    public String handleArticleNotFound() {
        return "articleNotFound";
    }
}
