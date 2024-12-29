package fructose.model.enumerator;

import java.util.HashSet;
import java.util.Set;

public enum Role {
	ETUDIANT("ETUDIANT"),
	EMPLOYEUR("EMPLOYEUR"),
	PROFESSEUR("PROFESSEUR"),
	ADMIN("ADMIN");
	
	private final String string;
	private final Set<Role> managedRoles = new HashSet<>();
	
	static {
		ADMIN.managedRoles.add(ETUDIANT);
		ADMIN.managedRoles.add(EMPLOYEUR);
		ADMIN.managedRoles.add(PROFESSEUR);
	}
	
	Role(String string) {
		this.string = string;
	}
	
	@Override
	public String toString() {
		return string;
	}
}