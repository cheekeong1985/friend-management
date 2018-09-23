package com.test.friendmanagement.repository;

import com.test.friendmanagement.domain.Relationship;
import com.test.friendmanagement.domain.RelationshipId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface RelationshipRepository extends CrudRepository<Relationship, RelationshipId>,
        RelationshipRepositoryCustom, QuerydslPredicateExecutor<Relationship> {
}
