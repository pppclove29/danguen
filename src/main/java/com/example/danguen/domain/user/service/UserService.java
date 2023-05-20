package com.example.danguen.domain.user.service;

import com.example.danguen.domain.review.RequestReviewDto;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.user.dto.response.ResponseUserPageDto;
import com.example.danguen.domain.user.dto.response.ResponseUserSimpleDto;
import com.example.danguen.domain.user.entity.User;

import java.util.List;

public interface UserService {

    /**
     * 특정 유저의 상세 정보를 표시
     *
     * @param userId 특정 유저의 ID
     * @return 특정 유저의 상세 정보
     */
    ResponseUserPageDto getUserDto(Long userId);

    /**
     * Email 을 통해 특정 유저가 있는지 판별 후 존재하면 반환, 없다면 새로 생성
     *
     * @param name  새로 저장할 유저의 이름
     * @param email 판별할 유저의 Email
     * @return 존재하는 유저 or 새로 생성된 유저
     */
    User getOrSave(String name, String email);

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
     * 상대방에 대한 평가를 진행한다
     *
     * @param request     평가표
     * @param otherUserId 평가할 상대의 ID
     */
    void review(RequestReviewDto request, Long otherUserId);


    /**
     * 특정 유저의 관심유저 목록을 반환
     *
     * @param userId 조회할 유저의 ID
     * @return 조회한 유저의 관심유저 목록
     */
    List<ResponseUserSimpleDto> getIUserDtos(Long userId);

    /**
     * 세션유저의 관심유저에 특정 유저를 추가한다
     *
     * @param userId  세션유저 ID
     * @param iUserId 관심유저에 추가할 유저 ID
     */
    void addInterestUser(Long userId, Long iUserId);

    /**
     * 세션유저의 관심유저에 특정 유저를 제거한다
     *
     * @param userId  특정 유저의 ID
     * @param iUserId 관심유저에서 제거할 유저 ID
     */
    void deleteInterestUser(Long userId, Long iUserId);

    /**
     * 특정 유저를 DB 에서 가져옴
     *
     * @param userId 가져올 유저의 Id
     * @return User Entity
     */
    User getUserFromDB(Long userId);
}
