package fructose.model.auth;

import fructose.model.enumerator.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public final class Credentials implements UserDetails {
	
	@NotNull
	@NotEmpty
	@Size(min = 5, max = 100, message = "L'adresse courriel doit contenir entre 5 et 100 caractères")
	@Column(unique = true, updatable = false)
	@Email(message = "L'adresse courriel doit être valide")
	private String email;
	
	@NotNull
	@NotEmpty
	@Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$", message = "Le mot de passe doit contenir au moins une lettre majuscule, un chiffre et un caractère spécial")
	@Column(nullable = false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(role.name()));
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String getUsername() {
		return email;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
}