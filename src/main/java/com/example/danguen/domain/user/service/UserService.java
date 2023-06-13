package com.example.danguen.domain.user.service;

import com.example.danguen.domain.review.RequestReviewDto;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.user.dto.response.ResponseUserPageDto;
import com.example.danguen.domain.user.dto.response.ResponseUserSimpleDto;
import com.example.danguen.domain.user.entity.Role;
import com.example.danguen.domain.user.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserService {
    ResponseUserPageDto getUserDto(Long userId);

    User save(String name, String email);

    void update(RequestUserUpdateDto request, Long userId);

    void delete(Long userId);

    void review(RequestReviewDto request, Long otherUserId);

    List<ResponseUserSimpleDto> getIUserDtos(Long userId);

    void addInterestUser(Long userId, Long iUserId);

    void deleteInterestUser(Long userId, Long iUserId);

    void changeRole(Long userId, Role role);

    User getUserById(Long userId);

    Optional<User> getUserByEmail(String email);
}
