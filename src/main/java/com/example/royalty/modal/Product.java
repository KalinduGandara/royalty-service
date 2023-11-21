package com.example.royalty.modal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotEmpty(message = "Field name is required.")
    private String name;

    @NotEmpty(message = "Field code is required.")
    @Column(unique = true)
    private String code;

    @PositiveOrZero(message = "Capacity must be greater than 0.")
    @NotNull(message = "Field capacity is required.")
    private Integer capacity;

    private String description;

    @PositiveOrZero(message = "Points must be greater than 0.")
    @NotNull(message = "Field points is required.")
    private Integer points;

    @OneToMany(mappedBy = "product")
    private List<Code> codes;

}