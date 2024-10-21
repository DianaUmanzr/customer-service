package com.flxpoint.customer.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter
@Getter
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;

}
