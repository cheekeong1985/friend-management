package com.test.friendmanagement.repository;

import com.test.friendmanagement.domain.Users;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Users, String>,
        QuerydslPredicateExecutor<Users> {
}
