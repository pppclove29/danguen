package com.example.danguen.domain.user.dto.request;

import com.example.danguen.domain.Address;
import com.example.danguen.domain.user.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
public class RequestUserUpdateDto{
    //Image profileImage;
    private String name;
    private Address address;
}
