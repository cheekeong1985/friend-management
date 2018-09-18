package com.test.friendmanagement.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Data
@Entity
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Email
    @Column(name = "USER_EMAIL", nullable = false)
    private String userEmail;

    @Email
    @Column(name = "FRIEND_EMAIL", nullable = false)
    private String friendEmail;

    @Column(name = "ARE_FRIENDS", nullable = false)
    private boolean areFriends;

    @Column(name = "IS_BLOCKED", nullable = false)
    private boolean isBlocked;

    @Column(name = "IS_SUBSCRIBED", nullable = false)
    private boolean isSubscribed;
}
