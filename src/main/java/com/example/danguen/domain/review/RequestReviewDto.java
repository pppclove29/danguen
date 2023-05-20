package com.example.danguen.domain.review;

import lombok.Data;

@Data
public class RequestReviewDto {

    int dealScore; // 거래자와의 거래에 대한 점수를 매긴다 1 ~ 10

    private boolean[] positiveAnswer = new boolean[10];
    private boolean[] negativeAnswer = new boolean[10];

    public int getReDealHope() {
        return dealScore;
    }

    public boolean[] getPositiveAnswer() {
        return positiveAnswer;
    }

    public boolean[] getNegativeAnswer() {
        return negativeAnswer;
    }
}
