package com.avega.portfolio.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avega.portfolio.enums.ERole;
import com.avega.portfolio.user.model.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
	Optional<Role> findByName(ERole name);
}
