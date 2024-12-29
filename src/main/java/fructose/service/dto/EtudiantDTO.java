package fructose.service.dto;

import fructose.model.Etudiant;
import fructose.model.enumerator.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EtudiantDTO extends UtilisateurDTO {
	
	public static EtudiantDTO toDTO(Etudiant etudiant) {
		EtudiantDTO etudiant_dto = new EtudiantDTO();
		etudiant_dto.setId(etudiant.getId());
		etudiant_dto.setFullName(etudiant.getFullName());
		etudiant_dto.setEmail(etudiant.getEmail());
		etudiant_dto.setPassword(etudiant.getPassword());
		etudiant_dto.setMatricule(etudiant.getMatricule());
		etudiant_dto.setRole(Role.ETUDIANT);
		etudiant_dto.setDepartementDTO(DepartementDTO.toDTO(etudiant.getDepartement()));
		etudiant_dto.setIsApproved(etudiant.getIsApproved());
		return etudiant_dto;
	}
	
	public static Etudiant toEntity(EtudiantDTO etudiant_dto) {
		Etudiant etudiant = new Etudiant();
		etudiant.setId(etudiant_dto.getId());
		etudiant.setFullName(etudiant_dto.getFullName());
		etudiant.getCredentials().setEmail(etudiant_dto.getEmail());
		etudiant.setPassword(etudiant_dto.getPassword());
		etudiant.setMatricule(etudiant_dto.getMatricule());
		etudiant.getCredentials().setRole(Role.ETUDIANT);
		etudiant.setDepartement(DepartementDTO.toEntity(etudiant_dto.getDepartementDTO()));
		etudiant.setIsApproved(etudiant_dto.getIsApproved());
		return etudiant;
	}
}