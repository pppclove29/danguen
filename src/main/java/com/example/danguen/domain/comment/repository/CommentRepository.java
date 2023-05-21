package com.example.danguen.domain.comment.repository;

import com.example.danguen.domain.comment.entity.Comment;
import com.example.danguen.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Stream<Comment> findAllByPost(Post post);
}
