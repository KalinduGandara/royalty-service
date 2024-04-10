package com.example.royalty.dao;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedeemPointsDAO {
     @Positive(message = "Points should be positive.")
    int points;

    @NotEmpty(message = "Select Customers are required.")
    List<Long> cids;

}
