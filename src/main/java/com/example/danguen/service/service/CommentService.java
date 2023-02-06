package com.example.danguen.service.service;

import com.example.danguen.config.exception.AlreadyDeletedCommentException;
import com.example.danguen.config.exception.ArticleNotFoundException;
import com.example.danguen.config.exception.CommentNotFoundException;
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
    public void save(RequestCommentSaveDto request, String postName, Long postId, Long userId) {
        User user = userRepository.getReferenceById(userId);
        Article article;

        // 보기는 별로 좋지 않다
        switch (postName) {
            case "article":
                article = articleRepository.findById(postId).orElseThrow(ArticleNotFoundException::new);
                break;
            default:
                throw new RuntimeException("알 수 없는 게시물 유형입니다.");
        }

        Comment comment = request.toArticleComment(user, article);

        commentRepository.save(comment);
    }

    @Transactional
    public void update(RequestCommentSaveDto request, Long commentId) throws AlreadyDeletedCommentException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);

        if(comment.isDeleted()) // 삭제된 댓글에 대해서는 수정을 막는다
            throw new AlreadyDeletedCommentException();

        comment.updateComment(request.getContent());
    }

    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        comment.getWriter().removeComment(comment);

        comment.updateComment("삭제된 메세지입니다.");
        comment.delete();
    }

    @Transactional
    public int like(Long commentId, Long userId) {
        User user = userRepository.getReferenceById(userId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);

        comment.likesComment(user);

        return 1;//comment.getLikedUser().size();
    }
}
