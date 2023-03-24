package com.example.danguen.service.service;

import com.example.danguen.domain.model.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.model.user.dto.request.review.RequestBuyerReviewDto;
import com.example.danguen.domain.model.user.dto.request.review.RequestSellerReviewDto;
import com.example.danguen.domain.model.user.dto.response.ResponseUserPageDto;
import com.example.danguen.domain.model.user.dto.response.ResponseUserSimpleDto;

import java.util.List;

public interface UserService {

    /**
     * 특정 유저의 상세 정보를 표시
     *
     * @param userId 특정 유저의 ID
     * @return 특정 유저의 상세 정보
     */
    ResponseUserPageDto getUserPage(Long userId);

    /**
     * Email 을 통해 특정 유저의 ID를 반환
     *
     * @param email ID를 알고 싶은 유저의 Email
     * @return 해당 유저의 ID
     */
    Long getUserIdByEmail(String email);

    /**
     * 유저 업데이트 정보를 받아 기존 유저의 정보를 수정
     *
     * @param request 유저 수정 정보
     * @param userId  수정할 유저 ID
     */
    void update(RequestUserUpdateDto request, Long userId);

    /**
     * ID를 통해 특정 유저 삭제
     *
     * @param userId 삭제할 유저 ID
     */
    void delete(Long userId);

    /**
     * 구매자의 판매자 평가, 평가표와 평가할 판매자 ID를 통해 평가를 진행
     *
     * @param request 평가표
     * @param sellerId 판매자 ID
     */
    void reviewSeller(RequestSellerReviewDto request, Long sellerId);

    /**
     * 판매자의 구매자 평가, 평가표와 평가할 구매자 ID를 통해 평가를 진행
     *
     * @param request 평가표
     * @param buyerId 구매자 ID
     */
    void reviewBuyer(RequestBuyerReviewDto request, Long buyerId);

    /**
     * 특정 유저의 관심유저 목록을 반환
     * 
     * @param userId 조회할 유저의 ID
     * @return 조회한 유저의 관심유저 목록
     */
    List<ResponseUserSimpleDto> getIUsers(Long userId);

    /**
     * 특정 유저를 관심유저로 추가한다
     *
     * @param iUserId 관심유저로 추가할 유저의 ID
     */
    void addInterestUser(Long iUserId);

    /**
     * 특정 유저를 관심유저에서 제거한다
     *
     * @param iUserId 관심유저에서 제거할 유저의 ID
     */
    void deleteInterestUser(Long iUserId);
}
