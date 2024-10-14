package com.eventease.eventease_service.repository;

import com.eventease.eventease_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findById(long id);
}
