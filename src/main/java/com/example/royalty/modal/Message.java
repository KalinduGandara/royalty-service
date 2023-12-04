package com.example.royalty.modal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Getter
@Setter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private String phone;

    private Long cid;

    private LocalDateTime createTime;

    private Boolean send;
    private LocalDateTime sendTime;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", phone='" + phone + '\'' +
                ", cid=" + cid +
                ", createTime=" + createTime +
                ", send=" + send +
                ", sendTime=" + sendTime +
                '}';
    }
}