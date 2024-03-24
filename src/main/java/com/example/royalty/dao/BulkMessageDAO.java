package com.example.royalty.dao;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BulkMessageDAO {
    @NotEmpty(message = "Message is required.")
    String message;

    @NotEmpty(message = "Select Customers are required.")
    List<Long> cids;

    @Override
    public String toString() {
        return "BulkMessage{" +
                "message='" + message + '\'' +
                ", cids=" + cids +
                '}';
    }
}
