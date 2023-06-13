package com.example.danguen.comment;

import com.example.danguen.BaseTest;
import com.example.danguen.config.jwt.JwtProperties;
import com.example.danguen.domain.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.comment.exception.AlreadyDeletedCommentException;
import com.example.danguen.domain.post.entity.ArticlePost;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.entity.PostKind;
import com.example.danguen.domain.user.entity.Role;
import com.example.danguen.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class SecuredCommentTest extends BaseTest {
    //todo 여러 post에 대한 테스트 진행

    private Long postId;

    @BeforeEach
    public void init() {
        //todo 모든 테스트가 article 위주의 테스트로 되어버렸음
        postId = makeArticle(0, loginUserId);
    }

    @DisplayName("중고물품에 댓글 등록 후 Entity 검증")
    @Test
    public void successSaveCommentOnArticleVerifyEntity() throws Exception {
        //given
        RequestCommentSaveDto dto = new RequestCommentSaveDto();
        LocalDateTime testTime = LocalDateTime.now();

        dto.setContent(commentContent);
        dto.setKind(PostKind.Kind.ARTICLE);

        //when
        mockMvc.perform(post("/secured/post/" + postId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        Comment comment = commentRepository.findAll().get(0);

        assertThat(comment.getParentComment()).isNull();
        assertThat(comment.getChildrenComment()).isEmpty();
        assertThat(comment.getLikedUser()).isEmpty();
        assertThat(comment.getContent()).isEqualTo(commentContent);
        assertThat(comment.getPost().getId()).isEqualTo(postId);
        assertThat(comment.isDeleted()).isFalse();
        assertThat(comment.getWriter().isPresent()).isTrue();
        assertThat(comment.getWriter().get().getId()).isEqualTo(loginUserId);
        assertThat(comment.getCreatedTime()).isAfter(testTime);
    }

    @DisplayName("중고물품에 댓글 등록 후 반환Dto 검증")
    @Test
    public void successSaveCommentOnArticleVerifyDto() throws Exception {
        //given
        RequestCommentSaveDto dto = new RequestCommentSaveDto();
        LocalDateTime testTime = LocalDateTime.now();

        dto.setContent(commentContent);
        dto.setKind(PostKind.Kind.ARTICLE);

        //when
        mockMvc.perform(post("/secured/post/" + postId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        List<ResponseCommentDto> comments = commentService.getComments(postId);

        assertThat(comments.size()).isEqualTo(1);

        ResponseCommentDto commentDto = comments.get(0);

        assertThat(commentDto.getWriter()).isEqualTo(loginUserName);
        assertThat(commentDto.getContent()).isEqualTo(commentContent);
        assertThat(commentDto.getLikeCount()).isEqualTo(0);
        assertThat(commentDto.getWrittenTime()).isAfter(testTime);
    }

    @DisplayName("대댓글 등록")
    @Test
    public void successSaveCommentOnComment() throws Exception {
        //given
        Long parentCommentId = makeComment(postId, loginUserId);

        RequestCommentSaveDto dto = new RequestCommentSaveDto();
        LocalDateTime testTime = LocalDateTime.now();

        dto.setContent(commentContent);
        dto.setKind(PostKind.Kind.ARTICLE);

        //when
        mockMvc.perform(post("/secured/comment/" + parentCommentId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        Comment parentComment = commentService.getCommentById(parentCommentId);

        assertThat(parentComment.getChildrenComment().size()).isEqualTo(1);

        Comment childComment = commentRepository.findAll().get(1);

        assertThat(childComment.getParentComment()).isNotNull();
        assertThat(childComment.getChildrenComment()).isEmpty();
        assertThat(childComment.getLikedUser()).isEmpty();
        assertThat(childComment.getContent()).isEqualTo(commentContent);
        assertThat(childComment.getPost().getId()).isEqualTo(postId);
        assertThat(childComment.isDeleted()).isFalse();
        assertThat(childComment.getWriter().isPresent()).isTrue();
        assertThat(childComment.getWriter().get().getId()).isEqualTo(loginUserId);
        assertThat(childComment.getCreatedTime()).isAfter(testTime);
    }

    @DisplayName("댓글 수정")
    @Test
    public void successUpdateComment() throws Exception {
        //given
        String newContent = "new " + commentContent;

        RequestCommentSaveDto dto = new RequestCommentSaveDto();
        dto.setContent(newContent);

        Long commentId = makeComment(postId, loginUserId);

        //when
        mockMvc.perform(put("/secured/comment/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        Comment comment = commentService.getCommentById(commentId);

        assertThat(comment.getContent()).isEqualTo(newContent);
    }

    @DisplayName("댓글 삭제")
    @Test
    public void successDeleteComment() throws Exception {
        //given
        Long commentId = makeComment(postId, loginUserId);

        //when
        mockMvc.perform(delete("/secured/comment/" + commentId)
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        Comment comment = commentService.getCommentById(commentId);

        assertThat(comment.getContent()).isNotEqualTo(commentContent);
        assertThat(comment.getContent()).isEqualTo("삭제된 메세지입니다.");

        User user = userService.getUserById(loginUserId);

        assertThat(user.getComments()).isEmpty();

        Post post = postService.getPostById(postId);

        assertThat(post.getComments()).isNotEmpty();
    }

    @DisplayName("삭제된 댓글 수정시도")
    @Test
    public void failUpdateWhenCommentAlreadyDeleted() throws Exception {
        //given
        Long commentId = makeComment(postId, loginUserId);

        commentService.delete(commentId);

        RequestCommentSaveDto dto = new RequestCommentSaveDto();
        dto.setContent("new " + commentContent);

        //when
        MvcResult result = mockMvc.perform(put("/secured/comment/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        assertThat(result.getResponse().getContentAsString()).contains(AlreadyDeletedCommentException.message);
    }

    @DisplayName("유저 삭제시 댓글 유지 검증")
    @Test
    public void successMaintainCommentWhenWriterDeleted() throws Exception {
        //given
        Long commentId = makeComment(postId, otherUserId);

        //when
        mockMvc.perform(delete("/admin/user/" + otherUserId)
                        .header(JwtProperties.HEADER, makeJwtValue(Role.ADMIN)))
                .andExpect(status().isOk());

        //then
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
    @Test
    public void successDeleteCommentWhenPostDeleted() throws Exception {
        //given
        makeComment(postId, loginUserId);

        //when
        mockMvc.perform(delete("/secured/article/" + postId)
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        User user = userService.getUserById(loginUserId);

        assertThat(commentRepository.findAll()).isEmpty();
        assertThat(user.getComments()).isEmpty();
    }
}