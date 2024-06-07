package com.example.royalty.modal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "received_message")
@Getter
@Setter
@NoArgsConstructor
public class ReceivedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private String phone;
    private Long cid;

    @Column(name="product_id")
    private Long productId;

    private String status;
    private LocalDateTime receivedTime;

}