package fructose.repository;

import fructose.model.Cv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CvRepository extends JpaRepository<Cv, Long> {
	@Query("SELECT new Cv(c.id, c.filename, c.isApproved, c.isRefused, c.utilisateur, c.commentaireRefus) FROM Cv c WHERE c.utilisateur.id = ?1")
	List<Cv> getAllByUserId(@Param("user_id") Long userId);

	@Query("SELECT new Cv(c.id, c.filename, c.isApproved, c.isRefused, c.utilisateur, c.commentaireRefus) FROM Cv c WHERE c.id = ?1")
	Cv getAllById(Long id);
}
