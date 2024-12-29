package fructose.model;

import fructose.model.enumerator.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Entity
@Setter
@Getter
@ToString
@DiscriminatorValue ("RECORD_ETUDIANT")
public class Etudiant extends Utilisateur {
	
	@OneToMany (mappedBy = "etudiant")
	private Set<Candidature> candidatures;
	
	public Etudiant() {
		super();
	}
	
	public Etudiant(String fullName, String email, String password, String matricule, Role role, Departement departement, String companyName, Boolean isApproved) {
		super(fullName, email, password, matricule, role, departement, companyName, isApproved);
	}
}