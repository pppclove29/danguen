package com.example.danguen.domain.user.dto.response;

import com.example.danguen.domain.user.User;
import lombok.Data;

@Data
public class ResponseUserPageDto {

    public ResponseUserPageDto(User user){
      this.name = user.getName();
      this.address = user.getAddress();
      this.dealTemperature = user.getDealTemperature();
      this.reDealHopePercent = user.getReDealHopePercent();
      this.responseRate = user.getResponseRate();
    }

    //Image profileImage;
    String name;
    String address;

    float dealTemperature;
    float reDealHopePercent;
    float responseRate;

}
