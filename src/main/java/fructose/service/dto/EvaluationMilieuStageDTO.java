package fructose.service.dto;

import fructose.model.enumerator.CapaciteEtudiant;
import fructose.model.enumerator.StageType;
import fructose.model.evaluation.EvaluationMilieuStage;
import fructose.model.evaluation.SectionEvaluation;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EvaluationMilieuStageDTO {

    private Long id;
    private CandidatureDTO candidatureDTO;
    private List<SectionEvaluationDTO> sections;
    private StageType stageType;
    private CapaciteEtudiant capaciteEtudiant;
    private int nbHeuresParSemPremierMois;
    private int nbHeuresParSemDeuxiemeMois;
    private int nbHeuresParSemTroisiemeMois;
    private int tauxHoraire;
    private StageType milieuStageAPrivilegierPour;
    private boolean memeStagiaireProchainStage;
    private boolean quartTravailVariable;
    private String signatureSuperviseur;
    private LocalDate dateSignatureSuperviseur;
    private String quartTravail1Debut;
    private String quartTravail1Fin;
    private String quartTravail2Debut;
    private String quartTravail2Fin;
    private String quartTravail3Debut;
    private String quartTravail3Fin;

    public static EvaluationMilieuStageDTO toDTO(EvaluationMilieuStage evaluationEmployeur) {
        EvaluationMilieuStageDTO dto = new EvaluationMilieuStageDTO();
        dto.setId(evaluationEmployeur.getId());
        dto.setCandidatureDTO(CandidatureDTO.toDTO(evaluationEmployeur.getCandidature()));
        dto.setStageType(evaluationEmployeur.getStageType());
        dto.setCapaciteEtudiant(evaluationEmployeur.getCapaciteEtudiant());
        dto.setNbHeuresParSemPremierMois(evaluationEmployeur.getNbHeuresParSemPremierMois());
        dto.setNbHeuresParSemDeuxiemeMois(evaluationEmployeur.getNbHeuresParSemDeuxiemeMois());
        dto.setNbHeuresParSemTroisiemeMois(evaluationEmployeur.getNbHeuresParSemTroisiemeMois());
        dto.setTauxHoraire(evaluationEmployeur.getTauxHoraire());
        dto.setMilieuStageAPrivilegierPour(evaluationEmployeur.getMilieuStageAPrivilegierPour());
        dto.setMemeStagiaireProchainStage(evaluationEmployeur.isMemeStagiaireProchainStage());
        dto.setQuartTravailVariable(evaluationEmployeur.isQuartTravailVariable());
        dto.setSignatureSuperviseur(evaluationEmployeur.getSignatureSuperviseur());
        dto.setDateSignatureSuperviseur(evaluationEmployeur.getDateSignatureSuperviseur());
        dto.setQuartTravail1Debut(evaluationEmployeur.getQuartTravail1Debut());
        dto.setQuartTravail1Fin(evaluationEmployeur.getQuartTravail1Fin());
        dto.setQuartTravail2Debut(evaluationEmployeur.getQuartTravail2Debut());
        dto.setQuartTravail2Fin(evaluationEmployeur.getQuartTravail2Fin());
        dto.setQuartTravail3Debut(evaluationEmployeur.getQuartTravail3Debut());
        dto.setQuartTravail3Fin(evaluationEmployeur.getQuartTravail3Fin());

        List<SectionEvaluationDTO> sectionDTOs = evaluationEmployeur.getSections().stream()
                .map(SectionEvaluationDTO::toDTO)
                .toList();
        dto.setSections(sectionDTOs);

        return dto;
    }

    public static EvaluationMilieuStage toEntity (EvaluationMilieuStageDTO evaluationMilieuStageDTO) {
        EvaluationMilieuStage dto = new EvaluationMilieuStage();
        dto.setId(evaluationMilieuStageDTO.getId());
        dto.setCandidature(CandidatureDTO.toEntity(evaluationMilieuStageDTO.getCandidatureDTO()));
        dto.setStageType(StageType.valueOf(evaluationMilieuStageDTO.getStageType().name()));
        dto.setCapaciteEtudiant(CapaciteEtudiant.valueOf(evaluationMilieuStageDTO.getCapaciteEtudiant().name()));
        dto.setNbHeuresParSemPremierMois(evaluationMilieuStageDTO.getNbHeuresParSemPremierMois());
        dto.setNbHeuresParSemDeuxiemeMois(evaluationMilieuStageDTO.getNbHeuresParSemDeuxiemeMois());
        dto.setNbHeuresParSemTroisiemeMois(evaluationMilieuStageDTO.getNbHeuresParSemTroisiemeMois());
        dto.setTauxHoraire(evaluationMilieuStageDTO.getTauxHoraire());
        dto.setMilieuStageAPrivilegierPour(StageType.valueOf(evaluationMilieuStageDTO.getMilieuStageAPrivilegierPour().name()));
        dto.setMemeStagiaireProchainStage(evaluationMilieuStageDTO.isMemeStagiaireProchainStage());
        dto.setQuartTravailVariable(evaluationMilieuStageDTO.isQuartTravailVariable());
        dto.setSignatureSuperviseur(evaluationMilieuStageDTO.getSignatureSuperviseur());
        dto.setDateSignatureSuperviseur(evaluationMilieuStageDTO.getDateSignatureSuperviseur());
        dto.setQuartTravail1Debut(evaluationMilieuStageDTO.getQuartTravail1Debut());
        dto.setQuartTravail1Fin(evaluationMilieuStageDTO.getQuartTravail1Fin());
        dto.setQuartTravail2Debut(evaluationMilieuStageDTO.getQuartTravail2Debut());
        dto.setQuartTravail2Fin(evaluationMilieuStageDTO.getQuartTravail2Fin());
        dto.setQuartTravail3Debut(evaluationMilieuStageDTO.getQuartTravail3Debut());
        dto.setQuartTravail3Fin(evaluationMilieuStageDTO.getQuartTravail3Fin());

        List<SectionEvaluation> sections = evaluationMilieuStageDTO.getSections().stream()
                .map(SectionEvaluationDTO::toEntity)
                .toList();
        dto.setSections(sections);

        return dto;
    }
}
