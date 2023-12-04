package com.example.royalty.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmsRequest {
    private String phone_number;
    private String shortcode;
    private String message;

    @Override
    public String toString() {
        return "SmsRequest{" +
                "phone_number='" + phone_number + '\'' +
                ", shortcode='" + shortcode + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

