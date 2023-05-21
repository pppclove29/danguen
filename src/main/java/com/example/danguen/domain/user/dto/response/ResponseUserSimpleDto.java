package com.example.danguen.domain.user.dto.response;

import com.example.danguen.domain.user.entity.User;
import lombok.Data;

@Data
public class ResponseUserSimpleDto {
    String name;
    String picture;

    public static ResponseUserSimpleDto toResponse(User user){
        ResponseUserSimpleDto dto = new ResponseUserSimpleDto();

        dto.name = user.getName();
        dto.picture = user.getImage().getUrl();

        return dto;
    }
}
