package com.example.danguen.domain.image.repository;

import com.example.danguen.domain.image.entity.PostImage;
import com.example.danguen.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    void deleteArticleImageByPost(Post post);
}
