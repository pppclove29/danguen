package com.example.danguen.comment;

import com.example.danguen.BaseTest;
import com.example.danguen.config.jwt.JwtProperties;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.user.entity.Role;
import com.example.danguen.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminCommentTest extends BaseTest {

    private Long postId;
    private Long commentId;

    @BeforeEach
    public void localInit() {
        //given
        postId = makeArticle(0, otherUserId);
        commentId = makeComment(postId, otherUserId);
    }

    @DisplayName("관리자의 타인 댓글 삭제")
    @Test
    public void successDeleteCommentByAdmin() throws Exception {
        //when
        mockMvc.perform(delete("/admin/comment/" + commentId)
                        .header(JwtProperties.HEADER, makeJwtValue(Role.ADMIN)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        //then
        Comment comment = commentService.getCommentById(commentId);

        assertThat(comment.getContent()).isNotEqualTo(commentContent);
        assertThat(comment.getContent()).isEqualTo("삭제된 메세지입니다.");

        User user = userService.getUserById(otherUserId);

        assertThat(user.getComments()).isEmpty();

        Post post = postService.getPostById(postId);

        assertThat(post.getComments()).isNotEmpty();
    }

    @DisplayName("익명유저의 타인 댓글 삭제")
    @Test
    public void failDeleteCommentByAnonymous() throws Exception {
        //when
        mockMvc.perform(delete("/admin/comment/" + commentId))
                .andExpect(status().isUnauthorized());

        //then
        Comment comment = commentService.getCommentById(commentId);

        assertThat(comment.getContent()).isEqualTo(commentContent);

        User user = userService.getUserById(otherUserId);
        assertThat(user.getComments()).isNotEmpty();

        Post post = postService.getPostById(postId);
        assertThat(post.getComments()).isNotEmpty();
    }

    @DisplayName("일반유저의 타인 댓글 삭제")
    @Test
    public void failDeleteCommentByUser() throws Exception {
        //when
        mockMvc.perform(delete("/admin/comment/" + commentId)
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isForbidden());

        //then
        Comment comment = commentService.getCommentById(commentId);

        assertThat(comment.getContent()).isEqualTo(commentContent);

        User user = userService.getUserById(otherUserId);
        assertThat(user.getComments()).isNotEmpty();

        Post post = postService.getPostById(postId);
        assertThat(post.getComments()).isNotEmpty();
    }
}
