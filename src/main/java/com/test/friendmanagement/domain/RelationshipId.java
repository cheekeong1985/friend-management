package com.test.friendmanagement.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Email;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RelationshipId implements Serializable {
    @Email
    @Column(name = "USER_EMAIL", nullable = false)
    private String userEmail;

    @Email
    @Column(name = "FRIEND_EMAIL", nullable = false)
    private String friendEmail;
}
