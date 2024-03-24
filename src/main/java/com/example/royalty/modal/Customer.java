package com.example.royalty.modal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotEmpty(message = "Field name is required.")
    private String name;
    @NotEmpty(message = "Field NIC is required.")
    private String nic;
    @NotEmpty(message = "Field phone is required.")
    private String phone;
    @NotEmpty(message = "Field address is required.")
    private String address;
    @NotEmpty(message = "Field area is required.")
    private String area;
    @PositiveOrZero(message = "Points must be greater than 0.")
    private int points;
    private String notes;

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nic='" + nic + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", area='" + area + '\'' +
                ", points=" + points +
                ", notes='" + notes + '\'' +
                '}';
    }
}