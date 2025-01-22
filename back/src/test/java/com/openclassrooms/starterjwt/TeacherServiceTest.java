package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherServiceTest {

	@Mock
	private TeacherRepository teacherRepository;

	@InjectMocks
	private TeacherService teacherService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void findAll_shouldReturnAllTeachers() {
		List<Teacher> teachers = new ArrayList<>();
		teachers.add(new Teacher());
		teachers.add(new Teacher());

		when(teacherRepository.findAll()).thenReturn(teachers);

		List<Teacher> result = teacherService.findAll();

		assertNotNull(result);
		assertEquals(teachers, result);

		verify(teacherRepository, times(1)).findAll();
	}

	@Test
	void findAll_shouldReturnEmptyListIfNoTeachers() {
		List<Teacher> teachers = new ArrayList<>();

		when(teacherRepository.findAll()).thenReturn(teachers);

		List<Teacher> result = teacherService.findAll();

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(teacherRepository, times(1)).findAll();
	}

	@Test
	void findById_shouldReturnTeacherIfExists() {
		Long teacherId = 1L;
		Teacher teacher = new Teacher();
		teacher.setId(teacherId);

		when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

		Teacher result = teacherService.findById(teacherId);

		assertNotNull(result);
		assertEquals(teacher, result);

		verify(teacherRepository, times(1)).findById(teacherId);
	}

	@Test
	void findById_shouldReturnNullIfTeacherNotExists() {
		Long teacherId = 1L;

		when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

		Teacher result = teacherService.findById(teacherId);

		assertNull(result);

		verify(teacherRepository, times(1)).findById(teacherId);
	}
}
