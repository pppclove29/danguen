package com.example.danguen.domain.repository.comment;

import com.example.danguen.domain.model.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
