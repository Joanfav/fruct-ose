package fructose.repository;

import fructose.model.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartementRepository extends JpaRepository<Departement, Long> {
	@Query("SELECT d FROM Departement d WHERE d.nom = ?1")
	List<Departement> findByNom(String name);
}