package com.openclassrooms.starterjwt.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

	@InjectMocks
	private JwtUtils jwtUtils;

	@Mock
	private Authentication authentication;

	@Mock
	private UserDetailsImpl userDetails;

	private final String jwtSecret = "testSecretKey";
	private final int jwtExpirationMs = 1000;

	@BeforeEach
	void setUp() throws Exception {
		// Injecte les valeurs dans JwtUtils (normalement injectées via @Value)
		jwtUtils = new JwtUtils();
		java.lang.reflect.Field secretField = JwtUtils.class.getDeclaredField("jwtSecret");
		secretField.setAccessible(true);
		secretField.set(jwtUtils, jwtSecret);

		java.lang.reflect.Field expirationField = JwtUtils.class.getDeclaredField("jwtExpirationMs");
		expirationField.setAccessible(true);
		expirationField.set(jwtUtils, jwtExpirationMs);
	}

	/**
	 * Teste la génération d'un JWT et s'assure qu'il est non null.
	 */
	@Test
	void generateJwtToken_ShouldReturnValidToken() {
		when(authentication.getPrincipal()).thenReturn(userDetails);
		when(userDetails.getUsername()).thenReturn("testUser");

		String token = jwtUtils.generateJwtToken(authentication);

		assertNotNull(token, "Le token JWT ne doit pas être null");
		assertTrue(token.length() > 10, "Le token doit être suffisamment long");
	}

	/**
	 * Teste la récupération du nom d'utilisateur à partir du token.
	 */
	@Test
	void getUserNameFromJwtToken_ShouldReturnUsername() {
		String username = "testUser";
		String token = Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();

		String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);

		assertEquals(username, extractedUsername, "Le nom d'utilisateur extrait doit être identique à celui du token");
	}

	/**
	 * Teste la validation d'un token valide.
	 */
	@Test
	void validateJwtToken_ShouldReturnTrue_WhenTokenIsValid() {
		String token = Jwts.builder()
				.setSubject("testUser")
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();

		assertTrue(jwtUtils.validateJwtToken(token), "Le token valide doit être accepté");
	}

	/**
	 * Teste la validation d'un token expiré.
	 */
	@Test
	void validateJwtToken_ShouldReturnFalse_WhenTokenIsExpired() throws InterruptedException {
		String token = Jwts.builder()
				.setSubject("testUser")
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 100)) // Expire après 100ms
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();

		Thread.sleep(200); // Attendre que le token expire

		assertFalse(jwtUtils.validateJwtToken(token), "Un token expiré ne doit pas être valide");
	}

	/**
	 * Teste la validation d'un token malformé.
	 */
	@Test
	void validateJwtToken_ShouldReturnFalse_WhenTokenIsMalformed() {
		String malformedToken = "this.is.a.fake.token";

		assertFalse(jwtUtils.validateJwtToken(malformedToken), "Un token malformé ne doit pas être valide");
	}

	/**
	 * Teste la validation d'un token avec une signature incorrecte.
	 */
	@Test
	void validateJwtToken_ShouldReturnFalse_WhenTokenHasInvalidSignature() {
		String tokenWithDifferentKey = Jwts.builder()
				.setSubject("testUser")
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, "wrongSecretKey")
				.compact();

		assertFalse(jwtUtils.validateJwtToken(tokenWithDifferentKey), "Un token avec une signature incorrecte ne doit pas être valide");
	}

	/**
	 * Teste la validation d'un token vide.
	 */
	@Test
	void validateJwtToken_ShouldReturnFalse_WhenTokenIsEmpty() {
		assertFalse(jwtUtils.validateJwtToken(""), "Un token vide ne doit pas être valide");
	}

	/**
	 * Teste la validation d'un token null.
	 */
	@Test
	void validateJwtToken_ShouldReturnFalse_WhenTokenIsNull() {
		assertFalse(jwtUtils.validateJwtToken(null), "Un token null ne doit pas être valide");
	}

	/**
	 * Teste la validation d'un token non supporté.
	 */
	@Test
	void validateJwtToken_ShouldThrowUnsupportedJwtException() {
		String unsupportedToken = Jwts.builder()
				.setPayload("invalid-payload")
				.compact();

		assertFalse(jwtUtils.validateJwtToken(unsupportedToken), "Un token non supporté ne doit pas être valide");
	}

}
