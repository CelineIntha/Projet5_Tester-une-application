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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IT // Remplace @SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@AfterEach
	public void tearDown() {
		userRepository.deleteAll();
	}

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
}
