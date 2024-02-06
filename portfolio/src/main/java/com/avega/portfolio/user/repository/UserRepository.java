package com.avega.portfolio.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avega.portfolio.user.model.User;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByUserName(String userName);

	Boolean existsByUserName(String userName);

	Boolean existsByEmail(String email);
}
