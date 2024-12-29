package fructose.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthentificationEntryPointTest {
	
	@InjectMocks
	private JwtAuthentificationEntryPoint jwtAuthentificationEntryPoint;
	
	@Mock
	private HttpServletRequest request;
	
	@Mock
	private HttpServletResponse response;
	
	@Mock
	private AuthenticationException authException;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void testCommence_Unauthorized() throws IOException {
		String errorMessage = "Unauthorized access";
		when(authException.getMessage()).thenReturn(errorMessage);
		
		jwtAuthentificationEntryPoint.commence(request, response, authException);
		
		verify(response).setContentType("application/json");
		verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
	}
}