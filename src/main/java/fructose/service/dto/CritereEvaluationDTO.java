package fructose.service.dto;

import fructose.model.enumerator.ReponseEvaluation;
import fructose.model.evaluation.CritereEvaluation;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CritereEvaluationDTO {

    private Long id;
    private String question;
    private String reponse;

    public static CritereEvaluationDTO toDTO(CritereEvaluation critereEvaluation) {
        CritereEvaluationDTO dto = new CritereEvaluationDTO();
        dto.setId(critereEvaluation.getId());
        dto.setQuestion(critereEvaluation.getQuestion());
        dto.setReponse(String.valueOf(critereEvaluation.getReponse()));
        return dto;
    }

    public static CritereEvaluation toEntity(CritereEvaluationDTO dto) {
        CritereEvaluation critereEvaluation = new CritereEvaluation();
        critereEvaluation.setId(dto.getId());
        critereEvaluation.setQuestion(dto.getQuestion());
        critereEvaluation.setReponse(ReponseEvaluation.valueOf(dto.getReponse()));
        return critereEvaluation;
    }
}
