package com.example.danguen.service.service;

import com.example.danguen.domain.model.comment.ArticleComment;
import com.example.danguen.domain.model.comment.Comment;
import com.example.danguen.domain.model.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.model.post.article.Article;
import com.example.danguen.domain.model.user.User;
import com.example.danguen.domain.repository.ArticleRepository;
import com.example.danguen.domain.repository.CommentRepository;
import com.example.danguen.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;


    @Transactional
    public void save(RequestCommentSaveDto request, String post, Long postId, Long userId) {
        User user = userRepository.getReferenceById(userId);


        // 좋은 코드인가? 아닌거 같다
        switch (post) {
            case "article":
                Article article = articleRepository.findById(postId).get();
                ArticleComment comment = request.toArticleCommentEntity(user, article);
                commentRepository.save(comment);
                break;
            case "other":
            case "end":
            default:
        }
    }

    @Transactional
    public void update(RequestCommentSaveDto request, Long commentId) {

    }

    @Transactional
    public void delete(Long commentId) {

    }

    @Transactional
    public void like(Long commentId) {

    }
}
