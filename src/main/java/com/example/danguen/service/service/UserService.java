package com.example.danguen.service.service;

import com.example.danguen.domain.infra.UserRepository;
import com.example.danguen.domain.user.User;
import com.example.danguen.domain.user.dto.RequestUserPageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public RequestUserPageDto getUserPage(Long id){
        User user = userRepository.getReferenceById(id);

        RequestUserPageDto request = new RequestUserPageDto(user);

        return request;
    }
}
