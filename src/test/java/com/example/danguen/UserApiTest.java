package com.example.danguen;

import com.example.danguen.domain.Address;
import com.example.danguen.domain.model.user.Role;
import com.example.danguen.domain.model.user.User;
import com.example.danguen.domain.model.user.dto.request.RequestUserUpdateDto;
import com.example.danguen.domain.model.user.dto.request.review.RequestBuyerReviewDto;
import com.example.danguen.domain.model.user.dto.request.review.RequestSellerReviewDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class UserApiTest extends BaseTest {

//    @BeforeEach
//    public void 임의유저_생성() {
//        Address address = new Address("서울시", "서울구", "서울로");
//
//        User user = User.builder()
//                .name("박이름")
//                .email("email@temp.com")
//                .picture("picture")
//                .address(address)
//                .build();
//
//        userRepository.save(user);
//
//        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
//                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
//                .alwaysDo(print())
//                .build();
//    }

    @WithMockUser
    @Test
    public void 특정_유저_불러오기() throws Exception {
        //given
        Long userId = userRepository.findAll().get(0).getId();

        //where & then
        mockMvc.perform(get("/user/" + userId))
                .andExpect(jsonPath("$.name").value("박이름"))
                .andExpect(jsonPath("$.address.city").value("서울시"))
                .andExpect(jsonPath("$.rate.dealTemperature").value(36.5));

        //then
        User user = userRepository.getReferenceById(userId);

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isEqualTo("박이름");
        assertThat(user.getAddress().getCity()).isEqualTo("서울시");
        assertThat(user.getAddress().getStreet()).isEqualTo("서울구");
        assertThat(user.getAddress().getZipcode()).isEqualTo("서울로");
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
        assertThat(result.getResponse().getContentAsString()).contains("userNotFound");
    }

    @WithMockUser
    @Test
    public void 특정유저_정보_업데이트() throws Exception {
        //given
        Long userId = userRepository.findAll().get(0).getId();

        RequestUserUpdateDto dto = new RequestUserUpdateDto();
        dto.setName("김개똥");
        dto.setAddress(new Address("부산시", "부산동", "부산로"));

        //where & then
        mockMvc.perform(put("/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(jsonPath("$.name").value("김개똥"))
                .andExpect(jsonPath("$.address.city").value("부산시"))
                .andExpect(jsonPath("$.rate.dealTemperature").value(36.5));

        //then
        User user = userRepository.getReferenceById(userId);

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isEqualTo("김개똥");
        assertThat(user.getAddress().getCity()).isEqualTo("부산시");
        assertThat(user.getAddress().getStreet()).isEqualTo("부산동");
        assertThat(user.getAddress().getZipcode()).isEqualTo("부산로");
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
        User seller = User.builder()
                .name("박판매")
                .email("seller@temp.com")
                .picture("picture")
                .address(new Address("1", "2", "3"))
                .build();

        User buyer = User.builder()
                .name("김구매")
                .email("buyer@temp.com")
                .picture("picture")
                .address(new Address("1", "2", "3"))
                .build();

        userRepository.save(seller);
        userRepository.save(buyer);

        Long sellerId = userRepository.findByEmail("seller@temp.com").get().getId();
        Long buyerId = userRepository.findByEmail("buyer@temp.com").get().getId();

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
        seller = userRepository.getReferenceById(sellerId);
        assertThat(seller.getRate().getDealTemperature()).isGreaterThan(36.5f);
        assertThat(seller.getRate().getTotalReviewScore()).isEqualTo(8);
        assertThat(seller.getRate().getTotalDealCount()).isEqualTo(1);
        assertThat(seller.getRate().getReDealHopePercent()).isGreaterThan(50);

        buyer = userRepository.getReferenceById(buyerId);
        assertThat(buyer.getRate().getDealTemperature()).isLessThan(36.5f);
        assertThat(buyer.getRate().getTotalReviewScore()).isEqualTo(2);
        assertThat(buyer.getRate().getTotalDealCount()).isEqualTo(1);
        assertThat(buyer.getRate().getReDealHopePercent()).isLessThan(50);
    }

}
