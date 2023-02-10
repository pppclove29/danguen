package com.example.danguen;

import com.example.danguen.config.exception.AlreadyDeletedCommentException;
import com.example.danguen.domain.model.comment.ArticleComment;
import com.example.danguen.domain.model.comment.Comment;
import com.example.danguen.domain.model.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.model.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentTest extends BaseTest {

    @WithMockUser
    @Test
    public void 중고물품에_댓글_달기() throws Exception {
        //given
        //when
        commentSaveProc();

        //then
        Comment comment = commentRepository.findAll().get(0);

        assertThat(commentRepository.findAll().size()).isEqualTo(1);
        assertThat(comment).isInstanceOf(ArticleComment.class);
        assertThat(comment.getContent()).isEqualTo(commentContent);
        assertThat(comment.getLikedUser().size()).isEqualTo(0);
        assertThat(comment.getWriter().getEmail()).isEqualTo(sessionEmail);
        assertThat(((ArticleComment) comment).getArticle().getTitle()).isEqualTo(title + 0);
        assertThat(comment.getCreatedTime()).isAfter(((ArticleComment) comment).getArticle().getCreatedTime());
    }

    @WithMockUser
    @Test
    public void 댓글_수정() throws Exception {
        //given
        commentSaveProc();

        Long commentId = commentRepository.findAll().get(0).getId();

        RequestCommentSaveDto dto = new RequestCommentSaveDto();
        dto.setContent(articleContent + " new");


        //when
        mockMvc.perform(put("/comment/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        //then
        Comment comment = commentRepository.findById(commentId).get();

        assertThat(comment.getContent()).isEqualTo(dto.getContent());
        assertThat(comment).isInstanceOf(ArticleComment.class);
    }

    @Test
    public void 댓글_삭제() throws Exception {
        //given
        commentSaveProc();

        Long commentId = commentRepository.findAll().get(0).getId();

        //when
        mockMvc.perform(delete("/comment/" + commentId))
                .andExpect(status().isOk());

        //then
        Comment comment = commentRepository.findById(commentId).get();

        assertThat(comment.getContent()).isNotEqualTo(commentContent);
        assertThat(comment.getContent()).isEqualTo("삭제된 메세지입니다.");
        assertThat(comment).isInstanceOf(ArticleComment.class);
    }

    @Test
    public void 삭제된_댓글_수정시도() throws Exception {
        //given
        commentSaveProc();

        Long commentId = commentRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/comment/" + commentId))
                .andExpect(status().isOk());

        RequestCommentSaveDto dto = new RequestCommentSaveDto();
        dto.setContent(articleContent + " new");

        //when
        MvcResult result = mockMvc.perform(put("/comment/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        //then
        Comment comment = commentRepository.findById(commentId).get();

        assertThat(comment.getContent()).isNotEqualTo(commentContent);
        assertThat(comment.getContent()).isEqualTo("삭제된 메세지입니다.");
        assertThat(comment).isInstanceOf(ArticleComment.class);

        assertThat(result.getResponse().getContentAsString()).contains(AlreadyDeletedCommentException.message);
    }

    @Test
    public void 유저삭제시_댓글유지테스트() throws Exception {
        //given
        User user = makeUserProc("임꺽정", "im@mmm.com");
        noneSessionsArticleSaveProc(user,0);

        RequestCommentSaveDto dto = new RequestCommentSaveDto();
        dto.setContent(commentContent);

        Long articleId = articleRepository.findAll().get(0).getId();

        mockMvc.perform(get("/article/" + articleId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        Long userId = userRepository.findByEmail(sessionEmail).get().getId();

        //when
        mockMvc.perform(delete("/user/" + userId))
                .andExpect(status().isOk());

        //then
        Comment comment = commentRepository.findAll().get(0);

        assertThat(comment).isNotNull();
        assertThat(comment.getWriter()).isNull();
    }

    @Test
    public void 글삭제시_댓글삭제테스트() throws Exception {
        //given
        User user = makeUserProc("임꺽정", "im@mmm.com");
        noneSessionsArticleSaveProc(user,0);

        RequestCommentSaveDto dto = new RequestCommentSaveDto();
        dto.setContent(commentContent);

        Long articleId = articleRepository.findAll().get(0).getId();

        mockMvc.perform(get("/article/" + articleId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        //when
        mockMvc.perform(delete("/article/" + articleId))
                .andExpect(status().isOk());

        //then
        User sessionUser = userRepository.findByEmail(sessionEmail).get();

        assertThat(commentRepository.findAll().size()).isEqualTo(0);
        assertThat(sessionUser.getComments().size()).isEqualTo(0);
    }

}
