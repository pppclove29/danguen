package com.example.danguen.domain.user.service;

import com.example.danguen.domain.review.RequestReviewDto;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.user.dto.response.ResponseUserPageDto;
import com.example.danguen.domain.user.dto.response.ResponseUserSimpleDto;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.exception.UserNotFoundException;
import com.example.danguen.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseUserPageDto getUserDto(Long userId) {
        User user = getUserById(userId);

        return ResponseUserPageDto.toResponse(user);
    }



    @Override
    @Transactional
    public User save(String name, String email) {
        return userRepository.save(
                User.builder()
                        .name(name)
                        .email(email)
                        .build()
        );
    }

    @Override
    @Transactional
    public void update(RequestUserUpdateDto request, Long userId) {
        User user = getUserById(userId);

        user.updateUser(request);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        User user = getUserById(userId);

        user.giveUpComments();

        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public void review(RequestReviewDto request, Long otherUserId) {
        getUserById(otherUserId).review(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseUserSimpleDto> getIUserDtos(Long userId) {
        User user = getUserById(userId);

        return user.getInterestUsers().stream().map(ResponseUserSimpleDto::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addInterestUser(Long userId, Long iUserId) {
        User user = getUserById(userId);
        User iUser = getUserById(iUserId);

        if (!user.isInterestUser(iUser)) {
            user.addInterestUser(iUser);
        }
    }

    @Override
    @Transactional
    public void deleteInterestUser(Long userId, Long iUserId) {
        User user = getUserById(userId);
        User iUser = getUserById(iUserId);

        if (user.isInterestUser(iUser)) {
            user.deleteInterestUser(iUser);
        }
    }

    @Override
    @Transactional
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
.