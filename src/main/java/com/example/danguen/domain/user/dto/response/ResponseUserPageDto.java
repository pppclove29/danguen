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

    public ResponseUserPageDto(User user) {
        this.name = user.getName();
        this.address = user.getAddress();
        this.rate = user.getRate();
    }

}
