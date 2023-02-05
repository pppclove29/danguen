package com.example.danguen;

import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.comment.ArticleComment;
import com.example.danguen.domain.model.comment.Comment;
import com.example.danguen.domain.model.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.model.post.article.dto.request.RequestArticleSaveOrUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentTest extends BaseTest {

    @WithMockUser
    @Test
    public void 중고물품에_댓글_달기() throws Exception {
        //given
        //when
        commentRegisterProc();

        //then
        Comment comment = commentRepository.findAll().get(0);

        assertThat(commentRepository.findAll().size()).isEqualTo(1);
        assertThat(comment).isInstanceOf(ArticleComment.class);
        assertThat(comment.getContent()).isEqualTo("댓글 내용");
       // assertThat(comment.getLikedUser().size()).isEqualTo(0);
        assertThat(comment.getWriter().getEmail()).isEqualTo("email@temp.com");
        assertThat(((ArticleComment) comment).getArticle().getTitle()).isEqualTo("제목 0");
        assertThat(comment.getCreatedTime()).isAfter(((ArticleComment) comment).getArticle().getCreatedTime());
    }

    @WithMockUser
    @Test
    public void 댓글_수정() throws Exception {
        //given
        articleRegisterProc(0);

        //RequestCommentSaveDto dto = new N

        //when
    }


}
