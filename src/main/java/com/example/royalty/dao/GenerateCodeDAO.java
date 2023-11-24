package com.example.royalty.dao;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateCodeDAO {
    private long productId;
    @Positive(message = "Count must be greater than 0.")
    private int count;
}
