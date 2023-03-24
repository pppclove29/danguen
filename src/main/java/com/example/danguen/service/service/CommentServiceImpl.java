package com.example.danguen.service.service;

import com.example.danguen.config.exception.AlreadyDeletedCommentException;
import com.example.danguen.config.exception.ArticleNotFoundException;
import com.example.danguen.config.exception.CommentNotFoundException;
import com.example.danguen.domain.model.comment.ArticleComment;
import com.example.danguen.domain.model.comment.Comment;
import com.example.danguen.domain.model.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.model.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.model.post.article.Article;
import com.example.danguen.domain.model.post.article.Post;
import com.example.danguen.domain.model.user.User;
import com.example.danguen.domain.repository.ArticleRepository;
import com.example.danguen.domain.repository.UserRepository;
import com.example.danguen.domain.repository.comment.ArticleCommentRepository;
import com.example.danguen.domain.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService{

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final ArticleCommentRepository articleCommentRepository;

@Override
    @Transactional
    public void save(RequestCommentSaveDto request, String postName, Long postId, Long userId) {
        User user = userRepository.getReferenceById(userId);
        Post post;

        // 보기 별로 좋지 않다
        switch (postName) {
            case "article":
                post = articleRepository.findById(postId).orElseThrow(ArticleNotFoundException::new);
                ArticleComment aComment = request.toArticleComment(user, (Article) post);
                articleCommentRepository.save(aComment);
                break;
            default:
                throw new RuntimeException("알 수 없는 게시물 유형입니다.");
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseCommentDto> getComments(String postName, Long postId) {
        List<ResponseCommentDto> list;

        switch (postName) {
            case "article":
                List<ArticleComment> aCommentList = articleCommentRepository.findAllByArticle_Id(postId);
                list = aCommentList.stream().map(ArticleComment::toDto).collect(Collectors.toList());
                break;
            default:
                throw new RuntimeException("알 수 없는 게시물 유형입니다.");
        }
        return list;
    }

    @Override
    @Transactional
    public void update(RequestCommentSaveDto request, Long commentId) throws AlreadyDeletedCommentException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);

        if (comment.isDeleted()) // 삭제된 댓글에 대해서는 수정을 막는다
            throw new AlreadyDeletedCommentException();

        comment.updateComment(request.getContent());
    }

    @Override
    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        comment.getWriter().removeComment(comment);

        comment.updateComment("삭제된 메세지입니다.");
        comment.delete();
    }

    @Override
    @Transactional
    public int like(Long commentId, Long userId) {
        User user = userRepository.getReferenceById(userId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);

        comment.likesComment(user);

        return comment.getLikedUser().size();
    }
}
