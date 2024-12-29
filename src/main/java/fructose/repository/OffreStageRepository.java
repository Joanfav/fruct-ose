package fructose.repository;

import fructose.model.OffreStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffreStageRepository extends JpaRepository<OffreStage, Long> {
	@Query("SELECT o FROM OffreStage o WHERE o.owner.id = ?1")
	List<OffreStage> getAllByOwnerId(Long ownerId);
	
	@Query("SELECT o FROM OffreStage o WHERE o.departement.id = ?1")
	List<OffreStage> findByUserDepartement(Long departementId);

	@Query("SELECT o FROM OffreStage o WHERE o.departement.id = ?1 AND o.isApproved = true")
	List<OffreStage> findByUserDepartementAndIsApproved(Long departementId);
}