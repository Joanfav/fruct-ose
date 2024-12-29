package fructose.security.exeption;

import fructose.security.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserNotFoundExceptionTest {
	
	@Test
	void testUserNotFoundException() {
		UserNotFoundException exception = new UserNotFoundException();
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
		assertEquals("userNotFound", exception.getMessage());
	}
}