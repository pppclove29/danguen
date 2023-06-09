package com.example.danguen;

import com.example.danguen.domain.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.comment.exception.AlreadyDeletedCommentException;
import com.example.danguen.domain.comment.repository.CommentRepository;
import com.example.danguen.domain.post.controller.SecuredArticleController;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.entity.PostKind;
import com.example.danguen.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class CommentTest extends BaseTest {

    //todo 여러 post에 대한 테스트 진행

    /* 댓글 불러오기 확인 테스트 할것
    assertThat(result.getModelAndView().getModel().get("comment")).isInstanceOf(ResponseCommentDto.class);

    ResponseCommentDto comment = (ResponseCommentDto)result.getModelAndView().getModel().get("comment");
     */

    @Autowired
    CommentRepository commentRepository;
    private Long postId;

    @BeforeEach
    public void init() {
        //todo 모든 테스트가 article 위주의 테스트로 되어버렸음
        postId = makeArticle(0, sessionUserId);
    }

    @Transactional
    @DisplayName("중고물품에 댓글 등록 후 Entity 검증")
    @WithMockUser
    @Test
    public void successSaveCommentOnArticleVerifyEntity() throws Exception {
        //given
        RequestCommentSaveDto dto = new RequestCommentSaveDto();
        LocalDateTime testTime = LocalDateTime.now();

        dto.setContent(commentContent);
        dto.setKind(PostKind.Kind.ARTICLE);

        //when
        mockMvc.perform(post(String.format("/post/%d/comment", postId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        //then
        Comment comment = commentRepository.findAll().get(0);

        //todo parent comment, children comment 검증
        assertThat(comment.getLikedUser()).isEmpty();
        assertThat(comment.getContent()).isEqualTo(commentContent);
        assertThat(comment.getPost().getId()).isEqualTo(postId);
        assertThat(comment.isDeleted()).isFalse();
        assertThat(comment.getWriter().isPresent()).isTrue();
        assertThat(comment.getWriter().get().getId()).isEqualTo(sessionUserId);
        assertThat(comment.getCreatedTime()).isAfter(testTime);
    }

    @DisplayName("중고물품에 댓글 등록 후 반환Dto 검증")
    @WithMockUser
    @Test
    public void successSaveCommentOnArticleVerifyDto() throws Exception {
        //given
        RequestCommentSaveDto dto = new RequestCommentSaveDto();
        LocalDateTime testTime = LocalDateTime.now();

        dto.setContent(commentContent);
        dto.setKind(PostKind.Kind.ARTICLE);

        //when
        mockMvc.perform(post(String.format("/post/%d/comment", postId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        //then
        List<ResponseCommentDto> comments = commentService.getComments(postId);

        assertThat(comments.size()).isEqualTo(1);

        ResponseCommentDto commentDto = comments.get(0);

        assertThat(commentDto.getWriter()).isEqualTo(sessionName);
        assertThat(commentDto.getContent()).isEqualTo(commentContent);
        assertThat(commentDto.getLikeCount()).isEqualTo(0);
        assertThat(commentDto.getWrittenTime()).isAfter(testTime);
    }

    @DisplayName("댓글 수정")
    @WithMockUser
    @Test
    public void successUpdateComment() throws Exception {
        //given
        String newContent = "new " + commentContent;

        RequestCommentSaveDto dto = new RequestCommentSaveDto();
        dto.setContent(newContent);

        Long commentId = makeComment(postId, sessionUserId);

        //when
        mockMvc.perform(put("/comment/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        //then
        Comment comment = commentService.getCommentById(commentId);

        assertThat(comment.getContent()).isEqualTo(newContent);
    }

    @DisplayName("댓글 삭제")
    @WithMockUser
    @Test
    public void successDeleteComment() throws Exception {
        //given
        Long commentId = makeComment(postId, sessionUserId);

        //when
        mockMvc.perform(delete("/comment/" + commentId))
                .andExpect(status().isOk());

        //then
        Comment comment = commentService.getCommentById(commentId);

        assertThat(comment.getContent()).isNotEqualTo(commentContent);
        assertThat(comment.getContent()).isEqualTo("삭제된 메세지입니다.");

        User user = userService.getUserById(sessionUserId);

        assertThat(user.getComments()).isEmpty();

        Post post = postService.getPostById(postId);

        assertThat(post.getComments()).isEmpty();
        assertThat(post).isInstanceOf(ArticlePost.class);
    }

    @DisplayName("삭제된 댓글 수정시도")
    @WithMockUser
    @Test
    public void failUpdateWhenCommentAlreadyDeleted() throws Exception {
        //given
        Long commentId = makeComment(postId, sessionUserId);

        commentService.delete(commentId);

        RequestCommentSaveDto dto = new RequestCommentSaveDto();
        dto.setContent("new " + commentContent);

        //when
        MvcResult result = mockMvc.perform(put("/comment/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        assertThat(result.getResponse().getContentAsString()).contains(AlreadyDeletedCommentException.message);
    }

    @DisplayName("유저 삭제시 댓글 유지 검증")
    @WithMockUser
    @Test
    public void successMaintainCommentWhenWriterDeleted() throws Exception {
        //given
        Long commentId = makeComment(postId, sessionUserId);

        //when
        mockMvc.perform(delete("/secured/user/" + sessionUserId))
                .andExpect(status().isOk());

        //then
        System.out.println(commentRepository.findAll().size());

        Comment comment = commentService.getCommentById(commentId);

        assertThat(comment).isNotNull();
        assertThat(comment.getWriter()).isEmpty();

        List<ResponseCommentDto> comments = commentService.getComments(postId);

        assertThat(comments.size()).isEqualTo(1);

        ResponseCommentDto commentDto = comments.get(0);

        assertThat(commentDto.getWriter()).isEqualTo("회원탈퇴 유저");
        assertThat(commentDto.getContent()).isEqualTo(commentContent);
    }

    @DisplayName("글 삭제시 댓글 삭제 검증")
    @WithMockUser
    @Test
    public void successDeleteCommentWhenPostDeleted() throws Exception {
        //given
        makeComment(postId, sessionUserId);

        //when
        mockMvc.perform(delete("/secured/article/" + postId))
                .andExpect(status().isOk());

        //then
        User user = userService.getUserById(sessionUserId);

        assertThat(commentRepository.findAll()).isEmpty();
        assertThat(user.getComments()).isEmpty();
    }
}
