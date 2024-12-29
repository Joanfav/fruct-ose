package fructose.service.dto;

import fructose.model.Professeur;
import fructose.model.enumerator.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProfesseurDTO extends UtilisateurDTO {
	
	public static ProfesseurDTO toDTO(Professeur professeur) {
		ProfesseurDTO professeur_dto = new ProfesseurDTO();
		professeur_dto.setId(professeur.getId());
		professeur_dto.setFullName(professeur.getFullName());
		professeur_dto.setEmail(professeur.getEmail());
		professeur_dto.setPassword(professeur.getPassword());
		professeur_dto.setMatricule(professeur.getMatricule());
		professeur_dto.setRole(Role.PROFESSEUR);
		professeur_dto.setDepartementDTO(DepartementDTO.toDTO(professeur.getDepartement()));
		professeur_dto.setCompanyName(professeur.getCompanyName());
		professeur_dto.setIsApproved(professeur.getIsApproved());
		return professeur_dto;
	}
}