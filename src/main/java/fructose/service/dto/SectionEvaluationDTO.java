package fructose.service.dto;

import fructose.model.evaluation.CritereEvaluation;
import fructose.model.evaluation.SectionEvaluation;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SectionEvaluationDTO {

    private Long id;
    private String name;
    private String commentaire;
    private List<CritereEvaluationDTO> criteres;

    public static SectionEvaluationDTO toDTO(SectionEvaluation sectionEvaluation) {
        SectionEvaluationDTO dto = new SectionEvaluationDTO();
        dto.setId(sectionEvaluation.getId());
        dto.setName(sectionEvaluation.getName());
        dto.setCommentaire(sectionEvaluation.getCommentaire());

        List<CritereEvaluationDTO> criteresDTO = sectionEvaluation.getCriteres().stream()
                .map(CritereEvaluationDTO::toDTO)
                .toList();
        dto.setCriteres(criteresDTO);

        return dto;
    }

    public static SectionEvaluation toEntity(SectionEvaluationDTO dto) {
        SectionEvaluation sectionEvaluation = new SectionEvaluation();
        sectionEvaluation.setId(dto.getId());
        sectionEvaluation.setName(dto.getName());
        sectionEvaluation.setCommentaire(dto.getCommentaire());

        List<CritereEvaluation> criteres = dto.getCriteres().stream()
                .map(CritereEvaluationDTO::toEntity)
                .toList();
        sectionEvaluation.setCriteres(criteres);

        return sectionEvaluation;
    }
}