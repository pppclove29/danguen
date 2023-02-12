package com.example.danguen.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.nio.Buffer;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Embeddable
public class Address {
    private String city;
    private String street;
    private String zipcode;

    @Override
    public String toString() {
        return city + " " + street + " " + zipcode;
    }
}
