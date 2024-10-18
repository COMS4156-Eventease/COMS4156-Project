package com.eventease.eventease_service.repository;

import com.eventease.eventease_service.model.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
/**
 * Repository for the User class, which represents a user in the system.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> , JpaSpecificationExecutor<User> {
  User findById(long id);
}
