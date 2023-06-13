package com.example.danguen.post.article;

import com.example.danguen.BaseTest;
import com.example.danguen.config.jwt.JwtProperties;
import com.example.danguen.domain.user.entity.Role;
import com.example.danguen.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminArticlePostTest extends BaseTest {

    private Long articleId;

    @BeforeEach
    public void localInit() {
        //given
        articleId = makeArticle(0, loginUserId);
    }

    @DisplayName("관리자의 중고물품 삭제")
    @Test
    public void successDeleteArticleByAdmin() throws Exception {
        //when
        mockMvc.perform(delete("/admin/article/" + articleId)
                        .header(JwtProperties.HEADER, makeJwtValue(Role.ADMIN)))
                .andExpect(status().isOk());

        //then
        User user = userService.getUserById(loginUserId);

        assertThat(articlePostRepository.findAll()).isEmpty();
        assertThat(user.getSellArticlePosts()).isEmpty();
    }

    @DisplayName("익명유저의 중고물품 삭제")
    @Test
    public void failDeleteArticleByAnonymous() throws Exception {
        //when
        mockMvc.perform(delete("/admin/article/" + articleId))
                .andExpect(status().isUnauthorized());

        //then
        User user = userService.getUserById(loginUserId);

        assertThat(articlePostRepository.findAll()).isNotEmpty();
        assertThat(user.getSellArticlePosts()).isNotEmpty();
    }

    @DisplayName("일반 유저의 중고물품 삭제")
    @Test
    public void failDeleteArticleByUser() throws Exception {
        //when
        mockMvc.perform(delete("/admin/article/" + articleId)
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isForbidden());

        //then
        User user = userService.getUserById(loginUserId);

        assertThat(articlePostRepository.findAll()).isNotEmpty();
        assertThat(user.getSellArticlePosts()).isNotEmpty();
    }
}
