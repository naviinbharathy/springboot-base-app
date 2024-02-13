package com.avega.portfolio.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.avega.portfolio.constants.Constants;
import com.avega.portfolio.enums.ERole;
import com.avega.portfolio.exception.TokenRefreshException;
import com.avega.portfolio.payload.request.LoginRequest;
import com.avega.portfolio.payload.request.SignupRequest;
import com.avega.portfolio.payload.response.Response;
import com.avega.portfolio.payload.response.UserInfoResponse;
import com.avega.portfolio.security.jwt.JwtUtils;
import com.avega.portfolio.security.services.RefreshTokenService;
import com.avega.portfolio.security.services.UserDetailsImpl;
import com.avega.portfolio.user.model.RefreshToken;
import com.avega.portfolio.user.model.Role;
import com.avega.portfolio.user.model.User;
import com.avega.portfolio.user.repository.RoleRepository;
import com.avega.portfolio.user.repository.UserRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

//@CrossOrigin(origins = "*", maxAge = 3600)
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
//@AllArgsConstructor
@Tag(name = "AuthController", description = "REST API's Related To Perform Auth Operation !! ")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	RefreshTokenService refreshTokenService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).toList();

		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUserId());

		ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
				.header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString()).body(new UserInfoResponse(
						userDetails.getUserId(), userDetails.getUsername(), userDetails.getEmail(), roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUserName(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new Response("Error: Username is already taken!", true));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new Response("Error: Email is already in use!", true));
		}

		// Create new user's account User user = new
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException(Constants.ROLE_NOT_FOUND));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException(Constants.ROLE_NOT_FOUND));
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException(Constants.ROLE_NOT_FOUND));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException(Constants.ROLE_NOT_FOUND));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new Response("User registered successfully!", true));
	}

	@PostMapping("/signout")
	public ResponseEntity<?> logoutUser() {
		Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!"anonymousUser".equals(principle.toString())) {
			String userId = ((UserDetailsImpl) principle).getUserId();
			refreshTokenService.deleteByUserId(userId);
		}

		ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
		ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
				.header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
				.body(new Response("You've been signed out!", true));
	}

	@PostMapping("/refreshtoken")
	public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
		String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

		if ((refreshToken != null) && (refreshToken.length() > 0)) {
			return refreshTokenService.findByToken(refreshToken).map(refreshTokenService::verifyExpiration)
					.map(RefreshToken::getUser).map(user -> {
						ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);

						return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
								.body(new Response("Token is refreshed successfully!", true));
					}).orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token is not in database!"));
		}

		return ResponseEntity.badRequest().body(new Response("Refresh Token is empty!", false));
	}
}
