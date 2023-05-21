package com.example.danguen.domain.comment.service;

import com.example.danguen.domain.comment.exception.AlreadyDeletedCommentException;
import com.example.danguen.domain.comment.dto.request.RequestCommentSaveDto;
import com.example.danguen.domain.comment.dto.response.ResponseCommentDto;
import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.post.entity.PostKind;

import java.util.List;
import java.util.stream.Stream;

public interface CommentService {

    /**
     * 댓글 정보, 댓글을 달 게시물과 댓글을 달 유저의 정보를 받아와 새로운 댓글을 등록
     *
     * @param request 댓글 정보
     * @param postId  게시글 ID
     * @param userId  댓글 작성 유저 ID
     */
    void save(RequestCommentSaveDto request, Long postId, Long userId);

    /**
     * 특정 게시글에 등록된 모든 댓글을 표시
     *
     * @param kind   게시글 유형(PostKind Enum)
     * @param postId 게시글 ID
     * @return 특정 게시물에 등록된 댓글 리스트
     */
    List<ResponseCommentDto> getComments(Long postId);

    /**
     * 댓글 업데이트 정보를 받아 기존 댓글의 정보를 수정
     *
     * @param request   댓글 수정 정보
     * @param commentId 수정할 댓글 ID
     * @throws AlreadyDeletedCommentException 수정시도 중 댓글 삭제 에러처리
     */
    void update(RequestCommentSaveDto request, Long commentId) throws AlreadyDeletedCommentException;

    /**
     * ID에 맞는 댓글 삭제
     *
     * @param commentId 삭제할 댓글 ID
     */
    void delete(Long commentId);

    /**
     * 댓글 좋아요 기능
     *
     * @param commentId 좋아요할 댓글 ID
     * @param userId    좋아요한 유저 ID
     * @return 현 댓글에 좋아요 한 유저 수
     */
    int like(Long commentId, Long userId);

    /**
     * 댓글 ID를 통해 DB 에서 댓글을 가져옴
     *
     * @param commentId 가져올 댓글 ID
     * @return 댓글 Entity
     */
    Comment getCommentFromDB(Long commentId);
}
