package com.example.danguen.domain.repository.image;

import com.example.danguen.domain.model.image.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {
}
