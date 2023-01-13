package com.example.danguen.service.service;

import com.example.danguen.config.exception.UserNotFoundException;
import com.example.danguen.domain.infra.UserRepository;
import com.example.danguen.domain.user.User;
import com.example.danguen.domain.user.dto.request.RequestSellerReviewDto;
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
    public ResponseUserPageDto getUserPage(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());

        ResponseUserPageDto response = new ResponseUserPageDto(user);

        return response;
    }
    @Transactional(readOnly = true)
    public Long getUserIdByEmail(String email){
        Long id = userRepository.findByEmail(email).get().getId();

        return id;
    }

    @Transactional
    public void update(RequestUserUpdateDto request, Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());

        user.updateUser(request);
    }

    @Transactional
    public void review(RequestSellerReviewDto request, Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());

        user.reviewUser(request);
    }
}
