package com.example.forecastgas.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class Person {
    @Id
    @GeneratedValue
    private Long id;


    private String firstname;
    private String lastname;
    private String email;


}
