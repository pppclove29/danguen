package com.example.danguen.post.article;


import com.example.danguen.BaseTest;
import com.example.danguen.config.jwt.JwtProperties;
import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.image.service.ArticleImageService;
import com.example.danguen.domain.post.dto.request.RequestArticleSaveOrUpdateDto;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.repository.ArticlePostRepository;
import com.example.danguen.domain.user.entity.Role;
import com.example.danguen.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//todo anonymousUser의 secured 접근에 대한 테스트 진행
//todo admin의 작업에 대한 anonymousUser, normal User, admin User의 접근 테스트 진행
//@WebMvcTest(SecuredArticleController.class)
public class SecuredArticlePostTest extends BaseTest {

    @Transactional(readOnly = true)
    @DisplayName("이미지가 포함된 중고물품 등록")
    @Test
    public void successSaveArticleWithImages() throws Exception {
        //given
        //todo local 등 저장소에 저장하는 작업을 막아야함 s3를 사용하던가
        RequestArticleSaveOrUpdateDto dto = RequestArticleSaveOrUpdateDto.builder()
                .title(articleTitle)
                .content(articleContent)
                .price(articlePrice)
                .category(articleCategory)
                .dealHopeAddress(new Address(articleCity, articleStreet, articleZipcode))
                .build();

        MockMultipartFile image1 = new MockMultipartFile(
                "images",
                "input1.png",
                "image/png",
                new FileInputStream("src/test/java/testImage/input.png"));

        MockMultipartFile image2 = new MockMultipartFile(
                "images",
                "input2.png",
                "image/png",
                new FileInputStream("src/test/java/testImage/input.png"));

        MockMultipartFile image3 = new MockMultipartFile(
                "images",
                "input3.png",
                "image/png",
                new FileInputStream("src/test/java/testImage/input.png"));

        //when
        mockMvc.perform(multipart("/secured/article")
                        .file(image1)
                        .file(image2)
                        .file(image3)
                        .flashAttr("request", dto)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(JwtProperties.HEADER,makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        ArticlePost articlePost = articlePostRepository.findAll().get(0);

        assertThat(articlePost.getImages().size()).isEqualTo(3);
    }

    @DisplayName("이미지 없이 중고물품 등록")
    @Test
    public void failSaveArticleWithOutImages() throws Exception {
        //given
        Address dealAddress = new Address(articleCity, articleStreet, articleZipcode);

        RequestArticleSaveOrUpdateDto dto = RequestArticleSaveOrUpdateDto.builder()
                .title(articleTitle)
                .content(articleContent)
                .price(articlePrice)
                .category(articleCategory)
                .dealHopeAddress(dealAddress)
                .build();

        //when
        mockMvc.perform(multipart("/secured/article")
                        .flashAttr("request", dto)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(JwtProperties.HEADER,makeJwtValue(Role.USER)))
                .andExpect(status().isBadRequest());

        //then
        assertThat(articlePostRepository.findAll()).isEmpty();
    }

    @DisplayName("물품 정보 수정")
    @Test
    public void successUpdateArticleInfo() throws Exception {
        //given
        Long articleId = makeArticle(0, loginUserId);

        RequestArticleSaveOrUpdateDto dto = RequestArticleSaveOrUpdateDto.builder()
                .title("new" + articleTitle)
                .content("new" + articleContent)
                .price(30000)
                .category("new" + articleCategory)
                .dealHopeAddress(new Address(
                        "new" + articleCity,
                        "new" + articleStreet,
                        "new" + articleZipcode))
                .build();

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "input.png",
                "image/png",
                new FileInputStream("src/test/java/testImage/input.png"));


        //when
        mockMvc.perform(multipart(HttpMethod.PUT, "/secured/article/" + articleId)
                        .file(image)
                        .flashAttr("request", dto)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(JwtProperties.HEADER,makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        ArticlePost articlePost = articleService.getArticleById(articleId);
        assertThat(articlePost.getTitle()).isEqualTo("new" + articleTitle);
        assertThat(articlePost.getCategory()).isEqualTo("new" + articleCategory);
        assertThat(articlePost.getContent()).isEqualTo("new" + articleContent);
        assertThat(articlePost.getDealHopeAddress()).isEqualTo(
                new Address("new" + articleCity, "new" + articleStreet, "new" + articleZipcode)
        );
        assertThat(articlePost.getPrice()).isEqualTo(30000);
    }

    @Transactional(readOnly = true)
    @DisplayName("중고물품 삭제")
    @Test
    public void successDeleteArticle() throws Exception {
        //given
        Long articleId = makeArticle(0, loginUserId);

        //when
        mockMvc.perform(delete("/secured/article/" + articleId)
                        .header(JwtProperties.HEADER,makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        User user = userService.getUserById(loginUserId);

        assertThat(articlePostRepository.findAll()).isEmpty();
        assertThat(user.getSellArticlePosts()).isEmpty();
    }



    @DisplayName("중고물품 등록자 회원탈퇴시 게시글 자동삭제")
    @Test
    public void successAutoDeleteArticleWhenSellerWithdrawal() throws Exception {
        //given
        makeArticle(0, loginUserId);

        //when
        mockMvc.perform(delete("/secured/user")
                        .header(JwtProperties.HEADER,makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        assertThat(articlePostRepository.findAll()).isEmpty();
    }



    @Transactional(readOnly = true)
    @DisplayName("중고물품에 관심주기")
    @Test
    public void successGiveInterestInArticle() throws Exception {
        //given
        Long articleId = makeArticle(0, loginUserId);


        //when
        mockMvc.perform(post("/secured/article/" + articleId + "/interest")
                        .header(JwtProperties.HEADER,makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        ArticlePost articlePost = articleService.getArticleById(articleId);

        assertThat(articlePost.getInterestingUsers().size()).isEqualTo(1);
        assertThat(articlePost.getInterestingUsers().get(0).getId()).isEqualTo(loginUserId);

        User user = userService.getUserById(loginUserId);

        assertThat(user.getInterestArticles().size()).isEqualTo(1);
        assertThat(user.getInterestArticles().get(0).getId()).isEqualTo(articleId);
    }

    //todo like, chat
}