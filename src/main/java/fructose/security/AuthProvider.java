package fructose.security;

import fructose.model.Utilisateur;
import fructose.repository.UtilisateurRepository;
import fructose.security.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthProvider implements AuthenticationProvider {
	private final PasswordEncoder passwordEncoder;
	private final UtilisateurRepository userAppRepository;
	
	@Override
	public Authentication authenticate(Authentication authentication) {
		try {
			Utilisateur user = loadUserByEmail(authentication.getPrincipal().toString());
			
			if (user == null) {
				throw new AuthenticationException(HttpStatus.FORBIDDEN, "Courriel invalide");
			}
			
			if (!passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
				throw new AuthenticationException(HttpStatus.FORBIDDEN, "Mot de passe invalide");
			}
			
			return new UsernamePasswordAuthenticationToken(
					user.getEmail(),
					user.getPassword(),
					user.getAuthorities()
			);
		} catch (AuthenticationException e) {
			throw e;
		} catch (Exception e) {
			throw new AuthenticationException(HttpStatus.FORBIDDEN, "Erreur d'authentification");
		}
	}
	
	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
	
	private Utilisateur loadUserByEmail(String email) throws UsernameNotFoundException {
		return userAppRepository.findByEmail(email);
	}
	
	//TODO: Il dit unused mais je ne sais pas si je peux le supprimer donc on le laisse
	private void validateAuthentication(Authentication authentication, Utilisateur user) {
		if (!passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
			throw new AuthenticationException(HttpStatus.FORBIDDEN, "Incorrect username or password");
		}
	}
}