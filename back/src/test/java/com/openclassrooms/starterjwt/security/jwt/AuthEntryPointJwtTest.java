package com.openclassrooms.starterjwt.security.jwt;

import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
class AuthEntryPointJwtTest {

	@InjectMocks
	private AuthEntryPointJwt authEntryPointJwt;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private AuthenticationException authException;

	@Test
	void commence_ShouldReturnUnauthorized() throws IOException, ServletException {
		when(response.getOutputStream()).thenReturn(mock(ServletOutputStream.class));


		authEntryPointJwt.commence(request, response, authException);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		verify(response).setContentType("application/json");
	}
}
