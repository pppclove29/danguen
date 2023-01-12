package com.example.danguen.domain.user.dto;

import com.example.danguen.domain.user.User;
import lombok.Builder;
import lombok.Data;

import java.awt.*;

@Data
public class RequestUserPageDto {

    public RequestUserPageDto(User user){
      this.nickname = user.getNickname();
      this.address = user.getAddress();
      this.dealTemperature = user.getDealTemperature();
      this.reDealHopePercent = user.getReDealHopePercent();
      this.responseRate = user.getResponseRate();
    }

    //Image profileImage;
    String nickname;
    String address;

    float dealTemperature;
    float reDealHopePercent;
    float responseRate;

}
