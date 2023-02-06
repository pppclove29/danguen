package com.example.danguen.domain.model.comment.dto.request;

import com.example.danguen.domain.Address;
import lombok.Data;

@Data
public class RequestUserUpdateDto{
    //Image profileImage;
    private String name;
    private Address address;
}
