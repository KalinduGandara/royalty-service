package com.example.royalty.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BulkMessage {
    String message;
    List<Long> cids;

    @Override
    public String toString() {
        return "BulkMessage{" +
                "message='" + message + '\'' +
                ", numbers=" + cids +
                '}';
    }
}
