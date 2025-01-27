package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.annotations.IT;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IT
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@AfterEach
	public void tearDown() {
		userRepository.deleteAll();
	}

	@Test
	@WithMockUser(username = "john.doe@email.com")
	public void findById_ShouldReturnUser_WhenUserExists() throws Exception {
		User user = new User("john.doe@email.com", "Doe", "John", "password", true);
		userRepository.save(user);

		mockMvc.perform(get("/api/user/" + user.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(user.getId()))
				.andExpect(jsonPath("$.email").value("john.doe@email.com"));
	}

	@Test
	@WithMockUser(username = "john.doe@email.com")
	public void findById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
		mockMvc.perform(get("/api/user/999"))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "john.doe@email.com")
	public void findById_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
		mockMvc.perform(get("/api/user/invalidId"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "john.doe@email.com")
	public void deleteUser_ShouldReturnOk_WhenAuthorized() throws Exception {
		User user = new User("john.doe@email.com", "Doe", "John", "password", true);
		userRepository.save(user);

		mockMvc.perform(delete("/api/user/" + user.getId()))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "another.user@email.com")
	public void deleteUser_ShouldReturnUnauthorized_WhenNotAuthorized() throws Exception {
		User user = new User("john.doe@example.com", "Doe", "John", "password", true);
		userRepository.save(user);

		mockMvc.perform(delete("/api/user/" + user.getId()))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "john.doe@email.com")
	public void deleteUser_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
		mockMvc.perform(delete("/api/user/invalidId"))
				.andExpect(status().isBadRequest());
	}
}
