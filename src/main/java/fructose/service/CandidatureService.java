package fructose.service;

import fructose.model.Candidature;
import fructose.model.Cv;
import fructose.model.OffreStage;
import fructose.model.Utilisateur;
import fructose.model.enumerator.EtatCandidature;
import fructose.repository.CandidatureRepository;
import fructose.repository.CvRepository;
import fructose.repository.EvaluationMilieuStageRepository;
import fructose.repository.OffreStageRepository;
import fructose.service.dto.CandidatureDTO;
import fructose.service.dto.EtudiantDTO;
import fructose.service.dto.UtilisateurDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CandidatureService {
	
	private static final Logger logger = LoggerFactory.getLogger(CandidatureService.class);
	private final CandidatureRepository candidatureRepository;
	private final OffreStageRepository offreStageRepository;
	private final CvRepository cvRepository;
	private final EvaluationMilieuStageRepository ev;
	
	public void postuler(UtilisateurDTO etudiantDTO, Long offreStageId, Long cvDTOId) {
		OffreStage offreStage = offreStageRepository.getReferenceById(offreStageId);
		Utilisateur etudiant = UtilisateurDTO.toEntity(etudiantDTO);
		Cv cv = cvRepository.getReferenceById(cvDTOId);
		
		// Verifier si la condidature n'est pas dupliquée
		Long candidatureCount = candidatureRepository.getCandidatureNumbers(etudiant.getId(), offreStageId);
		
		if (candidatureCount == 0) {
			Candidature candidature = new Candidature();
			candidature.setEtudiant(etudiant);
			candidature.setOffreStage(offreStage);
			candidature.setCv(cv);
			candidature.setEtat(EtatCandidature.EN_ATTENTE);
			candidatureRepository.save(candidature);
		} else {
			throw new IllegalArgumentException("L'utilisateur a déjà soumis une candidature pour ce poste");
		}
	}
	
	public void refuserCandidature(Long candidatureId, String commentaireRefus) {
		try {
			Candidature candidature = candidatureRepository.findById(candidatureId).orElseThrow(() -> new IllegalArgumentException("Candidature avec ID: " + candidatureId + " n'existe pas"));
			candidature.setEtat(EtatCandidature.REFUSEE);
			candidature.setCommentaireRefus(commentaireRefus);
			candidatureRepository.save(candidature);
		} catch (Exception e) {
			logger.error("Erreur lors du refus de la candidature avec ID: {}", candidatureId, e);
			throw new RuntimeException("Une erreur est survenue lors du refus de la candidature.", e);
		}
	}
	
	public void approuverCandidature(Long candidatureId, LocalDate dateEntrevue) {
		LocalDate today = LocalDate.now();
		LocalDate minDate = today.plusDays(3);
		LocalDate maxDate = today.plusMonths(1);
		
		if (dateEntrevue.isBefore(minDate) || dateEntrevue.isAfter(maxDate)) {
			throw new IllegalArgumentException("La date d'entrevue doit être entre 3 jours à partir d'aujourd'hui et dans un mois.");
		}
		
		try {
			Candidature candidature = candidatureRepository.findById(candidatureId)
				.orElseThrow(() -> new IllegalArgumentException("Candidature avec ID: " + candidatureId + " n'existe pas"));
			candidature.setEtat(EtatCandidature.ENTREVUE_PROPOSE);
			candidature.setDateEntrevue(dateEntrevue);
			candidatureRepository.save(candidature);
		} catch (Exception e) {
			logger.error("Erreur lors de l'approbation de la candidature avec ID: {}", candidatureId, e);
			throw new RuntimeException("Une erreur est survenue lors de l'approbation de la candidature.", e);
		}
	}
	
	public List<Map<String, Object>> getOffreStageDetailsByEtudiantId(Long etudiantId) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		List<Candidature> candidatures = candidatureRepository.findByEtudiantIdWithoutCv(etudiantId);
		
		for (Candidature candidature : candidatures) {
			Map<String, Object> candidatureData = new HashMap<>();
			
			// Ajouter les informations de la candidature
			candidatureData.put("id", candidature.getId());
			candidatureData.put("etat", candidature.getEtat());
			candidatureData.put("commentaireRefus", candidature.getCommentaireRefus());
			candidatureData.put("dateEntrevue", candidature.getDateEntrevue());
			candidatureData.put("offreStageId", candidature.getOffreStage().getId());
			
			// Ajouter les informations de l'offre de stage
			OffreStage offreStage = offreStageRepository.findById(candidature.getOffreStage().getId()).orElse(null);
			if (offreStage != null) {
				candidatureData.put("nomOffre", offreStage.getNom());
				candidatureData.put("compagnie", offreStage.getCompagnie());
			}
			
			result.add(candidatureData);
		}
		
		return result;
	}
	
	public List<Map<String, Object>> findByCandidatureByOwner(Long employeurId) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		List<Candidature> candidatures = candidatureRepository.findByCandidatureByOwnerWithoutCv(employeurId);

		return getMapsFromCandidature(result, candidatures);
	}
	
	public void modifierEtatCandidature(Long candidatureId, EtatCandidature nouvelEtat) {
		try {
			Candidature candidature = candidatureRepository.findById(candidatureId)
				.orElseThrow(() -> new IllegalArgumentException("Candidature avec ID: " + candidatureId + " n'existe pas"));
			candidature.setEtat(nouvelEtat);
			candidatureRepository.save(candidature);
		} catch (Exception e) {
			logger.error("Erreur lors de la modification de l'état de la candidature avec ID: {}", candidatureId, e);
			throw new RuntimeException("Une erreur est survenue lors de la modification de l'état de la candidature.", e);
		}
	}
	
	public List<CandidatureDTO> getCandidaturesByEtatAccepteApresEntrevue() {
		try {
			List<Candidature> candidatures = candidatureRepository.findByEtatWithoutCv(EtatCandidature.ACCEPTE_APRES_ENTREVUE);
			
			List<CandidatureDTO> result = new ArrayList<>();
			
			for (Candidature candidature : candidatures) {
				result.add(CandidatureDTO.toDTO(candidature));
			}
			return result;
		} catch (Exception e) {
			logger.error("Erreur lors de la récupération des candidatures par état accepté après entrevue", e);
			throw new RuntimeException("Une erreur est survenue lors de la récupération des candidatures par état accepté après entrevue.", e);
		}
	}
	
	public CandidatureDTO getCandidatureById(Long candidatureId) {
		Candidature candidature = candidatureRepository.findById(candidatureId)
				.orElseThrow(() -> new IllegalArgumentException("Candidature avec ID: " + candidatureId + " n'existe pas"));
		try {
			return CandidatureDTO.toDTO(candidature);
		} catch (Exception e) {
			logger.error("Erreur lors de la récupération de la candidature avec ID: {}", candidatureId, e);
			throw new RuntimeException("Une erreur est survenue lors de la récupération de la candidature.", e);
		}
	}

	public List<Map<String, Object>> findStagiaireByOwner(Long id) {
		List<Map<String, Object>> result = new ArrayList<>();

		List<Candidature> candidatures = candidatureRepository.findStagiaireByOwner(id, EtatCandidature.POSTE_OBTENU);

		return getMapsFromCandidature(result, candidatures);
	}

	private List<Map<String, Object>> getMapsFromCandidature(List<Map<String, Object>> result, List<Candidature> candidatures) {
		for (Candidature candidature : candidatures) {
			Map<String, Object> candidatureData = new HashMap<>();
			candidatureData.put("candidature", CandidatureDTO.toDTO(candidature));
			Utilisateur etudiant = candidature.getEtudiant();
			candidatureData.put("etudiant", EtudiantDTO.toDTO(etudiant));
			OffreStage offreStage = candidature.getOffreStage();
			candidatureData.put("idOffreStage", offreStage.getId());
			Cv cv = cvRepository.getAllById(candidatureRepository.getCvId(candidature.getId()));
			candidatureData.put("cvId", cv.getId());
			result.add(candidatureData);
		}
		return result;
	}

	public List<Map<String, Object>> findByContratSigneTous() {
		List<Map<String, Object>> result = new ArrayList<>();


		List<Candidature> candidatures = candidatureRepository.findByEtatAndEvaluationMilieuStageNotExist(EtatCandidature.POSTE_OBTENU);
		return getMapsFromCandidature(result, candidatures);
	}
}