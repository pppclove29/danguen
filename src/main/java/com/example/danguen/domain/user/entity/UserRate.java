package com.example.danguen.domain.user.entity;

import com.example.danguen.domain.review.RequestReviewDto;
import lombok.Getter;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;


@Getter
@Entity
public class UserRate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "RATE_ID")
    private Long id;
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

    public void applyReview(RequestReviewDto review) {
        totalReviewScore += review.getReDealHope();
        totalDealCount++;

        reDealHopePercent = totalReviewScore * 10.0f / totalDealCount;

        for (boolean answer : review.getPositiveAnswer()) {
            if (answer) {
                dealTemperature += 0.001f;
                dealTemperature = Math.min(100f, dealTemperature);
            }
        }

        for (boolean answer : review.getNegativeAnswer()) {
            if (answer) {
                dealTemperature -= 0.001f;
                dealTemperature = Math.max(0f, dealTemperature);
            }
        }
    }
}
