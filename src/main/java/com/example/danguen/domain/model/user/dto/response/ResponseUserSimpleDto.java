package com.example.danguen.domain.model.user.dto.response;

import com.example.danguen.domain.model.user.User;
import lombok.Data;

@Data
public class ResponseUserSimpleDto {
    String name;
    String picture;

    public static ResponseUserSimpleDto toDto(User user){
        ResponseUserSimpleDto dto = new ResponseUserSimpleDto();

        dto.name = user.getName();
        dto.picture = user.getImage().getUrl();

        return dto;
    }
}
