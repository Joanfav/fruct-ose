package fructose.service.dto;

import fructose.model.Employeur;
import fructose.model.enumerator.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class EmployeurDTO extends UtilisateurDTO {
	
	public static EmployeurDTO toDTO(Employeur employeur) {
		EmployeurDTO employeur_dto = new EmployeurDTO();
		employeur_dto.setId(employeur.getId());
		employeur_dto.setFullName(employeur.getFullName());
		employeur_dto.setEmail(employeur.getEmail());
		employeur_dto.setPassword(employeur.getPassword());
		employeur_dto.setMatricule(employeur.getMatricule());
		employeur_dto.setRole(Role.EMPLOYEUR);
		employeur_dto.setCompanyName(employeur.getCompanyName());
		employeur_dto.setIsApproved(employeur.getIsApproved());
		return employeur_dto;
	}

	public static Employeur toEntity(EmployeurDTO employeur_dto) {
		Employeur employeur = new Employeur();
		employeur.setId(employeur_dto.getId());
		employeur.setFullName(employeur_dto.getFullName());
		employeur.getCredentials().setEmail(employeur_dto.getEmail());
		employeur.setPassword(employeur_dto.getPassword());
		employeur.setMatricule(employeur_dto.getMatricule());
		employeur.getCredentials().setRole(Role.EMPLOYEUR);
		employeur.setCompanyName(employeur_dto.getCompanyName());
		employeur.setIsApproved(employeur_dto.getIsApproved());
		return employeur;
	}
}