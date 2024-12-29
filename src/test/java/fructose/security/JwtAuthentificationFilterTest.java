package fructose.security;

import fructose.model.Utilisateur;
import fructose.model.auth.Credentials;
import fructose.model.enumerator.Role;
import fructose.repository.UtilisateurRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtAuthentificationFilterTest {
	
	private JwtTokenProvider tokenProvider;
	private UtilisateurRepository userRepository;
	private JwtAuthentificationFilter filter;
	
	@BeforeEach
	public void setUp() {
		tokenProvider = mock(JwtTokenProvider.class);
		userRepository = mock(UtilisateurRepository.class);
		filter = new JwtAuthentificationFilter(tokenProvider, userRepository);
	}
	
	@Test
	public void test_extract_and_validate_jwt_success() throws ServletException, IOException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);
		
		String token = "Bearer valid.jwt.token";
		when(request.getHeader("Authorization")).thenReturn(token);
		when(tokenProvider.getEmailFromJWT("valid.jwt.token")).thenReturn("user@example.com");
		
		Utilisateur user = new Utilisateur();
		user.setCredentials(new Credentials("user@example.com", "password", Role.ETUDIANT));
		when(userRepository.findByEmail("user@example.com")).thenReturn(user);
		
		filter.doFilterInternal(request, response, filterChain);
		
		verify(tokenProvider).validateToken("valid.jwt.token");
		verify(userRepository).findByEmail("user@example.com");
		verify(filterChain).doFilter(request, response);
	}
	
	@Test
	public void test_missing_jwt_token() throws ServletException, IOException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);
		
		when(request.getHeader("Authorization")).thenReturn(null);
		
		filter.doFilterInternal(request, response, filterChain);
		
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}
	
	@Test
	public void test_missing_or_empty_jwt() throws ServletException, IOException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);
		
		when(request.getHeader("Authorization")).thenReturn("");
		
		filter.doFilterInternal(request, response, filterChain);
		
		verify(tokenProvider, never()).validateToken(anyString());
		verify(userRepository, never()).findByEmail(anyString());
		verify(filterChain).doFilter(request, response);
	}
	
	@Test
	public void user_authenticated_and_set_in_security_context() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);
		
		String token = "Bearer valid.jwt.token";
		when(request.getHeader("Authorization")).thenReturn(token);
		when(tokenProvider.getEmailFromJWT("valid.jwt.token")).thenReturn("user@example.com");
		
		Utilisateur user = new Utilisateur();
		user.setCredentials(Credentials.builder().email("user@example.com").build());
		when(userRepository.findByEmail("user@example.com")).thenReturn(user);
		
		assertDoesNotThrow(() -> filter.doFilterInternal(request, response, filterChain));
		
		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals("user@example.com", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
	}
}