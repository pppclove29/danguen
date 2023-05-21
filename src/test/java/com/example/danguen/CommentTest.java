package com.example.danguen;

import com.example.danguen.domain.comment.exception.AlreadyDeletedCommentException;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentTest extends BaseTest {

    /* 댓글 불러오기 확인 테스트 할것
    assertThat(result.getModelAndView().getModel().get("comment")).isInstanceOf(ResponseCommentDto.class);

        ResponseCommentDto comment = (ResponseCommentDto)result.getModelAndView().getModel().get("comment");
     */
    @WithMockUser
    @Test
    public void 중고물품에_댓글_달기() throws Exception {
        //given
        //when
        commentSaveProc();

        //then
        Comment comment = commentRepository.findAll().get(0);

        assertThat(commentRepository.findAll().size()).isEqualTo(1);
        assertThat(comment).isInstanceOf(ArticlePostComment.class);
        assertThat(comment.getContent()).isEqualTo(commentContent);
        assertThat(comment.getLikedUser().size()).isEqualTo(0);
        assertThat(comment.getWriter().getEmail()).isEqualTo(sessionEmail);
        assertThat(((ArticlePostComment) comment).getArticlePost().getTitle()).isEqualTo(title + 0);
        assertThat(comment.getCreatedTime()).isAfter(((ArticlePostComment) comment).getArticlePost().getCreatedTime());
    }

    @WithMockUser
    @Test
    public void 댓글_수정() throws Exception {
        //given
        commentSaveProc();

        Long commentId = commentRepository.findAll().get(0).getId();

        //when
        mockMvc.perform(put("/comment/" + commentId)
                        .param("content", commentContent + " new"))
                .andExpect(status().isOk());

        //then
        Comment comment = commentRepository.findById(commentId).get();

        assertThat(comment.getContent()).isEqualTo(commentContent + " new");
        assertThat(comment).isInstanceOf(ArticlePostComment.class);
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
        assertThat(comment).isInstanceOf(ArticlePostComment.class);
    }

    @Test
    public void 삭제된_댓글_수정시도() throws Exception {
        //given
        commentSaveProc();

        Long commentId = commentRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/comment/" + commentId))
                .andExpect(status().isOk());

        RequestCommentSaveDto dto = new RequestCommentSaveDto(commentContent);
        dto.setContent(articleContent + " new");

        //when
        MvcResult result = mockMvc.perform(put("/comment/" + commentId)
                        .param("content", commentContent + " new"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        Comment comment = commentRepository.findById(commentId).get();

        assertThat(comment.getContent()).isNotEqualTo(commentContent);
        assertThat(comment.getContent()).isEqualTo("삭제된 메세지입니다.");
        assertThat(comment).isInstanceOf(ArticlePostComment.class);

        assertThat(result.getResponse().getContentAsString()).contains(AlreadyDeletedCommentException.message);
    }

    @Test
    public void 유저삭제시_댓글유지테스트() throws Exception {
        //given
        User user = makeUserProc("임꺽정", "im@mmm.com");
        noneSessionsArticleSaveProc(user, 0);

        Long articleId = articlePostRepository.findAll().get(0).getId();

        mockMvc.perform(post("/article/" + articleId + "/comment")
                        .param("content", commentContent))
                .andExpect(status().is3xxRedirection());


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
        noneSessionsArticleSaveProc(user, 0);

        Long articleId = articlePostRepository.findAll().get(0).getId();

        mockMvc.perform(post("/article/" + articleId + "/comment")
                        .param("content", commentContent))
                .andExpect(status().is3xxRedirection());

        //when
        mockMvc.perform(delete("/article/" + articleId))
                .andExpect(status().isOk());

        //then
        User sessionUser = userRepository.findByEmail(sessionEmail).get();

        assertThat(commentRepository.findAll().size()).isEqualTo(0);
        assertThat(sessionUser.getComments().size()).isEqualTo(0);
    }

}
