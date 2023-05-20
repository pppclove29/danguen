package com.example.danguen.domain.base;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@NoArgsConstructor
@Getter
@Embeddable
public class Address {
    private String city;
    private String street;
    private String zipcode;

    public Address(String city, String street, String zipcode) {
        this.city = city == null ? "" : city;
        this.street = street == null ? "" : street;
        this.zipcode = zipcode == null ? "" : zipcode;
    }

    @Override
    public String toString() {
        return city + " " + street + " " + zipcode;
    }

}
