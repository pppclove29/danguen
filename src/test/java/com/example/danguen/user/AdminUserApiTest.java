package com.example.danguen.user;

import com.example.danguen.BaseTest;
import com.example.danguen.config.jwt.JwtProperties;
import com.example.danguen.domain.user.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminUserApiTest extends BaseTest {

    @DisplayName("관리자가 특정 유저 삭제")
    @Test
    public void successDeleteSomeUserByAdmin() throws Exception {
        //when
        mockMvc.perform(delete("/admin/user/" + otherUserId)
                        .header(JwtProperties.HEADER, makeJwtValue(Role.ADMIN)))
                .andExpect(status().isOk());

        //then
        assertThat(userService.getUserByEmail(otherUserEmail)).isEmpty();
    }

    @DisplayName("익명유저가 특정 유저 삭제")
    @Test
    public void failDeleteSomeUserByAnonymous() throws Exception {
        //when
        mockMvc.perform(delete("/admin/user/" + otherUserId))
                .andExpect(status().isUnauthorized());

        //then
        assertThat(userService.getUserByEmail(otherUserEmail)).isNotEmpty();
    }

    @DisplayName("일반유저가 특정 유저 삭제")
    @Test
    public void failDeleteSomeUserByUser() throws Exception {
        //when
        mockMvc.perform(delete("/admin/user/" + otherUserId)
                        .header(JwtProperties.HEADER, makeJwtValue(Role.USER)))
                .andExpect(status().isForbidden());

        //then
        assertThat(userService.getUserByEmail(otherUserEmail)).isNotEmpty();
    }
}
