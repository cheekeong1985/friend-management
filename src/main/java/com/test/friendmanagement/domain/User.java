package com.test.friendmanagement.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Email
    @Column(name = "EMAIL")
    private String email;
}
