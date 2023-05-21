package com.example.danguen.domain.user.dto.response;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.entity.UserRate;
import lombok.Data;

@Data
public class ResponseUserPageDto {

    //Image profileImage;
    private String name;
    private Address address;
    private UserRate rate;

    public static ResponseUserPageDto toResponse(User user) {
        ResponseUserPageDto dto = new ResponseUserPageDto();
        dto.name = user.getName();
        dto.address = user.getAddress();
        dto.rate = user.getRate();

        return dto;
    }

}
