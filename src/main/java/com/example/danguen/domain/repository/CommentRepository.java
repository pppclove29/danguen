package com.example.danguen.domain.repository;

import com.example.danguen.domain.model.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
