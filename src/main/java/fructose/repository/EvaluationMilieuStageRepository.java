package fructose.repository;

import fructose.model.evaluation.EvaluationMilieuStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EvaluationMilieuStageRepository extends JpaRepository<EvaluationMilieuStage, Long> {
    @Query("SELECT e FROM EvaluationMilieuStage e WHERE e.candidature.id = :candidatureId")
    EvaluationMilieuStage findByCandidatureId(@Param("candidatureId") Long candidatureId);
}
