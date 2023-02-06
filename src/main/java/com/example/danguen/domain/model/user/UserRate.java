package com.example.danguen.domain.model.user;

import com.example.danguen.domain.model.comment.dto.request.review.Review;
import lombok.Getter;

import javax.persistence.Embeddable;


@Getter
@Embeddable
public class UserRate {
    private float dealTemperature;
    private int totalReviewScore;
    private int totalDealCount;
    private float reDealHopePercent;
    private float responseRate;

    public UserRate() {
        dealTemperature = 36.5f;
        totalReviewScore = 0;
        totalDealCount = 0;
        reDealHopePercent = 0;
        responseRate = 0;
    }

    public void applyReview(Review review) {
        totalReviewScore += review.getReDealHope();
        totalDealCount++;

        reDealHopePercent = totalReviewScore * 10 / totalDealCount;

        for (boolean answer : review.getPositiveAnswer()) {
            if(answer) {
                dealTemperature += 0.001f;
                dealTemperature = Math.min(100f, dealTemperature);
            }
        }

        for (boolean answer : review.getNegativeAnswer()) {
            if(answer) {
                dealTemperature -= 0.001f;
                dealTemperature = Math.max(0f, dealTemperature);
            }
        }
    }
}
