package com.example.danguen.domain.user.dto.response;

import com.example.danguen.domain.Address;
import com.example.danguen.domain.user.User;
import com.example.danguen.domain.user.UserRate;
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
