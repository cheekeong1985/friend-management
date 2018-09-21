package com.test.friendmanagement.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "RELATIONSHIP")
public class Relationship {

    @EmbeddedId
    private RelationshipId relationshipId;

    @Column(name = "IS_FRIEND", nullable = false)
    private boolean isFriend;

    @Column(name = "IS_BLOCKED", nullable = false)
    private boolean isBlocked;

    @Column(name = "IS_SUBSCRIBED", nullable = false)
    private boolean isSubscribed;

}
