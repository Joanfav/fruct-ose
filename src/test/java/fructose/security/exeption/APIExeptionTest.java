package fructose.security.exeption;

import fructose.security.exception.APIException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class APIExeptionTest {
	
	@Test
	void testGetMessage() {
		APIException exception = new APIException(HttpStatus.BAD_REQUEST, "Bad Request") {
		};
		assertEquals("Bad Request", exception.getMessage());
	}
	
	@Test
	void testGetStatus() {
		APIException exception = new APIException(HttpStatus.BAD_REQUEST, "Bad Request") {
		};
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}
}
