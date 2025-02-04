package com.openclassrooms.starterjwt.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

	@InjectMocks
	private AuthTokenFilter authTokenFilter;

	@Mock
	private JwtUtils jwtUtils;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;


	/**
	 * Teste si une requête sans token passe sans authentification.
	 */
	@Test
	void doFilterInternal_ShouldNotAuthenticate_WhenNoTokenProvided() throws ServletException, IOException {
		when(request.getHeader("Authorization")).thenReturn(null);

		authTokenFilter.doFilterInternal(request, response, filterChain);

		SecurityContext securityContext = SecurityContextHolder.getContext();
		assertNull(securityContext.getAuthentication(), "Aucune authentification ne doit être présente");

		verify(filterChain).doFilter(request, response);
	}

	/**
	 * Teste si un token invalide est rejeté.
	 */
	@Test
	void doFilterInternal_ShouldNotAuthenticate_WhenTokenIsInvalid() throws ServletException, IOException {
		String jwt = "invalid-token";

		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
		when(jwtUtils.validateJwtToken(jwt)).thenReturn(false);

		authTokenFilter.doFilterInternal(request, response, filterChain);

		SecurityContext securityContext = SecurityContextHolder.getContext();
		assertNull(securityContext.getAuthentication(), "Un token invalide ne doit pas authentifier l'utilisateur");

		verify(filterChain).doFilter(request, response);
	}

	/**
	 * Teste si une exception dans le processus d'authentification est bien gérée.
	 */
	@Test
	void doFilterInternal_ShouldHandleException_WhenErrorOccurs() throws ServletException, IOException {
		String jwt = "valid-token";

		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
		when(jwtUtils.validateJwtToken(jwt)).thenThrow(new RuntimeException("Unexpected error"));

		authTokenFilter.doFilterInternal(request, response, filterChain);

		SecurityContext securityContext = SecurityContextHolder.getContext();
		assertNull(securityContext.getAuthentication(), "L'authentification doit être annulée en cas d'erreur");

		verify(filterChain).doFilter(request, response);
	}
}
