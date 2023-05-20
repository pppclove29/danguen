package com.example.danguen.domain.user.dto.request;

import com.example.danguen.domain.base.Address;
import lombok.Data;

@Data
public class RequestUserUpdateDto{
    //Image profileImage;
    private String name;
    private Address address;
}
