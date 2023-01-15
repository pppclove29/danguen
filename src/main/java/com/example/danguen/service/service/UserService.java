package com.example.danguen.service.service;

import com.example.danguen.config.exception.UserNotFoundException;
import com.example.danguen.domain.infra.UserRepository;
import com.example.danguen.domain.user.User;
import com.example.danguen.domain.user.dto.request.review.RequestBuyerReviewDto;
import com.example.danguen.domain.user.dto.request.review.RequestSellerReviewDto;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.user.dto.response.ResponseUserPageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ResponseUserPageDto getUserPage(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());

        return new ResponseUserPageDto(user);
    }

    @Transactional(readOnly = true)
    public Long getUserIdByEmail(String email) {
        Long id = userRepository.findByEmail(email).get().getId();

        return id;
    }

    @Transactional
    public ResponseUserPageDto update(RequestUserUpdateDto request, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());

        user.updateUser(request);

        return new ResponseUserPageDto(user);
    }

    @Transactional
    public void reviewSeller(RequestSellerReviewDto request, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());

            user.reviewSeller(request);
    }
    @Transactional
    public void reviewBuyer(RequestBuyerReviewDto request, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());

        user.reviewBuyer(request);
    }
}
