package com.test.friendmanagement.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.test.friendmanagement.domain.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

import static com.test.friendmanagement.domain.QRelationship.relationship;

public class RelationshipRepositoryImpl extends QuerydslRepositorySupport implements RelationshipRepositoryCustom {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    public RelationshipRepositoryImpl() {
        super(Relationship.class);
    }

    @Override
    public List<String> getFriendList(String email) {
        return jpaQueryFactory.select(relationship.relationshipId.friendEmail)
                .from(relationship)
                .where(relationship.relationshipId.userEmail.eq(email)
                        .and(relationship.isFriend.isTrue())).fetch();
    }

    @Override
    public List<String> getCommonFriendList(List<String> emails) {
        return jpaQueryFactory.select(relationship.relationshipId.friendEmail)
                .from(relationship)
                .where(relationship.relationshipId.userEmail
                        .in(emails)
                        .and(relationship.isFriend.isTrue()))
                .groupBy(relationship.relationshipId.friendEmail)
                .having(relationship.relationshipId.friendEmail
                        .count()
                        .gt(1))
                .fetch();
    }

    @Override
    public List<String> getUpdatesRecipients(String email) {
        return jpaQueryFactory.select(relationship.relationshipId.userEmail)
                .from(relationship)
                .where(relationship.relationshipId.friendEmail.eq(email)
                        .and(relationship.isBlocked.isFalse())
                        .and(relationship.isFriend.isTrue()
                                .or(relationship.isSubscribed.isTrue())))
                .fetch();
    }
}
