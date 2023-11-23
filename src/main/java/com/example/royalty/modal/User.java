package com.example.royalty.modal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotEmpty(message = "Field name is required.")
    private String name;
    private String eid;
    @NotEmpty(message = "Field NIC is required.")
    private String nic;
    private String description;
    @NotEmpty(message = "Field roll is required.")
    private String roll;

}