package fructose.model;

import fructose.model.enumerator.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString(callSuper = true)
@DiscriminatorValue("RECORD_ADMIN")
public class Admin extends Utilisateur {
	public Admin() {
		super();
	}
	
	public Admin(String fullName, String email, String password, String matricule, Role role, Departement departement, String companyName, Boolean isApproved) {
		super(fullName, email, password, matricule, role, departement, companyName, isApproved);
	}
}