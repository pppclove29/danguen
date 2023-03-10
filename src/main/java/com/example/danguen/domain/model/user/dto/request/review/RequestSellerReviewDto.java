package com.example.danguen.domain.model.user.dto.request.review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestSellerReviewDto implements Review {

    int dealScore; // 거래자와의 거래에 대한 점수를 매긴다 1 ~ 10

    private boolean[] positiveAnswer = new boolean[10];
    private boolean[] negativeAnswer = new boolean[10];

    @Override
    public int getReDealHope() {
        return dealScore;
    }

    @Override
    public boolean[] getPositiveAnswer() {
        return positiveAnswer;
    }

    @Override
    public boolean[] getNegativeAnswer() {
        return negativeAnswer;
    }
}
