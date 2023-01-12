package com.example.danguen.domain.user.dto.request;

import com.example.danguen.domain.user.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class RequestUserUpdateDto {
    //Image profileImage;
    private final String nickname;
    private final String address;
}
