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

	/**
	 * Nettoie la base de données après chaque test pour éviter les conflits de données.
	 */
	@AfterEach
	public void tearDown() {
		userRepository.deleteAll();
	}

	/**
	 * Teste la récupération d'un utilisateur par son ID.
	 * Doit renvoyer 200 OK avec les informations de l'utilisateur si celui-ci existe.
	 */
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

	/**
	 * Teste la récupération d'un utilisateur qui n'existe pas.
	 * Doit renvoyer 404 Not Found.
	 */
	@Test
	@WithMockUser(username = "john.doe@email.com")
	public void findById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
		mockMvc.perform(get("/api/user/999"))
				.andExpect(status().isNotFound());
	}

	/**
	 * Teste la récupération d'un utilisateur avec un ID invalide.
	 * Doit renvoyer 400 Bad Request.
	 */
	@Test
	@WithMockUser(username = "john.doe@email.com")
	public void findById_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
		mockMvc.perform(get("/api/user/invalidId"))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Teste la suppression d'un utilisateur lorsqu'il est autorisé.
	 * Doit renvoyer 200 OK si l'utilisateur peut être supprimé.
	 */
	@Test
	@WithMockUser(username = "john.doe@email.com")
	public void deleteUser_ShouldReturnOk_WhenAuthorized() throws Exception {
		User user = new User("john.doe@email.com", "Doe", "John", "password", true);
		userRepository.save(user);

		mockMvc.perform(delete("/api/user/" + user.getId()))
				.andExpect(status().isOk());
	}

	/**
	 * Teste la suppression d'un utilisateur lorsqu'un autre utilisateur tente de le supprimer.
	 * Doit renvoyer 401 Unauthorized.
	 */
	@Test
	@WithMockUser(username = "another.user@email.com")
	public void deleteUser_ShouldReturnUnauthorized_WhenNotAuthorized() throws Exception {
		User user = new User("john.doe@email.com", "Doe", "John", "password", true);
		userRepository.save(user);

		mockMvc.perform(delete("/api/user/" + user.getId()))
				.andExpect(status().isUnauthorized());
	}

	/**
	 * Teste la suppression d'un utilisateur avec un ID invalide.
	 * Doit renvoyer 400 Bad Request.
	 */
	@Test
	@WithMockUser(username = "john.doe@email.com")
	public void deleteUser_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
		mockMvc.perform(delete("/api/user/invalidId"))
				.andExpect(status().isBadRequest());
	}
}
