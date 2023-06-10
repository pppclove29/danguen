package com.example.danguen.user;

import com.example.danguen.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminUserApiTest extends BaseTest {

    @DisplayName("관리자가 특정 유저 삭제")
    @WithMockUser(value = "admin")
    @Test
    public void successDeleteSomeUserByAdmin() throws Exception {
        //when
        mockMvc.perform(delete("/admin/user/" + noneSessionUserId))
                .andExpect(status().isOk());

        //then
        assertThat(userService.getUserByEmail(noneSessionEmail)).isEmpty();
    }
}
