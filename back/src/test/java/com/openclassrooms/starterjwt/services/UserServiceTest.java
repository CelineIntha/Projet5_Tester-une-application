package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	/**
	 * Teste que la méthode `delete` appelle correctement la suppression d'un utilisateur par son ID.
	 * - Un ID utilisateur est défini (userId = 1L).
	 * - Vérifie que la méthode `deleteById` du repository est appelée une fois avec le bon ID.
	 */
	@Test
	void delete_shouldDeleteUserById() {
		Long userId = 1L;

		userService.delete(userId);

		verify(userRepository, times(1)).deleteById(userId);
	}

	/**
	 * Teste que la méthode `findById` retourne un utilisateur si celui-ci existe.
	 * - Un ID utilisateur est défini (userId = 1L).
	 * - Simule un utilisateur trouvé dans le repository.
	 * - Vérifie que le service retourne correctement cet utilisateur.
	 */
	@Test
	void findById_shouldReturnUserIfExists() {
		Long userId = 1L;
		User user = new User();
		user.setId(userId);

		// Simule le comportement du repository : un utilisateur est trouvé pour cet ID
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		User result = userService.findById(userId);

		assertNotNull(result);
		// Vérifie que l'utilisateur retourné est celui simulé
		assertEquals(user, result);

		// Vérifie que la méthode `findById` du repository est appelée une fois avec le bon ID
		verify(userRepository, times(1)).findById(userId);
	}

	/**
	 * Teste que la méthode `findById` retourne null si l'utilisateur n'existe pas.
	 * - Un ID utilisateur est défini (userId = 1L).
	 * - Simule qu'aucun utilisateur n'est trouvé dans le repository.
	 * - Vérifie que le service retourne `null`.
	 */
	@Test
	void findById_shouldReturnNullIfUserNotExists() {
		Long userId = 1L;

		// Simule le comportement du repository : aucun utilisateur trouvé pour cet ID
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		User result = userService.findById(userId);

		assertNull(result);

		// Vérifie que la méthode `findById` du repository est appelée une fois avec le bon ID
		verify(userRepository, times(1)).findById(userId);
	}
}
