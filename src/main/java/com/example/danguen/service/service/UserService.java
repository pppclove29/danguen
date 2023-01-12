package com.example.danguen.service.service;

import com.example.danguen.domain.infra.UserRepository;
import com.example.danguen.domain.user.User;
import com.example.danguen.domain.user.dto.request.RequestUserJoinDto;
import com.example.danguen.domain.user.dto.request.RequestUserReviewDto;
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
        User user = userRepository.getReferenceById(id);

        ResponseUserPageDto response = new ResponseUserPageDto(user);

        return response;
    }

    @Transactional
    public void update(RequestUserUpdateDto request, Long id){
        User user = userRepository.getReferenceById(id);

        user.updateUser(request);
    }

    @Transactional
    public void review(RequestUserReviewDto request, Long id){
        User user = userRepository.getReferenceById(id);

        user.reviewUser(request);
    }
}
