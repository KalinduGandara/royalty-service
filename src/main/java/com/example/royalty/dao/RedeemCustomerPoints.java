package com.example.royalty.dao;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedeemCustomerPoints {
    @Positive(message = "Points should be positive.")
    int redeemPoints;
}
