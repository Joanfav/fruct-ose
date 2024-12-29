package fructose.security.exeption;

import fructose.security.exception.AuthenticationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticationExceptionTest {
	
	@Test
	void testAuthenticationException() {
		AuthenticationException exception = new AuthenticationException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
		assertEquals("Unauthorized", exception.getMessage());
	}
}