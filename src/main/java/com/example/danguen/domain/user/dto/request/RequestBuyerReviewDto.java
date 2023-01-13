package com.example.danguen.domain.user.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class RequestBuyerReviewDto {

    int dealScore; // 거래자와의 거래에 대한 점수를 매긴다 1 ~ 10

    List<Boolean> positiveCheck;
    List<Boolean> negativeCheck;
}
