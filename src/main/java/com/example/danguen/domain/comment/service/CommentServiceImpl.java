package com.example.danguen.domain.comment.service;

import com.example.danguen.domain.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.comment.exception.AlreadyDeletedCommentException;
import com.example.danguen.domain.comment.exception.CommentNotFoundException;
import com.example.danguen.domain.comment.repository.CommentRepository;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.service.PostServiceImpl;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final UserServiceImpl userService;
    private final PostServiceImpl postServiceImpl;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Comment saveInPost(RequestCommentSaveDto request, Long postId, Long userId) {
        User user = userService.getUserById(userId);
        Post post = postServiceImpl.getPostById(postId);

        Comment comment = request.toEntity(user, post);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment saveInComment(RequestCommentSaveDto request, Long commentId, Long userId) {
        User user = userService.getUserById(userId);
        Comment parentComment = getCommentById(commentId);

        Comment childComment = request.toEntity(user, parentComment.getPost());

        parentComment.setChildComment(childComment);

        return commentRepository.save(childComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseCommentDto> getComments(Long postId) {
        Post post = postServiceImpl.getPostById(postId);

        return commentRepository.findAllByPost(post)
                .map(ResponseCommentDto::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void update(RequestCommentSaveDto request, Long commentId) throws AlreadyDeletedCommentException {
        Comment comment = getCommentById(commentId);

        if (comment.isDeleted()) // 삭제된 댓글에 대해서는 수정을 막는다
            throw new AlreadyDeletedCommentException();

        comment.updateComment(request.getContent());
    }

    @Override
    @Transactional
    public void delete(Long commentId) {
        Comment comment = getCommentById(commentId);
        comment.updateComment("삭제된 메세지입니다.");
        comment.delete();
    }

    @Override
    @Transactional
    public int like(Long commentId, Long userId) {
        Comment comment = getCommentById(commentId);

        comment.likesComment(userService.getUserById(userId));

        return comment.getLikedUser().size();
    }

    @Override
    @Transactional
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
    }
}
