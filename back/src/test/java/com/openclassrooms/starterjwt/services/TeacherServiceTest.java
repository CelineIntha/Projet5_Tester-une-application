package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

	@Mock
	private TeacherRepository teacherRepository;

	@InjectMocks
	private TeacherService teacherService;

	/**
	 * Teste que la méthode `findAll` retourne une liste contenant tous les enseignants.
	 * - Le mock retourne une liste de deux enseignants.
	 * - Vérifie que le service retourne correctement cette liste.
	 */
	@Test
	void findAll_shouldReturnAllTeachers() {
		List<Teacher> teachers = new ArrayList<>();
		teachers.add(new Teacher());
		teachers.add(new Teacher());

		when(teacherRepository.findAll()).thenReturn(teachers);

		List<Teacher> result = teacherService.findAll();

		assertNotNull(result); // Vérifie que le résultat n'est pas null
		assertEquals(teachers, result); // Vérifie que la liste retournée correspond à celle simulée

		verify(teacherRepository, times(1)).findAll(); // Vérifie que `findAll` a été appelé une fois
	}

	/**
	 * Teste que la méthode `findAll` retourne une liste vide si aucun enseignant n'existe.
	 * - Le mock retourne une liste vide.
	 * - Vérifie que le service retourne une liste vide.
	 */
	@Test
	void findAll_shouldReturnEmptyListIfNoTeachers() {
		List<Teacher> teachers = new ArrayList<>();

		when(teacherRepository.findAll()).thenReturn(teachers);

		List<Teacher> result = teacherService.findAll();

		assertNotNull(result); // Vérifie que le résultat n'est pas null
		assertTrue(result.isEmpty()); // Vérifie que la liste retournée est vide

		verify(teacherRepository, times(1)).findAll(); // Vérifie que `findAll` a été appelé une fois
	}

	/**
	 * Teste que la méthode `findById` retourne un enseignant si celui-ci existe.
	 * - Le mock retourne un enseignant pour l'ID donné.
	 * - Vérifie que le service retourne correctement cet enseignant.
	 */
	@Test
	void findById_shouldReturnTeacherIfExists() {
		Long teacherId = 1L;
		Teacher teacher = new Teacher();
		teacher.setId(teacherId);

		when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

		Teacher result = teacherService.findById(teacherId);

		assertNotNull(result); // Vérifie que le résultat n'est pas null
		assertEquals(teacher, result); // Vérifie que l'enseignant retourné correspond à celui simulé

		verify(teacherRepository, times(1)).findById(teacherId); // Vérifie que `findById` a été appelé une fois avec le bon ID
	}

	/**
	 * Teste que la méthode `findById` retourne null si l'enseignant n'existe pas.
	 * - Le mock retourne une valeur vide (Optional.empty) pour l'ID donné.
	 * - Vérifie que le service retourne `null`.
	 */
	@Test
	void findById_shouldReturnNullIfTeacherNotExists() {
		Long teacherId = 1L;

		when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

		Teacher result = teacherService.findById(teacherId);

		assertNull(result);

		verify(teacherRepository, times(1)).findById(teacherId); // Vérifie que `findById` a été appelé une fois avec le bon ID
	}
}
