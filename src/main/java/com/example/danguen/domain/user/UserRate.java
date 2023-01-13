package com.example.danguen.domain.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;


@NoArgsConstructor
@Getter
@Embeddable
public class UserRate {
    private float dealTemperature;
    private float reDealHopePercent;
    private float responseRate;
}
