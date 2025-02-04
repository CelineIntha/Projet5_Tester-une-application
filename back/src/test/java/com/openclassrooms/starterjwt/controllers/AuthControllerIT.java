package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.annotations.IT;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IT
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@AfterEach
	public void tearDown() {
		userRepository.deleteAll();
	}

	/**
	 * Teste l'inscription d'un utilisateur valide.
	 * Vérifie que la requête retourne un statut HTTP 200 et un message de succès.
	 */
	@Test
	public void registerUser_ShouldReturnSuccess_WhenUserIsValid() throws Exception {
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setEmail("john@email.com");
		signupRequest.setPassword("password");
		signupRequest.setFirstName("John");
		signupRequest.setLastName("Doe");

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(signupRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("User registered successfully!"));
	}

	/**
	 * Teste l'inscription d'un utilisateur avec un email déjà existant.
	 * Vérifie que la requête retourne un statut HTTP 400 et un message d'erreur.
	 */
	@Test
	public void registerUser_ShouldReturnBadRequest_WhenEmailAlreadyExists() throws Exception {
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setEmail("existing_email@email.com");
		signupRequest.setPassword("password");
		signupRequest.setFirstName("Jane");
		signupRequest.setLastName("Doe");

		userRepository.save(new User(
				signupRequest.getEmail(),
				signupRequest.getLastName(),
				signupRequest.getFirstName(),
				"encoded_password",
				false));

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(signupRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
	}

	/**
	 * Teste l'authentification avec des identifiants incorrects.
	 * Vérifie que la requête retourne un statut HTTP 401 (Unauthorized).
	 */
	@Test
	public void authenticateUser_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("wrong_email@example.com");
		loginRequest.setPassword("wrong_password");

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isUnauthorized());
	}

	/**
	 * Teste l'authentification avec des identifiants valides.
	 * Vérifie que la requête retourne un statut HTTP 200 et un token JWT.
	 */
	@Test
	public void authenticateUser_ShouldReturnSuccess_WhenCredentialsAreValid() throws Exception {
		String encodedPassword = passwordEncoder.encode("encoded_password");

		User user = new User("john_doe@email.com", "Doe", "John", encodedPassword, false);
		userRepository.save(user);

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("john_doe@email.com");
		loginRequest.setPassword("encoded_password");

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists())
				.andExpect(jsonPath("$.username").value("john_doe@email.com"));
	}

	/**
	 * Teste l'authentification avec un mot de passe incorrect.
	 * Vérifie que la requête retourne un statut HTTP 401 (Unauthorized).
	 */
	@Test
	public void authenticateUser_ShouldReturnUnauthorized_WhenPasswordIsIncorrect() throws Exception {
		User user = new User("john_doe@email.com", "Doe", "John", "encoded_password", false);
		userRepository.save(user);

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("user@email.com");
		loginRequest.setPassword("wrong_password");

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isUnauthorized());
	}
}
