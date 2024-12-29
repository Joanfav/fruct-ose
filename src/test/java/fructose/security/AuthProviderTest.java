package fructose.security;

import fructose.model.Departement;
import fructose.model.Utilisateur;
import fructose.model.enumerator.Role;
import fructose.repository.UtilisateurRepository;
import fructose.security.exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthProviderTest {
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private UtilisateurRepository utilisateurRepository;
	
	@InjectMocks
	private AuthProvider authProvider;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void test_authenticate_valid_user() {
		PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
		UtilisateurRepository userAppRepository = mock(UtilisateurRepository.class);
		AuthProvider authProvider = new AuthProvider(passwordEncoder, userAppRepository);
		
		String email = "valid@example.com";
		String password = "validPassword";
		Utilisateur user = new Utilisateur("Valid User", email, password, "1234567", Role.ETUDIANT, new Departement(), "Company", false);
		
		when(userAppRepository.findByEmail(email)).thenReturn(user);
		when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
		
		when(userAppRepository.findByEmail(email)).thenReturn(user);
		when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
		Authentication result = authProvider.authenticate(authentication);
		
		assertNotNull(result);
		assertEquals(email, result.getPrincipal());
	}
	
	@Test
	void testAuthenticate_Failure() {
		String email = "vazgen@gmail.com";
		String password = "Vazgen123!";
		Utilisateur user = new Utilisateur("Vazgen Markaryan", email, password, "1234567", null, new Departement(), "Company", false);
		
		when(utilisateurRepository.findByEmail(email)).thenReturn(user);
		when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
		
		assertThrows(AuthenticationException.class, () -> {
			authProvider.authenticate(authentication);
		});
	}
	
	@Test
	void testSupports() {
		assertTrue(authProvider.supports(UsernamePasswordAuthenticationToken.class));
	}
}