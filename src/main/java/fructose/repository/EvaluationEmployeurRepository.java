package fructose.repository;

import fructose.model.evaluation.EvaluationEmployeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EvaluationEmployeurRepository extends JpaRepository<EvaluationEmployeur, Long> {
    @Query("SELECT e.candidature.id FROM EvaluationEmployeur e WHERE e.id = :id")
    Long findCandidatureId(@Param("id") Long id);

    @Query("SELECT e FROM EvaluationEmployeur e LEFT JOIN FETCH e.sections WHERE e.id = :id AND (e.candidature IS NULL OR e.candidature.id IS NULL)")
    EvaluationEmployeur getEvaluationByIdWithoutCandidature(@Param("id") Long id);


}
