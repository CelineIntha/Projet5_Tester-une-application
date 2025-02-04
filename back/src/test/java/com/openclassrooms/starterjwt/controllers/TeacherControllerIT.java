package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.annotations.IT;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IT
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TeacherControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TeacherRepository teacherRepository;

	@AfterEach
	public void tearDown() {
		teacherRepository.deleteAll();
	}

	@Test
	@WithMockUser(username = "authenticated_user@email.com")
	public void findById_ShouldReturnTeacher_WhenTeacherExists() throws Exception {
		Teacher teacher = Teacher.builder()
				.firstName("Jane")
				.lastName("Doe")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();
		teacherRepository.save(teacher);

		mockMvc.perform(get("/api/teacher/" + teacher.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(teacher.getId()))
				.andExpect(jsonPath("$.firstName").value("Jane"))
				.andExpect(jsonPath("$.lastName").value("Doe"));
	}

	@Test
	@WithMockUser(username = "authenticated_user@email.com")
	public void findById_ShouldReturnNotFound_WhenTeacherDoesNotExist() throws Exception {
		mockMvc.perform(get("/api/teacher/1234"))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "authenticated_user@email.com")
	public void findById_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
		mockMvc.perform(get("/api/teacher/invalidId"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "authenticated_user@email.com")
	public void findAll_ShouldReturnListOfTeachers_WhenTeachersExist() throws Exception {
		Teacher teacher1 = Teacher.builder()
				.firstName("Jane")
				.lastName("Doe")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		Teacher teacher2 = Teacher.builder()
				.firstName("John")
				.lastName("Doe")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		teacherRepository.save(teacher1);
		teacherRepository.save(teacher2);

		mockMvc.perform(get("/api/teacher"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].firstName").value("Jane"))
				.andExpect(jsonPath("$[1].firstName").value("John"));
	}

	@Test
	@WithMockUser(username = "authenticated_user@email.com")
	public void findAll_ShouldReturnEmptyList_WhenNoTeachersExist() throws Exception {
		mockMvc.perform(get("/api/teacher"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));
	}
}
