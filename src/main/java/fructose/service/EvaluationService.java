package fructose.service;

import fructose.model.evaluation.CritereEvaluation;
import fructose.model.evaluation.EvaluationEmployeur;
import fructose.model.evaluation.EvaluationMilieuStage;
import fructose.model.evaluation.PDF.EvaluationEmployeurPdf;
import fructose.model.evaluation.PDF.EvaluationMilieuStagePdf;
import fructose.model.evaluation.SectionEvaluation;
import fructose.repository.EvaluationEmployeurRepository;
import fructose.repository.EvaluationMilieuStageRepository;
import fructose.service.dto.EvaluationEmployeurDTO;
import fructose.service.dto.EvaluationMilieuStageDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvaluationService {

    private final EvaluationEmployeurRepository evaluationEmployeurRepository;
    private final EvaluationMilieuStageRepository evaluationMilieuStageRepository;

    public EvaluationService(EvaluationEmployeurRepository evaluationEmployeurRepository,
                             EvaluationMilieuStageRepository evaluationMilieuStageRepository) {
        this.evaluationEmployeurRepository = evaluationEmployeurRepository;
        this.evaluationMilieuStageRepository = evaluationMilieuStageRepository;
    }

    @Transactional
    public String creerEvaluationEmployeur(EvaluationEmployeurDTO evaluationDTO) {
        EvaluationEmployeur evaluation = initializeEvaluationData(evaluationDTO);
        evaluationEmployeurRepository.save(evaluation);
        return generatePdf(new EvaluationEmployeurPdf(evaluation));
    }

    @Transactional
    public String creerEvaluationMilieuStage(EvaluationMilieuStageDTO evaluationDTO) {
        EvaluationMilieuStage evaluation = initializeEvaluationData(evaluationDTO);
        evaluationMilieuStageRepository.save(evaluation);
        return generatePdf(new EvaluationMilieuStagePdf(evaluation));
    }

    private <T> String generatePdf(T pdfGenerator) {
        if (pdfGenerator == null) {
            throw new IllegalArgumentException("PDF generator cannot be null");
        }
        try {
            if (pdfGenerator instanceof EvaluationEmployeurPdf) {
                return ((EvaluationEmployeurPdf) pdfGenerator).createPdf();
            } else if (pdfGenerator instanceof EvaluationMilieuStagePdf) {
                System.out.println(pdfGenerator);
                return ((EvaluationMilieuStagePdf) pdfGenerator).createPdf();
            } else {
                throw new IllegalArgumentException("Unsupported PDF generator type: " + pdfGenerator.getClass().getName());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generating the evaluation PDF", e);
        }
    }

    private EvaluationEmployeur initializeEvaluationData(EvaluationEmployeurDTO evaluationDTO) {
        EvaluationEmployeur evaluation = EvaluationEmployeurDTO.toEntity(evaluationDTO);
        ajoutSectionCritere(evaluation);
        return evaluation;
    }

    private EvaluationMilieuStage initializeEvaluationData(EvaluationMilieuStageDTO evaluationDTO) {
        EvaluationMilieuStage evaluation = EvaluationMilieuStageDTO.toEntity(evaluationDTO);
        ajoutSectionCritere(evaluation);
        return evaluation;
    }

    private void ajoutSectionCritere(EvaluationEmployeur evaluation) {
        for (SectionEvaluation section : evaluation.getSections()) {
            section.setEvaluationEmployeur(evaluation);
            for (CritereEvaluation critere : section.getCriteres()) {
                critere.setSection(section);
            }
        }
    }

    private void ajoutSectionCritere(EvaluationMilieuStage evaluation) {
        for (SectionEvaluation section : evaluation.getSections()) {
            section.setEvaluationMilieuStage(evaluation);
            for (CritereEvaluation critere : section.getCriteres()) {
                critere.setSection(section);
            }
        }
    }

    public String recupererEvaluationEmployeurParId(Long id) {
        EvaluationEmployeur evaluation = evaluationEmployeurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evaluation not found"));
        return generatePdf(new EvaluationEmployeurPdf(evaluation));
    }

    public String recupererEvaluationMilieuStageParId(Long id) {
        EvaluationMilieuStage evaluation = evaluationMilieuStageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evaluation not found"));
        return generatePdf(new EvaluationMilieuStagePdf(evaluation));
    }

    public List<EvaluationEmployeurDTO> findAllEvaluationEmployeur() {
        return evaluationEmployeurRepository.findAll()
                .stream()
                .map(EvaluationEmployeurDTO::toDTO)
                .collect(Collectors.toList());
    }

    public List<EvaluationMilieuStageDTO> findAllEvaluationMilieuStage() {
        return evaluationMilieuStageRepository.findAll()
                .stream()
                .map(EvaluationMilieuStageDTO::toDTO)
                .collect(Collectors.toList());
    }
}
