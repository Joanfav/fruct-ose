package fructose.repository;

import fructose.model.Contrat;
import fructose.model.Utilisateur;
import fructose.service.dto.ContratSansCvDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long> {

    @Query("SELECT new fructose.service.dto.ContratSansCvDTO(c.id, c.candidature.id, c.gestionnaire.id, c.signatureGestionnaire, c.dateSignatureGestionnaire, c.signatureEmployeur, c.dateSignatureEmployeur, c.signatureEtudiant, c.dateSignatureEtudiant) " +
            "FROM Contrat c WHERE c.candidature.id = ?1")
    ContratSansCvDTO findByCandidatureIdWithoutCv(Long id);
}