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
    @NotEmpty(message = "Field phone is required.")
    private String phone;
//    @NotEmpty(message = "Field address is required.")
    private String address;
    @PositiveOrZero(message = "Points must be greater than 0.")
    private int points;
    private String notes;

    @Column(name = "tp_number")
//    @NotEmpty(message = "Field TP Number is required.")
    private String tpNumber;
    @NotEmpty(message = "Field city is required.")
    private String city;
    @NotEmpty(message = "Field district is required.")
    private String district;
    @NotEmpty(message = "Field province is required.")
    private String province;
    @NotEmpty(message = "Field Sales person Territory is required.")
    @Column(name = "sales_person_territory")
    private String salesPersonTerritory;
//    @NotEmpty(message = "Field Region is required.")
    private String region;
    @NotEmpty(message = "Field Assigned CMDE is required.")
    @Column(name = "assigned_cmde")
    private String assignedCMDE;
//    @NotEmpty(message = "Field Loyalty status is required.")
    @Column(name = "loyalty_status")
    private String loyaltyStatus;
//    @NotEmpty(message = "Field Current Ave consumption P/M is required.")
    @Column(name = "current_ave_consumption_pm")
    private String currentAveConsumptionPM;
//    @NotEmpty(message = "Field Linked dealer 1 is required.")
    @Column(name = "linked_dealer_1")
    private String linkedDealer1;
//    @NotEmpty(message = "Field Linked dealer 2 is required.")
    @Column(name = "linked_dealer_2")
    private String linkedDealer2;

}