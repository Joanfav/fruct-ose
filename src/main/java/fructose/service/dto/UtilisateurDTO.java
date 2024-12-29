package fructose.service.dto;

import fructose.model.*;
import fructose.model.auth.Credentials;
import fructose.model.enumerator.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UtilisateurDTO {
	private Long id;
	private String fullName;
	private String email;
	private String password;
	private String matricule;
	private Role role;
	private DepartementDTO departementDTO;
	private String companyName;
	private Boolean isApproved = false;
	
	public static Utilisateur toEntity(UtilisateurDTO utilisateur_dto) {
		Utilisateur utilisateur = switch (utilisateur_dto.getRole()) {
			case ETUDIANT -> new Etudiant();
			case PROFESSEUR -> new Professeur();
			case EMPLOYEUR -> new Employeur();
			case ADMIN -> new Admin();
		};
		
		utilisateur.setId(utilisateur_dto.getId());
		utilisateur.setFullName(utilisateur_dto.getFullName());
		utilisateur.setMatricule(utilisateur_dto.getMatricule());
		utilisateur.setDepartement(DepartementDTO.toEntity(utilisateur_dto.getDepartementDTO()));
		utilisateur.setCompanyName(utilisateur_dto.getCompanyName());
		utilisateur.setCredentials(Credentials.builder().email(utilisateur_dto.getEmail()).password(utilisateur_dto.getPassword()).role(utilisateur_dto.getRole()).build());
		utilisateur.setIsApproved(utilisateur_dto.getIsApproved());
		return utilisateur;
	}
	
	public static UtilisateurDTO toDTO(Utilisateur utilisateur) {
		UtilisateurDTO utilisateur_dto = switch (utilisateur.getRole()) {
			case ETUDIANT -> new EtudiantDTO();
			case PROFESSEUR -> new ProfesseurDTO();
			case EMPLOYEUR -> new EmployeurDTO();
			case ADMIN -> new AdminDTO();
		};
		
		utilisateur_dto.setId(utilisateur.getId());
		utilisateur_dto.setFullName(utilisateur.getFullName());
		utilisateur_dto.setEmail(utilisateur.getEmail());
		utilisateur_dto.setPassword(utilisateur.getPassword());
		utilisateur_dto.setMatricule(utilisateur.getMatricule());
		utilisateur_dto.setRole(utilisateur.getRole());
		if (utilisateur.getDepartement() != null)
			utilisateur_dto.setDepartementDTO(DepartementDTO.toDTO(utilisateur.getDepartement()));
		utilisateur_dto.setCompanyName(utilisateur.getCompanyName());
		utilisateur_dto.setIsApproved(utilisateur.getIsApproved());
		return utilisateur_dto;
	}
}