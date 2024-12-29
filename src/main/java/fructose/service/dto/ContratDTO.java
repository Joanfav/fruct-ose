package fructose.service.dto;

import fructose.model.Contrat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ContratDTO {
	private Long id;
	
	@NotNull
	private CandidatureDTO candidatureDTO;
	
	@NotBlank
	private UtilisateurDTO gestionnaire;
	
	private String signatureGestionnaire;
	
	private LocalDate dateSignatureGestionnaire;
	
	private String signatureEmployeur = "Non signe";
	
	private LocalDate dateSignatureEmployeur;
	
	private String signatureEtudiant = "Non signe";
	
	private LocalDate dateSignatureEtudiant;
	
	public static ContratDTO toDTO(Contrat contrat) {
		ContratDTO dto = new ContratDTO();
		dto.setId(contrat.getId());
		dto.setCandidatureDTO(CandidatureDTO.toDTO(contrat.getCandidature()));
		dto.setSignatureGestionnaire(contrat.getSignatureGestionnaire());
		dto.setDateSignatureGestionnaire(contrat.getDateSignatureGestionnaire());
		dto.setSignatureEmployeur(contrat.getSignatureEmployeur());
		dto.setDateSignatureEmployeur(contrat.getDateSignatureEmployeur());
		dto.setSignatureEtudiant(contrat.getSignatureEtudiant());
		dto.setDateSignatureEtudiant(contrat.getDateSignatureEtudiant());
		dto.setGestionnaire(UtilisateurDTO.toDTO(contrat.getGestionnaire()));
		return dto;
	}
	
	public static Contrat toEntity(ContratDTO dto) {
		Contrat contrat = new Contrat();
		contrat.setId(dto.getId());
		contrat.setCandidature(CandidatureDTO.toEntity(dto.getCandidatureDTO()));
		contrat.setSignatureGestionnaire(dto.getSignatureGestionnaire());
		contrat.setDateSignatureGestionnaire(dto.getDateSignatureGestionnaire());
		contrat.setSignatureEmployeur(dto.getSignatureEmployeur());
		contrat.setDateSignatureEmployeur(dto.getDateSignatureEmployeur());
		contrat.setSignatureEtudiant(dto.getSignatureEtudiant());
		contrat.setDateSignatureEtudiant(dto.getDateSignatureEtudiant());
		contrat.setGestionnaire(UtilisateurDTO.toEntity(dto.getGestionnaire()));
		return contrat;
	}

	public static List<ContratDTO> toDTOList(List<Contrat> all) {
		return all.stream().map(ContratDTO::toDTO).collect(Collectors.toList());
	}
}