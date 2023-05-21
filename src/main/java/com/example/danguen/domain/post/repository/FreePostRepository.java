package com.example.danguen.domain.post.repository;

import com.example.danguen.domain.post.entity.FreePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreePostRepository extends JpaRepository<FreePost, Long> {
}
