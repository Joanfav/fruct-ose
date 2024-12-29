package fructose.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import fructose.model.auth.Credentials;
import fructose.model.enumerator.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "RECORD_TYPE", discriminatorType = DiscriminatorType.STRING)
public class Utilisateur {
	
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	private Long id;
	
	@NotNull
	@NotEmpty
	@Size(min = 5, max = 50, message = "Le nom complet doit contenir entre 5 et 50 caractères")
	@Pattern(regexp = "^[\\p{L}\\s]+$", message = "Le nom complet doit contenir uniquement des lettres et des espaces")
	private String fullName;
	
	@Column(unique = true)
	@Pattern(regexp = "^\\d{7}$", message = "Le Matricule doit contenir 7 chiffres")
	private String matricule;
	
	@ManyToOne
	@JoinColumn(name = "departement_id")
	private Departement departement;
	
	@Size(min = 3, max = 100, message = "Le nom de l'entreprise doit contenir de 3 à 100 caractères")
	@Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "Le nom de l'entreprise doit contenir uniquement des lettres et des espaces")
	private String companyName;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = CascadeType.ALL)
	private List<OffreStage> offreStages;
	
	@Embedded
	private Credentials credentials;
	
	@OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonBackReference
	private List<Cv> cvs;
	
	private Boolean isApproved = false;
	
	public String getEmail() {
		return credentials.getEmail();
	}
	
	public String getPassword() {
		return credentials.getPassword();
	}
	
	public Role getRole() {
		return credentials.getRole();
	}
	
	public void setPassword(String password) {
		credentials.setPassword(password);
	}
	
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return credentials.getAuthorities();
	}
	
	public Utilisateur(String fullName, String email,
	                   String password, String matricule,
	                   Role role, Departement departement,
	                   String companyName, Boolean isApproved) {
		this.fullName = fullName;
		this.matricule = matricule;
		this.departement = departement;
		this.companyName = companyName;
		this.credentials = Credentials.builder().email(email).password(password).role(role).build();
		this.isApproved = isApproved;
	}
	
	public static Utilisateur createUtilisateur(String type, String fullName, String email, String password, String matricule, Departement departement, String companyName, Boolean isApproved) {
		return switch (type.toLowerCase()) {
			case "etudiant" -> new Etudiant(fullName, email, password, matricule, Role.ETUDIANT, departement, companyName, isApproved);
			case "employeur" -> new Employeur(fullName, email, password, matricule, Role.EMPLOYEUR, departement, companyName, isApproved);
			case "professeur" -> new Professeur(fullName, email, password, matricule, Role.PROFESSEUR, departement, companyName, isApproved);
			case "admin" -> new Admin(fullName, email, password, matricule, Role.ADMIN, departement, companyName, isApproved);
			default -> throw new IllegalArgumentException("Type d'utilisateur: " + type + " n'est pas supporté");
		};
	}
}