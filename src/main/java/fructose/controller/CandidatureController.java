package fructose.controller;

import fructose.model.enumerator.EtatCandidature;
import fructose.service.CandidatureService;
import fructose.service.UtilisateurService;
import fructose.service.dto.ApplicationStageDTO;
import fructose.service.dto.CandidatureDTO;
import fructose.service.dto.UtilisateurDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ("/candidatures")
@RequiredArgsConstructor
public class CandidatureController {
	
	private static final Logger logger = LoggerFactory.getLogger(CandidatureController.class);
	private final CandidatureService candidatureService;
	private final UtilisateurService utilisateurService;
	
	@PostMapping ("/postuler")
	@ResponseStatus (HttpStatus.CREATED)
	public ResponseEntity<?> postuler(@RequestHeader ("Authorization") String token, @RequestBody ApplicationStageDTO applicationStageDTO) {
		UtilisateurDTO utilisateurDTO = utilisateurService.getUtilisateurByToken(token);
		try {
			candidatureService.postuler(utilisateurDTO, applicationStageDTO.getOffreStageId(), applicationStageDTO.getCvId());
			return new ResponseEntity<>("Success", HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (RuntimeException e) {
			logger.error("Erreur lors de la soumission de la candidature", e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping ("/approuver/{candidatureId}")
	@ResponseStatus (HttpStatus.OK)
	public void approuverCandidature(@PathVariable Long candidatureId, @RequestBody Map<String, String> request) {
		try {
			LocalDate dateEntrevue = LocalDate.parse(request.get("dateEntrevue"));
			candidatureService.approuverCandidature(candidatureId, dateEntrevue);
		} catch (RuntimeException e) {
			logger.error("Erreur lors de l'approbation de la candidature avec ID: {}", candidatureId, e);
			throw new RuntimeException("Une erreur est survenue lors de l'approbation de la candidature.");
		}
	}
	
	@PutMapping ("/refuser/{candidatureId}")
	@ResponseStatus (HttpStatus.OK)
	public void refuserCandidature(@PathVariable Long candidatureId, @RequestParam String commentaireRefus) {
		try {
			candidatureService.refuserCandidature(candidatureId, commentaireRefus);
		} catch (RuntimeException e) {
			logger.error("Erreur lors du refus de la candidature avec ID: {}", candidatureId, e);
			throw new RuntimeException("Une erreur est survenue lors du refus de la candidature.");
		}
	}
	
	@GetMapping ("/etudiant/{etudiantId}")
	public List<Map<String, Object>> getOffreStageDetailsByEtudiantId(@PathVariable Long etudiantId) {
		return candidatureService.getOffreStageDetailsByEtudiantId(etudiantId);
	}
	
	@GetMapping ("/candidaturesEmployeur")
	public ResponseEntity<?> findByCandidatureByOwner(@RequestHeader ("Authorization") String token) {
		if (!utilisateurService.validationToken(token)) {
			return new ResponseEntity<>("Invalid token", HttpStatus.BAD_REQUEST);
		}
		UtilisateurDTO utilisateurDTO = utilisateurService.getUtilisateurByToken(token);
		
		try {
			List<Map<String, Object>> candidatures = candidatureService.findByCandidatureByOwner(utilisateurDTO.getId());
			return new ResponseEntity<>(candidatures, HttpStatus.OK);
		} catch (RuntimeException e) {
			logger.error("Erreur lors de la récupération des candidatures pour l'offre de stage ID: {}", utilisateurDTO.getId(), e);
			return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/modifierEtatCandidature/{candidatureId}")
	@ResponseStatus(HttpStatus.OK)
	public void modifierEtatCandidature(@PathVariable Long candidatureId, @RequestParam EtatCandidature nouvelEtat) {
		try {
			candidatureService.modifierEtatCandidature(candidatureId, nouvelEtat);
		} catch (RuntimeException e) {
			logger.error("Erreur lors de la modification de l'état de la candidature avec ID: {}", candidatureId, e);
			throw new RuntimeException("Une erreur est survenue lors de la modification de l'état de la candidature.");
		}
	}
	
	@GetMapping("/accepteApresEntrevue")
	public ResponseEntity<?> getCandidaturesAccepteApresEntrevue(@RequestHeader("Authorization") String token) {
		if (!utilisateurService.validationToken(token)) {
			return new ResponseEntity<>("Invalid token", HttpStatus.FORBIDDEN);
		}
		try {
			List<CandidatureDTO>  candidatures = candidatureService.getCandidaturesByEtatAccepteApresEntrevue();
			return new ResponseEntity<>(candidatures, HttpStatus.OK);
		} catch (RuntimeException e) {
			logger.error("Erreur lors de la récupération des candidatures acceptées après l'entrevue", e);
			return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@GetMapping ("/stagiaireByOwner")
	public ResponseEntity<?> findByStagiaireByOwner(@RequestHeader ("Authorization") String token) {
		if (!utilisateurService.validationToken(token)) {
			return new ResponseEntity<>("Invalid token", HttpStatus.BAD_REQUEST);
		}
		UtilisateurDTO utilisateurDTO = utilisateurService.getUtilisateurByToken(token);

		try {
			List<Map<String, Object>> candidatures = candidatureService.findStagiaireByOwner(utilisateurDTO.getId());
			return new ResponseEntity<>(candidatures, HttpStatus.OK);
		} catch (RuntimeException e) {
			logger.error("Erreur lors de la récupération des candidatures pour l'offre de stage ID: {}", utilisateurDTO.getId(), e);
			return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@GetMapping ("/contratSigneTous")
	public ResponseEntity<?> findByContratSigneTous(@RequestHeader ("Authorization") String token) {
		if (!utilisateurService.validationToken(token)) {
			return new ResponseEntity<>("Invalid token", HttpStatus.BAD_REQUEST);
		}
		UtilisateurDTO utilisateurDTO = utilisateurService.getUtilisateurByToken(token);

		try {
			List<Map<String, Object>> candidatures = candidatureService.findByContratSigneTous();
			return new ResponseEntity<>(candidatures, HttpStatus.OK);
		} catch (RuntimeException e) {
			logger.error("Erreur lors de la récupération des candidatures pour l'offre de stage ID: {}", utilisateurDTO.getId(), e);
			return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}