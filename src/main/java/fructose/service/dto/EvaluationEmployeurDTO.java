package fructose.service.dto;

import fructose.model.evaluation.EvaluationEmployeur;
import fructose.model.evaluation.SectionEvaluation;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationEmployeurDTO {

    private Long id;
    private CandidatureDTO candidatureDTO;
    private String appreciationGlobale;
    private String commentaireAppreciationGlobale;
    private String discuterAvecEtudiant;
    private float nombreHeureEncadrement;
    private String acceuilleEleveProchainStage;
    private String commentairesAccomplissementMandat;
    private String signature;
    private List<SectionEvaluationDTO> sections;

    public static EvaluationEmployeurDTO toDTO(EvaluationEmployeur evaluationEmployeur) {
        EvaluationEmployeurDTO dto = new EvaluationEmployeurDTO();
        dto.setId(evaluationEmployeur.getId());
        dto.setAppreciationGlobale(evaluationEmployeur.getAppreciationGlobale());
        dto.setCommentaireAppreciationGlobale(evaluationEmployeur.getCommentaireAppreciationGlobale());
        dto.setDiscuterAvecEtudiant(evaluationEmployeur.getDiscuterAvecEtudiant());
        dto.setNombreHeureEncadrement(evaluationEmployeur.getNombreHeureEncadrement());
        dto.setAcceuilleEleveProchainStage(evaluationEmployeur.getAcceuilleEleveProchainStage());
        dto.setCommentairesAccomplissementMandat(evaluationEmployeur.getCommentairesAccomplissementMandat());
        dto.setSignature(evaluationEmployeur.getSignature());

        dto.setCandidatureDTO(CandidatureDTO.toDTO(evaluationEmployeur.getCandidature()));

        List<SectionEvaluationDTO> sectionDTOs = evaluationEmployeur.getSections().stream()
                .map(SectionEvaluationDTO::toDTO)
                .toList();
        dto.setSections(sectionDTOs);

        return dto;
    }

    public static EvaluationEmployeur toEntity(EvaluationEmployeurDTO dto) {
        EvaluationEmployeur evaluationEmployeur = new EvaluationEmployeur();
        evaluationEmployeur.setId(dto.getId());
        evaluationEmployeur.setAppreciationGlobale(dto.getAppreciationGlobale());
        evaluationEmployeur.setCommentaireAppreciationGlobale(dto.getCommentaireAppreciationGlobale());
        evaluationEmployeur.setDiscuterAvecEtudiant(dto.getDiscuterAvecEtudiant());
        evaluationEmployeur.setNombreHeureEncadrement(dto.getNombreHeureEncadrement());
        evaluationEmployeur.setAcceuilleEleveProchainStage(dto.getAcceuilleEleveProchainStage());
        evaluationEmployeur.setCommentairesAccomplissementMandat(dto.getCommentairesAccomplissementMandat());
        evaluationEmployeur.setSignature(dto.getSignature());

        evaluationEmployeur.setCandidature(CandidatureDTO.toEntity(dto.getCandidatureDTO()));

        List<SectionEvaluation> sections = dto.getSections().stream()
                .map(SectionEvaluationDTO::toEntity)
                .toList();
        evaluationEmployeur.setSections(sections);

        return evaluationEmployeur;
    }
}