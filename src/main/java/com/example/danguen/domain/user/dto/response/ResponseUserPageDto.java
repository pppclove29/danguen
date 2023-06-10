package com.example.danguen.domain.user.dto.response;

import com.example.danguen.domain.base.Address;
import com.example.danguen.domain.user.entity.User;
import com.example.danguen.domain.user.entity.UserRate;
import lombok.Data;

@Data
public class ResponseUserPageDto {

    private Long id;
    private String name;
    private UserRate rate;

    /*
    todo
     활동 배지
     판매상품
     받은 매너평가
     대표 매너평가
     거래 후기
     재거래 희망률
     응답률
     */

    public static ResponseUserPageDto toResponse(User user) {
        ResponseUserPageDto dto = new ResponseUserPageDto();

        dto.id = user.getId();
        dto.name = user.getName();
        dto.rate = user.getRate();

        return dto;
    }

}
