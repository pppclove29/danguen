package com.example.danguen.user;

import com.example.danguen.BaseTest;
import com.example.danguen.config.jwt.JwtProperties;
import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.post.dto.response.ResponseArticleSimpleDto;
import com.example.danguen.domain.review.RequestReviewDto;
import com.example.danguen.domain.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.user.dto.response.ResponseUserSimpleDto;
import com.example.danguen.domain.user.entity.Role;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecuredUserApiTest extends BaseTest {

    @DisplayName("특정 유저 정보 요청")
    @Test
    public void successLoadUserInfo() throws Exception {
        //given
        Long userId = loginUserId;
        RequestUserUpdateDto updateDto = new RequestUserUpdateDto();
        updateDto.setName(loginUserName);
        updateDto.setAddress(userAddress);

        userService.update(updateDto, loginUserId);

        //when
        mockMvc.perform(get("/secured/user/" + userId)
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isOk())
                .andReturn();

        //then
        User user = userService.getUserById(userId);

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isEqualTo(loginUserName);
        assertThat(user.getAddress()).isEqualTo(userAddress);
        assertThat(user.getRole()).isEqualTo(Role.USER);
    }

    @DisplayName("존재하지 않는 유저 정보 검색")
    @Test
    public void failLoadNonExistUserInfo() throws Exception {
        //given
        long userId = -1L;

        //when
        MvcResult result = mockMvc.perform(get("/secured/user/" + userId)
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isNotFound())
                .andReturn();

        //then
        assertThat(result.getResponse().getContentAsString()).contains(UserNotFoundException.message);
    }

    @DisplayName("유저 자신의 정보 갱신")
    @Test
    public void successUpdateUserInfo() throws Exception {
        //given
        Long userId = loginUserId;

        RequestUserUpdateDto dto = new RequestUserUpdateDto();
        dto.setName("김개똥");
        Address newAddress = new Address("부산광역시", "화지로", "52");
        dto.setAddress(newAddress);

        //where
        mockMvc.perform(put("/secured/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        User user = userService.getUserById(userId);

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isEqualTo("김개똥");
        assertThat(user.getAddress()).isEqualTo(newAddress);
        assertThat(user.getRole()).isEqualTo(Role.USER);
    }

    @DisplayName("회원탈퇴")
    @Test
    public void successDeleteUser() throws Exception {
        //given
        //when
        mockMvc.perform(delete("/secured/user")
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        assertThat(userService.getUserByEmail(loginUserEmail)).isEqualTo(Optional.empty());
    }


    @DisplayName("거래 상대에 대한 좋은리뷰 후 거래 점수 상승")
    @Test
    public void successReviewOtherUser() throws Exception {
        //given
        RequestReviewDto review = new RequestReviewDto();
        review.setDealScore(8);
        review.setPositiveAnswer(new boolean[]{true, true, true, true, true, true, true, true, true, true});
        review.setNegativeAnswer(new boolean[]{false, false, false, false, false, false, false, false, false, false});

        //when
        mockMvc.perform(post("/secured/user/" + loginUserId + "/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(review))
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        User user = userService.getUserById(loginUserId);

        assertThat(user.getRate().getDealTemperature()).isGreaterThan(36.5f);
        assertThat(user.getRate().getTotalReviewScore()).isEqualTo(8);
        assertThat(user.getRate().getTotalDealCount()).isEqualTo(1);
        assertThat(user.getRate().getReDealHopePercent()).isGreaterThan(50);
    }

    @DisplayName("관심유저 추가")
    @Test
    public void successAddInterestUser() throws Exception {
        //given
        User otherUser = userService.getUserById(otherUserId);

        //when
        mockMvc.perform(put("/secured/user/iuser/" + otherUser.getId())
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        List<ResponseUserSimpleDto> interestUser = userService.getIUserDtos(loginUserId);

        assertThat(interestUser.get(0).getName()).isEqualTo(otherUser.getName());
        assertThat(interestUser.get(0).getId()).isEqualTo(otherUserId);
    }

    @DisplayName("관심유저 제거")
    @Test
    public void successDeleteInterestUser() throws Exception {
        //given
        User otherUser = userService.getUserById(otherUserId);

        setInterestUsers(List.of(otherUser));

        //when
        mockMvc.perform(delete("/secured/user/iuser/" + otherUser.getId())
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isOk());

        //then
        List<ResponseUserSimpleDto> interestUser = userService.getIUserDtos(loginUserId);

        assertThat(interestUser).isEmpty();
    }

    @DisplayName("관심유저 중복 등록")
    @Test
    public void successDuplicateAddInterestUser() throws Exception {
        //given
        User otherUser = userService.getUserById(otherUserId);

        setInterestUsers(List.of(otherUser));

        //when
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(put("/secured/user/iuser/" + otherUser.getId())
                            .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                    .andExpect(status().isOk());
        }

        //then
        List<ResponseUserSimpleDto> interestUser = userService.getIUserDtos(loginUserId);

        assertThat(interestUser.size()).isEqualTo(1);
        assertThat(interestUser.get(0).getName()).isEqualTo(otherUser.getName());
        assertThat(interestUser.get(0).getId()).isEqualTo(otherUserId);
    }

    @DisplayName("관심유저 중복 삭제")
    @Test
    public void successDuplicateDeleteInterestUser() throws Exception {
        //given
        User otherUser = userService.getUserById(otherUserId);

        setInterestUsers(List.of(otherUser));

        //when
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(delete("/secured/user/iuser/" + otherUser.getId())
                            .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                    .andExpect(status().isOk());
        }

        //then
        List<ResponseUserSimpleDto> interestUser = userService.getIUserDtos(loginUserId);

        assertThat(interestUser).isEmpty();
    }

    @DisplayName("존재하지 않는 관심유저 등록")
    @Test
    public void failAddNonExistInterestUser() throws Exception {
        MvcResult result = mockMvc.perform(put("/secured/user/iuser/" + 99999999)
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isNotFound())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).contains(UserNotFoundException.message);
    }

    @DisplayName("존재하지 않는 관심유저 삭제")
    @Test
    public void failDeleteNonExistInterestUser() throws Exception {
        MvcResult result = mockMvc.perform(delete("/secured/user/iuser/" + 99999999)
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isNotFound())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).contains(UserNotFoundException.message);
    }

    @DisplayName("관심 유저 리스트 요청")
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
        ResultActions resultActions = mockMvc.perform(get("/secured/user/iuser")
                        .header(JwtProperties.HEADER,makeJwtValue(Role.USER)))
                .andExpect(status().isOk());


        //then
        for (int i = 0; i < iUserCnt; i++) {
            String jsonPathQuery = String.format("$[%d]", i);
            resultActions.andExpect(jsonPath(jsonPathQuery + ".name").value("이름" + i));
            resultActions.andExpect(jsonPath(jsonPathQuery + ".picture").value("uuid"));
        }

        resultActions.andExpect(jsonPath(String.format("$[%d]", iUserCnt)).doesNotExist());
    }

    @DisplayName("관심 유저의 중고물품 리스트 출력")
    @Test
    public void successLoadInterestUsersArticleList() throws Exception {
        //given
        List<User> interestUserList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            User newUser = makeUser("노관심" + i, "nonInterest" + i + "@email.com");
            makeArticle(i, newUser.getId());
        }
        for (int i = 0; i < 3; i++) {
            User newUser = makeUser("김관심" + i, "interest" + i + "@email.com");
            makeArticle(i, newUser.getId());

            interestUserList.add(newUser);
        }
        setInterestUsers(interestUserList);

        //when
        MvcResult result = mockMvc.perform(get("/secured/user/iusers/articles")
                        .header(JwtProperties.HEADER,makeJwtValue(Role.USER)))
                .andExpect(status().isOk())
                .andReturn();

        List<ResponseArticleSimpleDto> responseList = mappingResponse(result, ResponseArticleSimpleDto.class);

        //then
        assertThat(responseList.size()).isEqualTo(3);
        for (var res : responseList) {
            assertThat(res.getWriter()).contains("김관심");
        }
    }

    @DisplayName("관심 중고물품 리스트 출력")
    @Test
    public void successLoadInterestArticleList() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
            Long articleId = makeArticle(i, loginUserId);

            if (i % 2 == 0) {
                articleService.giveInterest(articleId, loginUserId);
            }
        }

        //when
        MvcResult result = mockMvc.perform(get("/secured/user/iarticle")
                        .header(JwtProperties.HEADER,makeJwtValue(Role.USER)))
                .andExpect(status().isOk())
                .andReturn();

        //then
        List<ResponseArticleSimpleDto> responseList = mappingResponse(result, ResponseArticleSimpleDto.class);

        //then
        assertThat(responseList.size()).isEqualTo(5);
        for (int i = 0; i < responseList.size(); i++) {
            assertThat(responseList.get(i).getTitle()).isEqualTo(articleTitle + i * 2);
        }
    }
}
