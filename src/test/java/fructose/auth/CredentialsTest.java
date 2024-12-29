package fructose.auth;

import fructose.model.auth.Credentials;
import fructose.model.enumerator.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsTest {
	
	@Test
	void testGetAuthorities() {
		Credentials credentials = Credentials.builder()
			.email("test@example.com")
			.password("Password1!")
			.role(Role.ETUDIANT)
			.build();
		
		Collection<? extends GrantedAuthority> authorities = credentials.getAuthorities();
		assertNotNull(authorities);
		assertEquals(1, authorities.size());
		assertEquals("ETUDIANT", authorities.iterator().next().getAuthority());
	}
	
	@Test
	void testGetPassword() {
		Credentials credentials = Credentials.builder()
			.email("test@example.com")
			.password("Password1!")
			.role(Role.ETUDIANT)
			.build();
		
		assertEquals("Password1!", credentials.getPassword());
	}
	
	@Test
	void testGetUsername() {
		Credentials credentials = Credentials.builder()
			.email("test@example.com")
			.password("Password1!")
			.role(Role.ETUDIANT)
			.build();
		
		assertEquals("test@example.com", credentials.getUsername());
	}
	
	@Test
	void testIsAccountNonExpired() {
		Credentials credentials = Credentials.builder()
			.email("test@example.com")
			.password("Password1!")
			.role(Role.ETUDIANT)
			.build();
		
		assertTrue(credentials.isAccountNonExpired());
	}
	
	@Test
	void testIsAccountNonLocked() {
		Credentials credentials = Credentials.builder()
			.email("test@example.com")
			.password("Password1!")
			.role(Role.ETUDIANT)
			.build();
		
		assertTrue(credentials.isAccountNonLocked());
	}
	
	@Test
	void testIsCredentialsNonExpired() {
		Credentials credentials = Credentials.builder()
			.email("test@example.com")
			.password("Password1!")
			.role(Role.ETUDIANT)
			.build();
		
		assertTrue(credentials.isCredentialsNonExpired());
	}
	
	@Test
	void testIsEnabled() {
		Credentials credentials = Credentials.builder()
			.email("test@example.com")
			.password("Password1!")
			.role(Role.ETUDIANT)
			.build();
		
		assertTrue(credentials.isEnabled());
	}
	
	@Test
	void testToString() {
		Credentials credentials = Credentials.builder()
			.email("test@example.com")
			.password("Password1!")
			.role(Role.ETUDIANT)
			.build();
		
		String expected = "Credentials(email=test@example.com, password=Password1!, role=ETUDIANT)";
		assertEquals(expected, credentials.toString());
	}
}