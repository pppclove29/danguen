package com.example.danguen;

import com.example.danguen.domain.user.exception.UserNotFoundException;
import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.user.entity.Role;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.review.RequestReviewDto;
import com.example.danguen.domain.user.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

public class UserTest extends BaseTest {

    @Autowired
    UserServiceImpl userService;

    @DisplayName("특정 유저 정보 요청")
    @WithMockUser
    @Test
    public void successLoadUserInfo() throws Exception {
        //given
        Long userId = userRepository.findAll().get(0).getId();

        //where & then
        mockMvc.perform(get("/user/" + userId))
                .andExpect(jsonPath("$.name").value(sessionName))
                .andExpect(jsonPath("$.address.city").value(userCity))
                .andExpect(jsonPath("$.rate.dealTemperature").value(36.5));

        //then
        User user = userRepository.getReferenceById(userId);

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isEqualTo(sessionName);
        assertThat(user.getAddress().getCity()).isEqualTo(userCity);
        assertThat(user.getAddress().getStreet()).isEqualTo(userStreet);
        assertThat(user.getAddress().getZipcode()).isEqualTo(userZipcode);
        assertThat(user.getRole()).isEqualTo(Role.ROLE_USER);
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
        Long userId = userRepository.findAll().get(0).getId();

        RequestUserUpdateDto dto = new RequestUserUpdateDto();
        dto.setName("김개똥");
        dto.setAddress(new Address("부산광역시", "화지로", "52"));

        //where
        mockMvc.perform(put("/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)));

        //then
        User user = userRepository.getReferenceById(userId);

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isEqualTo("김개똥");
        assertThat(user.getAddress().getCity()).isEqualTo("부산광역시");
        assertThat(user.getAddress().getStreet()).isEqualTo("화지로");
        assertThat(user.getAddress().getZipcode()).isEqualTo("52");
        assertThat(user.getRole()).isEqualTo(Role.ROLE_USER);
    }

    @DisplayName("유저 삭제")
    @WithMockUser
    @Test
    public void successDeleteUser() throws Exception {
        //given
        Long userId = userRepository.findAll().get(0).getId();

        //when
        mockMvc.perform(delete("/user/" + userId));

        //then
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }


    @DisplayName("거래 상대에 대한 좋은리뷰 후 거래 점수 상승")
    @WithMockUser
    @Test
    public void successReviewOtherUser() throws Exception {
        //given
        User user = getSessionUser();


        // 세션 유저에 대한 상대방의 좋은 리뷰
        RequestReviewDto review = new RequestReviewDto();
        review.setDealScore(8);
        review.setPositiveAnswer(new boolean[]{true, true, true, true, true, true, true, true, true, true});
        review.setNegativeAnswer(new boolean[]{false, false, false, false, false, false, false, false, false, false});

        //when
        mockMvc.perform(post("/user/" + user.getId() + "/review-seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(review)));

        //then
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
        User otherUser = getOtherUser();

        //when
        mockMvc.perform(put("/user/iuser/" + otherUser.getId()))
                .andExpect(status().isOk());

        //then
        User user = getSessionUser();

        assertThat(user.getInterestUser()).contains(otherUser);
    }

    @DisplayName("관심유저 제거")
    @WithMockUser
    @Test
    public void successDeleteInterestUser() throws Exception {
        //given
        User user = getSessionUser();
        User otherUser = getOtherUser();

        when(user.getInterestUser()).thenReturn(List.of(otherUser));

        //when
        mockMvc.perform(delete("/user/iuser/" + otherUser.getId()))
                .andExpect(status().isOk());

        //then
        assertThat(user.getInterestUser().size()).isEqualTo(0);
    }

    @DisplayName("관심유저 중복 등록")
    @WithMockUser
    @Test
    public void successDuplicateAddInterestUser() throws Exception {
        //given
        User user = getSessionUser();
        User otherUser = getOtherUser();

        when(user.getInterestUser()).thenReturn(List.of(otherUser));

        //when
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(put("/user/iuser/" + otherUser.getId()))
                    .andExpect(status().isOk());
        }

        //then
        assertThat(user.getInterestUser().size()).isEqualTo(1);
        assertThat(user.getInterestUser()).contains(otherUser);
    }

    @DisplayName("관심유저 중복 삭제")
    @WithMockUser
    @Test
    public void successDuplicateDeleteInterestUser() throws Exception {
        //given
        User user = getSessionUser();
        User otherUser = getOtherUser();

        when(user.getInterestUser()).thenReturn(List.of(otherUser));

        //when
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(delete("/user/iuser/" + otherUser.getId()))
                    .andExpect(status().isOk());
        }

        //then
        assertThat(user.getInterestUser().size()).isEqualTo(0);
        assertThat(user.getInterestUser()).doesNotContain(otherUser);
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
    public void success() throws Exception {
        //given
        User user = getSessionUser();

        final int iUserCnt = 10;
        List<User> iUsers = new ArrayList<>();

        for (int i = 0; i < iUserCnt; i++) {
            iUsers.add(makeMockUser("이름" + i, String.format("iUser[%d]@email.com", i)));
        }

        when(user.getInterestUser()).thenReturn(iUsers);

        //when
        ResultActions resultActions = mockMvc.perform(get("/user/iuser"))
                .andExpect(status().isOk());

        //then
        for (int i = 0; i < iUserCnt; i++) {
            String jsonPathQuery = String.format("$[%d]", i);
            resultActions.andExpect(jsonPath(jsonPathQuery + ".name").value("이름" + i));
            resultActions.andExpect(jsonPath(jsonPathQuery + ".email").value(String.format("iUser[%d]@email.com", i)));
        }

        resultActions.andExpect(jsonPath(String.format("$[%d]", iUserCnt)).doesNotExist());
    }
}
