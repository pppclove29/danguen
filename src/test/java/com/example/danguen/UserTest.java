package com.example.danguen;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.review.RequestReviewDto;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.user.dto.response.ResponseUserSimpleDto;
import com.example.danguen.domain.user.entity.Role;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.exception.UserNotFoundException;
import com.example.danguen.domain.user.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserTest extends BaseTest {

    @DisplayName("특정 유저 정보 요청")
    @WithMockUser
    @Test
    public void successLoadUserInfo() throws Exception {
        //given
        Long userId = sessionUserId;
        RequestUserUpdateDto updateDto = new RequestUserUpdateDto();
        updateDto.setName(sessionName);
        updateDto.setAddress(userAddress);

        userService.update(updateDto, sessionUserId);

        //when
        mockMvc.perform(get("/user/" + userId))
                .andExpect(status().isOk())
                .andReturn();

        //then
        User user = userService.getUserById(userId);

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isEqualTo(sessionName);
        assertThat(user.getAddress()).isEqualTo(userAddress);
        assertThat(user.getRole()).isEqualTo(Role.USER);
    }

    @DisplayName("존재하지 않는 유저 정보 검색")
    @WithMockUser
    @Test
    public void failLoadNonExistUserInfo() throws Exception {
        //given
        long userId = -1L;

        //when
        MvcResult result = mockMvc.perform(get("/user/" + userId))
                .andReturn();

        //then
        assertThat(result.getResponse().getContentAsString()).contains(UserNotFoundException.message);
    }

    @DisplayName("특정 유저 정보 갱신")
    @WithMockUser
    @Test
    public void successUpdateUserInfo() throws Exception {
        //given
        Long userId = sessionUserId;

        RequestUserUpdateDto dto = new RequestUserUpdateDto();
        dto.setName("김개똥");
        Address newAddress = new Address("부산광역시", "화지로", "52");
        dto.setAddress(newAddress);

        //where
        mockMvc.perform(put("/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)));

        //then
        User user = userService.getUserById(userId);

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isEqualTo("김개똥");
        assertThat(user.getAddress()).isEqualTo(newAddress);
        assertThat(user.getRole()).isEqualTo(Role.USER);
    }

    @DisplayName("유저 삭제")
    @WithMockUser
    @Test
    public void successDeleteUser() throws Exception {
        //given
        Long userId = sessionUserId;

        //when
        mockMvc.perform(delete("/user/" + userId));

        //then
        assertThat(userService.getUserByEmail(sessionEmail)).isEqualTo(Optional.empty());
    }


    @DisplayName("거래 상대에 대한 좋은리뷰 후 거래 점수 상승")
    @WithMockUser
    @Test
    public void successReviewOtherUser() throws Exception {
        //given

        // 세션 유저에 대한 상대방의 좋은 리뷰
        RequestReviewDto review = new RequestReviewDto();
        review.setDealScore(8);
        review.setPositiveAnswer(new boolean[]{true, true, true, true, true, true, true, true, true, true});
        review.setNegativeAnswer(new boolean[]{false, false, false, false, false, false, false, false, false, false});

        //when
        mockMvc.perform(post("/user/" + sessionUserId + "/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(review)));

        //then
        User user = userService.getUserById(sessionUserId);

        assertThat(user.getRate().getDealTemperature()).isGreaterThan(36.5f);
        assertThat(user.getRate().getTotalReviewScore()).isEqualTo(8);
        assertThat(user.getRate().getTotalDealCount()).isEqualTo(1);
        assertThat(user.getRate().getReDealHopePercent()).isGreaterThan(50);
    }

    @DisplayName("관심유저 추가")
    @WithMockUser
    @Test
    public void successAddInterestUser() throws Exception {
        //given
        User otherUser = userService.getUserById(noneSessionUserId);

        //when
        mockMvc.perform(put("/user/iuser/" + otherUser.getId()))
                .andExpect(status().isOk());

        //then
        List<ResponseUserSimpleDto> interestUser = userService.getIUserDtos(sessionUserId);

        assertThat(interestUser.get(0).getName()).isEqualTo(otherUser.getName());
        assertThat(interestUser.get(0).getId()).isEqualTo(userService.getUserByEmail(noneSessionEmail).get().getId());
    }

    @DisplayName("관심유저 제거")
    @WithMockUser
    @Test
    public void successDeleteInterestUser() throws Exception {
        //given
        User otherUser = userService.getUserById(noneSessionUserId);

        setInterestUsers(List.of(otherUser));

        //when
        mockMvc.perform(delete("/user/iuser/" + otherUser.getId()))
                .andExpect(status().isOk());

        //then
        List<ResponseUserSimpleDto> interestUser = userService.getIUserDtos(sessionUserId);

        assertThat(interestUser.size()).isEqualTo(0);
    }

    @DisplayName("관심유저 중복 등록")
    @WithMockUser
    @Test
    public void successDuplicateAddInterestUser() throws Exception {
        //given
        User otherUser = userService.getUserById(noneSessionUserId);

        setInterestUsers(List.of(otherUser));

        //when
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(put("/user/iuser/" + otherUser.getId()))
                    .andExpect(status().isOk());
        }

        //then
        List<ResponseUserSimpleDto> interestUser = userService.getIUserDtos(sessionUserId);

        assertThat(interestUser.size()).isEqualTo(1);
        assertThat(interestUser.get(0).getName()).isEqualTo(otherUser.getName());
        assertThat(interestUser.get(0).getId()).isEqualTo(userService.getUserByEmail(noneSessionEmail).get().getId());
    }

    @DisplayName("관심유저 중복 삭제")
    @WithMockUser
    @Test
    public void successDuplicateDeleteInterestUser() throws Exception {
        //given
        User otherUser = userService.getUserById(noneSessionUserId);

        setInterestUsers(List.of(otherUser));

        //when
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(delete("/user/iuser/" + otherUser.getId()))
                    .andExpect(status().isOk());
        }

        //then
        List<ResponseUserSimpleDto> interestUser = userService.getIUserDtos(sessionUserId);

        assertThat(interestUser.size()).isEqualTo(0);
    }

    @DisplayName("존재하지 않는 관심유저 등록")
    @WithMockUser
    @Test
    //todo 성공할지 말지 결정
    public void AddNonExistInterestUser() {
    }

    @DisplayName("존재하지 않는 관심유저 삭제")
    @WithMockUser
    @Test
    //todo 성공할지 말지 결정
    public void DeleteNonExistInterestUser() {
    }

    @DisplayName("관심 유저 리스트 요청")
    @WithMockUser
    @Test
    public void successLoadInterestUserList() throws Exception {
        //given
        final int iUserCnt = 10;
        List<User> iUsers = new ArrayList<>();

        for (int i = 0; i < iUserCnt; i++) {
            iUsers.add(makeUser("이름" + i, String.format("iUser[%d]@email.com", i)));
        }

        setInterestUsers(iUsers);

        //when
        ResultActions resultActions = mockMvc.perform(get("/user/iuser"))
                .andExpect(status().isOk());


        //then
        for (int i = 0; i < iUserCnt; i++) {
            String jsonPathQuery = String.format("$[%d]", i);
            resultActions.andExpect(jsonPath(jsonPathQuery + ".name").value("이름" + i));
            resultActions.andExpect(jsonPath(jsonPathQuery + ".picture").value("uuid"));
        }

        resultActions.andExpect(jsonPath(String.format("$[%d]", iUserCnt)).doesNotExist());
    }


}
