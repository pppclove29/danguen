package com.example.danguen.domain.comment.service;

import com.example.danguen.domain.comment.exception.AlreadyDeletedCommentException;
import com.example.danguen.domain.comment.exception.CommentNotFoundException;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.post.entity.Post;
import com.example.danguen.domain.post.entity.PostKind;
import com.example.danguen.domain.post.service.PostService;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.comment.repository.CommentRepository;
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
    private final PostService postService;

    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public void save(RequestCommentSaveDto request, Long postId, Long userId) {
        User user = userService.getUserFromDB(userId);
        Post post = postService.getPostFromDB(postId);

        Comment comment = request.toEntity(user, post);
        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseCommentDto> getComments(Long postId) {
        Post post = postService.getPostFromDB(postId);

        return commentRepository.findAllByPost(post)
                .map(ResponseCommentDto::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void update(RequestCommentSaveDto request, Long commentId) throws AlreadyDeletedCommentException {
        Comment comment = getCommentFromDB(commentId);

        if (comment.isDeleted()) // 삭제된 댓글에 대해서는 수정을 막는다
            throw new AlreadyDeletedCommentException();

        comment.updateComment(request.getContent());
    }

    @Override
    @Transactional
    public void delete(Long commentId) {
        Comment comment = getCommentFromDB(commentId);
        comment.getWriter().removeComment(comment);

        comment.updateComment("삭제된 메세지입니다.");
        comment.delete();
    }

    @Override
    @Transactional
    public int like(Long commentId, Long userId) {
        Comment comment = getCommentFromDB(commentId);

        comment.likesComment(userService.getUserFromDB(userId));

        return comment.getLikedUser().size();
    }

    @Override
    @Transactional
    public Comment getCommentFromDB(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
    }


}
