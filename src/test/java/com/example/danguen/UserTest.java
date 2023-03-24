package com.example.danguen;

import com.example.danguen.config.exception.UserNotFoundException;
import com.example.danguen.config.oauth.CustomOAuth2UserService;
import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.user.Role;
import com.example.danguen.domain.model.user.User;
import com.example.danguen.domain.model.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.model.user.dto.request.review.RequestBuyerReviewDto;
import com.example.danguen.domain.model.user.dto.request.review.RequestSellerReviewDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserTest extends BaseTest {

    @MockBean
    CustomOAuth2UserService oAuth2UserService;

    @Test
    public void Oauth_로그인() throws Exception {
        //given
        //Mockito.when(oAuth2UserService.loadUser(user));

        //when
        mockMvc.perform(get("/login").with(oauth2Login()
                        // 1
                        .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                        // 2
                        .attributes(attributes -> {
                            attributes.put("name", "name");
                            attributes.put("email", "my@email");
                            attributes.put("picture", "https://my_picture");
                        })
                ))
                .andExpect(status().isOk());
        //then

    }

    @WithMockUser
    @Test
    public void 특정_유저_불러오기() throws Exception {
        //given
        Long userId = userRepository.findAll().get(0).getId();

        //where & then
        mockMvc.perform(get("/user/" + userId))
                .andExpect(jsonPath("$.name").value(sessionName))
                .andExpect(jsonPath("$.address.city").value(city))
                .andExpect(jsonPath("$.rate.dealTemperature").value(36.5));

        //then
        User user = userRepository.getReferenceById(userId);

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isEqualTo(sessionName);
        assertThat(user.getAddress().getCity()).isEqualTo(city);
        assertThat(user.getAddress().getStreet()).isEqualTo(street);
        assertThat(user.getAddress().getZipcode()).isEqualTo(zipcode);
        assertThat(user.getRole()).isEqualTo(Role.ROLE_USER);
    }

    @WithMockUser
    @Test
    public void 없는_유저_정보_검색() throws Exception {
        //given
        Long userId = -1L;

        //when
        MvcResult result = mockMvc.perform(get("/user/" + userId))
                .andReturn();

        //then
        assertThat(result.getResponse().getContentAsString()).contains(UserNotFoundException.message);
    }

    @WithMockUser
    @Test
    public void 특정유저_정보_업데이트() throws Exception {
        //given
        Long userId = userRepository.findAll().get(0).getId();

        RequestUserUpdateDto dto = new RequestUserUpdateDto();
        dto.setName("김개똥");
        dto.setAddress(new Address("부산광역시", "화지로", "52"));

        //where & then
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

    @WithMockUser
    @Test
    public void 회원탈퇴() throws Exception {
        //given
        Long userId = userRepository.findAll().get(0).getId();

        //when
        mockMvc.perform(delete("/user/" + userId));

        //then
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }


    @WithMockUser
    @Test
    public void 구매자_및_판매자리뷰() throws Exception {
        //given
        Long sellerId = makeUserProc("박판매", "seller@temp.com").getId();
        Long buyerId = makeUserProc("김구매", "buyer@temp.com").getId();

        // 구매자에 대한 판매자의 긍정적 리뷰
        RequestSellerReviewDto sdto = new RequestSellerReviewDto();
        sdto.setDealScore(8);
        sdto.setPositiveAnswer(new boolean[]{true, true, true, true, true, true, true, true, true, true});
        sdto.setNegativeAnswer(new boolean[]{false, false, false, false, false, false, false, false, false, false});

        // 판매자에 대한 구매자의 부정적 리뷰
        RequestBuyerReviewDto bdto = new RequestBuyerReviewDto();
        bdto.setDealScore(2);
        bdto.setPositiveAnswer(new boolean[]{false, false, false, false, false, false, false, false, false, false});
        bdto.setNegativeAnswer(new boolean[]{true, true, true, true, true, true, true, true, true, true});

        //when
        mockMvc.perform(post("/user/" + sellerId + "/review-seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(sdto)));

        mockMvc.perform(post("/user/" + buyerId + "/review-buyer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bdto)));

        //then
        User seller = userRepository.getReferenceById(sellerId);
        assertThat(seller.getRate().getDealTemperature()).isGreaterThan(36.5f);
        assertThat(seller.getRate().getTotalReviewScore()).isEqualTo(8);
        assertThat(seller.getRate().getTotalDealCount()).isEqualTo(1);
        assertThat(seller.getRate().getReDealHopePercent()).isGreaterThan(50);

        User buyer = userRepository.getReferenceById(buyerId);
        assertThat(buyer.getRate().getDealTemperature()).isLessThan(36.5f);
        assertThat(buyer.getRate().getTotalReviewScore()).isEqualTo(2);
        assertThat(buyer.getRate().getTotalDealCount()).isEqualTo(1);
        assertThat(buyer.getRate().getReDealHopePercent()).isLessThan(50);
    }

    @WithMockUser
    @Test
    public void 관심유저_등록() throws Exception {
        //given
        User iUser = makeUserProc("이관심", "interest@temp.net");

        //when
        mockMvc.perform(put("/user/iuser/" + iUser.getId()))
                .andExpect(status().isOk());

        //then
        User user = userRepository.findByEmail(sessionEmail).get();

        assertThat(user.getInterestUser()).contains(iUser);
    }

    @WithMockUser
    @Test
    public void 관심유저_제거() throws Exception {
        //given
        User iUser = makeUserProc("이관심", "interest@temp.net");

        mockMvc.perform(put("/user/iuser/" + iUser.getId()))
                .andExpect(status().isOk());

        //when
        mockMvc.perform(delete("/user/iuser/" + iUser.getId()))
                .andExpect(status().isOk());

        //then
        User user = userRepository.findByEmail(sessionEmail).get();

        assertThat(user.getInterestUser().size()).isEqualTo(0);
    }

    @WithMockUser
    @Test
    public void 관심유저_중복_등록_및_삭제() throws Exception {
        //given
        //given
        User iUser1 = makeUserProc("이관심", "interest@temp.net");
        User iUser2 = makeUserProc("최사랑", "lovelove@temp.net");

        mockMvc.perform(put("/user/iuser/" + iUser2.getId()))
                .andExpect(status().isOk());

        //when
        //중복 등록
        for (int i = 0; i < 3; i++)
            mockMvc.perform(put("/user/iuser/" + iUser1.getId()))
                    .andExpect(status().isOk());

        //중복 삭제
        for (int i = 0; i < 3; i++)
            mockMvc.perform(delete("/user/iuser/" + iUser2.getId()))
                    .andExpect(status().isOk());

        //then
        User user = userRepository.findByEmail(sessionEmail).get();

        assertThat(user.getInterestUser().size()).isEqualTo(1);
        assertThat(user.getInterestUser()).contains(iUser1);
        assertThat(user.getInterestUser()).doesNotContain(iUser2);
    }

    @WithMockUser
    @Test
    public void 관심유저_불러오기() throws Exception {
        //given
        for (int i = 1; i < 11; i++) {
            User iUser = makeUserProc("이름" + i, "이메일" + i + "@temp.net");
            mockMvc.perform(put("/user/iuser/" + iUser.getId()))
                    .andExpect(status().isOk());
        }

        //when
        mockMvc.perform(get("/user/iuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("이름1"))
                .andExpect(jsonPath("$[1].name").value("이름2"))
                .andExpect(jsonPath("$[2].name").value("이름3"))
                .andExpect(jsonPath("$[3].name").value("이름4"))
                .andExpect(jsonPath("$[4].name").value("이름5"))
                .andExpect(jsonPath("$[9]").exists())
                .andExpect(jsonPath("$[10]").doesNotExist());
    }
}
