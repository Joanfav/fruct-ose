package fructose.security;

import fructose.security.exception.InvalidJwtTokenException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Method;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class JwtTokenProviderTest {
	
	private JwtTokenProvider jwtTokenProvider;
	
	@BeforeEach
	void setUp() {
		jwtTokenProvider = new JwtTokenProvider();
		setField(jwtTokenProvider, "jwtSecret", "f1010ddd5248cf015267e355fed260207ddbbb3448bd13ae360b8a63a9fd4522c32ff6573756b4460f1e0c052f0c1ecf14caa8d9a454dbae9eb1994be309633e3a930879f186ac536554c07e11c84c0a7f7c8301564e2ba6f8c3ec8da682c7731dfef09b9416de14625f906a58de8a14c72c99f83fb8e1d7750f931f67a5e10d5f47f804fe790e48183d6a9adaff03c0d9dade160bbdc0f8aa65988387ebab41544fb7e950d97d90520e08caf93896ac100f3c1fe49e1426733db46b743e5d4c4fff45487abce6720af1a80cc1d82489fe7c1140a703d127f8314d10203cee28be14b12bd26787c13e5b454f34a4a310a6b891b4df5d664e5a292e30622680a0");
		setField(jwtTokenProvider, "expirationInMs", 3600000);
	}
	
	@Test
	public void test_generate_valid_jwt_token() {
		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(authentication.getName()).thenReturn("user@example.com");
		Mockito.when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
		
		String token = jwtTokenProvider.generateToken(authentication);
		
		assertNotNull(token);
		assertFalse(token.isEmpty());
	}
	
	@Test
	public void test_handle_expired_jwt_token() throws Exception {
		Method keyMethod = JwtTokenProvider.class.getDeclaredMethod("key");
		keyMethod.setAccessible(true);
		Key key = (Key) keyMethod.invoke(jwtTokenProvider);
		
		String expiredToken = Jwts.builder()
			.setSubject("user@example.com")
			.setIssuedAt(new Date(System.currentTimeMillis() - 3600000))
			.setExpiration(new Date(System.currentTimeMillis() - 1800000))
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();
		
		InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () -> {
			jwtTokenProvider.validateToken(expiredToken);
		});
		
		assertEquals("Expired JWT token", exception.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}
	
	@Test
	public void test_handle_malformed_jwt_token() {
		String malformedToken = "malformed.jwt.token";
		
		InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () -> {
			jwtTokenProvider.validateToken(malformedToken);
		});
		
		assertEquals("Invalid JWT token", exception.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}
	
	@Test
	public void test_handle_unsupported_jwt_token() throws Exception {
		// Generate RSA key pair
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();
		
		String unsupportedToken = Jwts.builder()
			.setPayload("{ \"sub\": \"user@example.com\" }")
			.signWith(privateKey, SignatureAlgorithm.RS256)  // Use an unsupported algorithm
			.compact();
		
		InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () -> {
			jwtTokenProvider.validateToken(unsupportedToken);
		});
		
		assertEquals("Unsupported JWT token", exception.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}
	
	@Test
	public void test_handle_empty_jwt_token() {
		InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () -> {
			jwtTokenProvider.validateToken("");
		});
		
		assertEquals("JWT claims string is empty", exception.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}
	
	@Test
	public void test_extract_email_from_valid_jwt_token() {
		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(authentication.getName()).thenReturn("user@example.com");
		Mockito.when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
		
		String token = jwtTokenProvider.generateToken(authentication);
		String extractedEmail = jwtTokenProvider.getEmailFromJWT(token);
		
		assertEquals("user@example.com", extractedEmail);
	}
	
	@Test
	public void validate_non_expired_jwt_token() {
		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(authentication.getName()).thenReturn("user@example.com");
		Mockito.when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
		
		String token = jwtTokenProvider.generateToken(authentication);
		
		assertDoesNotThrow(() -> jwtTokenProvider.validateToken(token));
	}
	
	@Test
	public void test_handle_invalid_jwt_signature() {
		String token = "valid.token"; // Simulate a valid token
		
		// Simulate a SecurityException during token validation
		JwtParser parser = Mockito.mock(JwtParser.class);
		Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode("f1010ddd5248cf015267e355fed260207ddbbb3448bd13ae360b8a63a9fd4522c32ff6573756b4460f1e0c052f0c1ecf14caa8d9a454dbae9eb1994be309633e3a930879f186ac536554c07e11c84c0a7f7c8301564e2ba6f8c3ec8da682c7731dfef09b9416de14625f906a58de8a14c72c99f83fb8e1d7750f931f67a5e10d5f47f804fe790e48183d6a9adaff03c0d9dade160bbdc0f8aa65988387ebab41544fb7e950d97d90520e08caf93896ac100f3c1fe49e1426733db46b743e5d4c4fff45487abce6720af1a80cc1d82489fe7c1140a703d127f8314d10203cee28be14b12bd26787c13e5b454f34a4a310a6b891b4df5d664e5a292e30622680a0"));
		Mockito.when(parser.setSigningKey(key)).thenThrow(new SecurityException("Invalid signature"));
		
		InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () -> {
			jwtTokenProvider.validateToken(token);
		});
		
		assertEquals("Invalid JWT token", exception.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}
}