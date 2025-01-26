package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
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
class SessionServiceTest {

	@Mock
	private SessionRepository sessionRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private SessionService sessionService;


	/**
	 * Teste que la méthode `create` sauvegarde correctement une session.
	 * - Vérifie que la méthode `save` du repository est appelée.
	 */
	@Test
	void create_shouldSaveSession() {
		Session session = new Session();
		when(sessionRepository.save(session)).thenReturn(session);

		Session result = sessionService.create(session);

		assertNotNull(result);
		verify(sessionRepository, times(1)).save(session); // Vérifie que la méthode 'save' est appelée une fois
	}

	/**
	 * Teste que la méthode `delete` supprime une session par son ID.
	 * - Vérifie que la méthode `deleteById` est appelée avec le bon ID.
	 */
	@Test
	void delete_shouldDeleteSessionById() {
		Long sessionId = 1L;

		sessionService.delete(sessionId);

		verify(sessionRepository, times(1)).deleteById(sessionId);
	}

	/**
	 * Teste que la méthode `findAll` retourne toutes les sessions disponibles.
	 * - Simule une liste de sessions.
	 * - Vérifie que la liste retournée correspond à celle simulée.
	 */
	@Test
	void findAll_shouldReturnAllSessions() {
		List<Session> sessions = new ArrayList<>();
		when(sessionRepository.findAll()).thenReturn(sessions);

		List<Session> result = sessionService.findAll();

		assertEquals(sessions, result);
		verify(sessionRepository, times(1)).findAll();
	}

	/**
	 * Teste que la méthode `getById` retourne une session existante.
	 * - Simule qu'une session est trouvée.
	 * - Vérifie que la session retournée correspond à celle simulée.
	 */
	@Test
	void getById_shouldReturnSessionIfExists() {
		Long sessionId = 1L;
		Session session = new Session();
		when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

		Session result = sessionService.getById(sessionId);

		assertNotNull(result);
		assertEquals(session, result);
		verify(sessionRepository, times(1)).findById(sessionId);
	}

	/**
	 * Teste que la méthode `getById` retourne null si la session n'existe pas.
	 * - Simule une absence de session pour un ID donné.
	 */
	@Test
	void getById_shouldReturnNullIfNotExists() {
		Long sessionId = 1L;
		when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

		Session result = sessionService.getById(sessionId);

		assertNull(result);
		verify(sessionRepository, times(1)).findById(sessionId);
	}

	/**
	 * Teste que la méthode `update` met à jour une session existante.
	 * - Simule la sauvegarde d'une session mise à jour.
	 */
	@Test
	void update_shouldUpdateSession() {
		Long sessionId = 1L;
		Session session = new Session();
		session.setId(sessionId);
		when(sessionRepository.save(session)).thenReturn(session);

		Session result = sessionService.update(sessionId, session);

		assertNotNull(result);
		assertEquals(sessionId, result.getId());
		verify(sessionRepository, times(1)).save(session);
	}

	/**
	 * Teste que la méthode `participate` ajoute un utilisateur à une session.
	 * - Simule une session et un utilisateur valides.
	 * - Vérifie que l'utilisateur est ajouté et que la session est sauvegardée.
	 */
	@Test
	void participate_shouldAddUserToSession() {
		Long sessionId = 1L;
		Long userId = 2L;

		Session session = new Session();
		session.setUsers(new ArrayList<>());
		User user = new User();
		user.setId(userId);

		when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(sessionRepository.save(session)).thenReturn(session);

		sessionService.participate(sessionId, userId);

		assertTrue(session.getUsers().contains(user));
		verify(sessionRepository, times(1)).save(session);
	}

	/**
	 * Teste que la méthode `participate` lève une exception NotFoundException
	 * si la session ou l'utilisateur n'existe pas.
	 */
	@Test
	void participate_shouldThrowNotFoundExceptionIfSessionOrUserNotFound() {
		Long sessionId = 1L;
		Long userId = 2L;

		when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));
	}

	/**
	 * Teste que la méthode `participate` lève une exception BadRequestException
	 * si l'utilisateur participe déjà à la session.
	 */
	@Test
	void participate_shouldThrowBadRequestExceptionIfUserAlreadyParticipates() {
		Long sessionId = 1L;
		Long userId = 2L;

		User user = new User();
		user.setId(userId);

		Session session = new Session();
		session.setUsers(List.of(user));

		when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		assertThrows(BadRequestException.class, () -> sessionService.participate(sessionId, userId));
	}

	/**
	 * Teste que la méthode `noLongerParticipate` retire un utilisateur d'une session.
	 */
	@Test
	void noLongerParticipate_shouldRemoveUserFromSession() {
		Long sessionId = 1L;
		Long userId = 2L;

		User user = new User();
		user.setId(userId);

		Session session = new Session();
		session.setUsers(new ArrayList<>(List.of(user)));

		when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
		when(sessionRepository.save(session)).thenReturn(session);

		sessionService.noLongerParticipate(sessionId, userId);

		assertFalse(session.getUsers().contains(user));
		verify(sessionRepository, times(1)).save(session);
	}

	/**
	 * Teste que la méthode `noLongerParticipate` lève une exception NotFoundException
	 * si la session n'existe pas.
	 */
	@Test
	void noLongerParticipate_shouldThrowNotFoundExceptionIfSessionNotFound() {
		Long sessionId = 1L;
		Long userId = 2L;

		when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(sessionId, userId));
	}

	/**
	 * Teste que la méthode `noLongerParticipate` lève une exception BadRequestException
	 * si l'utilisateur ne participe pas à la session.
	 */
	@Test
	void noLongerParticipate_shouldThrowBadRequestExceptionIfUserNotInSession() {
		Long sessionId = 1L;
		Long userId = 2L;

		Session session = new Session();
		session.setUsers(new ArrayList<>());

		when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

		assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(sessionId, userId));
	}
}
