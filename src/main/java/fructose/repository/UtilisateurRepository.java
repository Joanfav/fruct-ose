package fructose.repository;

import fructose.model.Utilisateur;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Primary
public interface UtilisateurRepository<T extends Utilisateur, ID> extends JpaRepository<T, ID> {
	@Query("SELECT u FROM Utilisateur u WHERE u.credentials.email = ?1")
	Utilisateur findByEmail(@Param("email") String email);
	
	@Query("SELECT u FROM Utilisateur u WHERE u.matricule = ?1")
	Utilisateur findByMatricule(@Param("matricule") String matricule);
	
	@Query("SELECT u FROM Utilisateur u WHERE u.isApproved = :isApproved")
	List<Utilisateur> findByIsApproved(@Param("isApproved") boolean isApproved);
}