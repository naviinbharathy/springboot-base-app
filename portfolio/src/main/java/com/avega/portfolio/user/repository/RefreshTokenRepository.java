package com.avega.portfolio.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.avega.portfolio.user.model.RefreshToken;
import com.avega.portfolio.user.model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

	Optional<RefreshToken> findByToken(String token);

	@Modifying
	int deleteByUser(User user);
}
