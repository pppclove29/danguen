package com.example.danguen.service.service;

import com.example.danguen.config.exception.UserNotFoundException;
import com.example.danguen.config.oauth.PrincipalUserDetails;
import com.example.danguen.domain.model.user.User;
import com.example.danguen.domain.model.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.model.user.dto.request.review.RequestBuyerReviewDto;
import com.example.danguen.domain.model.user.dto.request.review.RequestSellerReviewDto;
import com.example.danguen.domain.model.user.dto.response.ResponseUserPageDto;
import com.example.danguen.domain.model.user.dto.response.ResponseUserSimpleDto;
import com.example.danguen.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ResponseUserPageDto getUserPage(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        return new ResponseUserPageDto(user);
    }

    @Transactional(readOnly = true)
    public Long getUserIdByEmail(String email) {
        Long id = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new).getId();

        return id;
    }

    @Transactional
    public ResponseUserPageDto update(RequestUserUpdateDto request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.updateUser(request);

        return new ResponseUserPageDto(user);
    }

    @Transactional
    public void delete(Long userId) {
        User user = userRepository.getReferenceById(userId);

        user.giveUpComments(); // 작성한 모든 댓글에 대한 소유권을 포기한다

        userRepository.deleteById(userId);
    }

    @Transactional
    public void reviewSeller(RequestSellerReviewDto request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.reviewSeller(request);
    }

    @Transactional
    public void reviewBuyer(RequestBuyerReviewDto request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.reviewBuyer(request);
    }

    @Transactional(readOnly = true)
    public List<ResponseUserSimpleDto> getIUsers(Long userId) {
        User user = userRepository.findById(userId).get();

        return user.getInterestUser().stream().map(ResponseUserSimpleDto::toDto).collect(Collectors.toList());
    }

    // iuserId와 userId의 혼동을 방지하기 위해 session id 값을 서비스에서 받아온다
    @Transactional
    public void addInterestUser(Long iUserId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(((PrincipalUserDetails) principal).getUserEmail()).orElseThrow(UserNotFoundException::new);

        User iUser = userRepository.findById(iUserId).orElseThrow(UserNotFoundException::new);

        if(!user.getInterestUser().contains(iUser))
            user.addInterestUser(iUser);
    }

    @Transactional
    public void deleteInterestUser(Long iUserId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(((PrincipalUserDetails) principal).getUserEmail()).orElseThrow(UserNotFoundException::new);

        User iUser = userRepository.findById(iUserId).orElseThrow(UserNotFoundException::new);

        if(user.getInterestUser().contains(iUser))
            user.deleteInterestUser(iUser);
    }
}
