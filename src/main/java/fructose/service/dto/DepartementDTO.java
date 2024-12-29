package fructose.service.dto;

import fructose.model.Departement;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DepartementDTO {
	
	private Long id;
	
	@NotEmpty(message = "Le nom ne peut pas être vide")
	@Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
	private String nom;
	
	
	public static DepartementDTO toDTO(Departement departement) {
		DepartementDTO departementDTO = new DepartementDTO();
		departementDTO.setId(departement.getId());
		departementDTO.setNom(departement.getNom());
		return departementDTO;
	}
	
	public static Departement toEntity(DepartementDTO departementDTO) {
		Departement departement = new Departement();
		if (departementDTO == null) {
			return null;
		}
		departement.setId(departementDTO.getId());
		departement.setNom(departementDTO.getNom());
		return departement;
	}
}