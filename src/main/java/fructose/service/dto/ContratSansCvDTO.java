package fructose.service.dto;

import fructose.model.Contrat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContratSansCvDTO {
    private Long id;
    private Long candidatureId;
    private Long gestionnaireId;
    private String signatureGestionnaire;
    private LocalDate dateSignatureGestionnaire;
    private String signatureEmployeur = "Non signe";
    private LocalDate dateSignatureEmployeur;
    private String signatureEtudiant = "Non signe";
    private LocalDate dateSignatureEtudiant;

    public static Contrat toEntity(ContratSansCvDTO contratDTO) {
        Contrat contrat = new Contrat();
        contrat.setId(contratDTO.getId());
        contrat.setSignatureGestionnaire(contratDTO.getSignatureGestionnaire());
        contrat.setDateSignatureGestionnaire(contratDTO.getDateSignatureGestionnaire());
        contrat.setSignatureEmployeur(contratDTO.getSignatureEmployeur());
        contrat.setDateSignatureEmployeur(contratDTO.getDateSignatureEmployeur());
        contrat.setSignatureEtudiant(contratDTO.getSignatureEtudiant());
        contrat.setDateSignatureEtudiant(contratDTO.getDateSignatureEtudiant());
        return contrat;
    }
}