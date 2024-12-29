package fructose.controller;

import fructose.model.enumerator.Role;
import fructose.service.OffreStageService;
import fructose.service.UtilisateurService;
import fructose.service.dto.OffreStageDTO;
import fructose.service.dto.UtilisateurDTO;
import jakarta.validation.Valid;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class OffreStageController {

	private static final Logger logger = LoggerFactory.getLogger(OffreStageController.class);
	private final OffreStageService offreStageService;
	private final UtilisateurService utilisateurService;

	public OffreStageController(OffreStageService offreStageService, UtilisateurService utilisateurService) {
		this.offreStageService = offreStageService;
		this.utilisateurService = utilisateurService;
	}

	@PostMapping ("/creer-offre-stage")
	public ResponseEntity<?> creerOffreStage(@RequestHeader ("Authorization") String token, @RequestBody @Valid OffreStageDTO offreStageDTO, BindingResult result) {
		UtilisateurDTO currentUser = utilisateurService.getUtilisateurByToken(token);

		if(!(currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.EMPLOYEUR)){
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");
		}

		if (offreStageDTO.getOwnerDTO() == null) {
			UtilisateurDTO utilisateurDTO = utilisateurService.getUtilisateurByToken(token);
			offreStageDTO.setOwnerDTO(utilisateurDTO);
		} else {
			try {
				UtilisateurDTO utilisateurDTO = utilisateurService.getUtilisateurById(offreStageDTO.getOwnerDTO().getId(), Role.EMPLOYEUR);
				if (utilisateurDTO != null && currentUser.getRole() == Role.ADMIN){
					offreStageDTO.setOwnerDTO(utilisateurDTO);
					offreStageDTO.setIsApproved(true);
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employeur non trouvé");
				}
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employeur non trouvé");
			}
		}
		if (result.hasErrors()) {
			String errorMessages = result.getFieldErrors().stream()
					.map(DefaultMessageSourceResolvable::getDefaultMessage)
					.collect(Collectors.joining(", "));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur de validation : " + errorMessages);
		}
		try {
			offreStageService.addOffreStage(offreStageDTO);
			return ResponseEntity.status(HttpStatus.CREATED).body("Offre de stage créée avec succès !");
		} catch (DataAccessException e) {
			if (e.getCause() instanceof ConstraintViolationException violation) {
				String detailMessage = violation.getSQLException().getMessage();
				String uniqueValue = detailMessage.substring(detailMessage.indexOf('(') + 1, detailMessage.indexOf(')'));
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Violation de contrainte unique : La valeur \"" + uniqueValue + "\" existe déjà.");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création de l'offre de stage.");
			}
		} catch (Exception e) {
			logger.error("Une erreur inattendue s'est produite", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur inattendue s'est produite.");
		}
	}

	@PutMapping ("/modifier-offre-stage")
	public ResponseEntity<?> modifierOffreStage(@RequestHeader ("Authorization") String token, @RequestBody @Valid OffreStageDTO offreStageDTO, BindingResult result) {

		if (result.hasErrors()) {
			// Collect validation error messages into a list and return as JSON
			List<String> errorMessages = result.getFieldErrors().stream()
					.map(DefaultMessageSourceResolvable::getDefaultMessage)
					.collect(Collectors.toList());
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
			errorResponse.put("errors", errorMessages);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
		try {
			// Attempt to update the OffreStage
			offreStageService.updateOffreStage(offreStageDTO);
			Map<String, String> successResponse = new HashMap<>();
			successResponse.put("message", "Offre de stage modifiée avec succès !");
			return ResponseEntity.status(HttpStatus.OK).body(successResponse);

		} catch (DataAccessException e) {
			// Handle unique constraint violations
			if (e.getCause() instanceof ConstraintViolationException violation) {
				String detailMessage = violation.getSQLException().getMessage();
				String uniqueValue = detailMessage.substring(detailMessage.indexOf('(') + 1, detailMessage.indexOf(')'));
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("message", "Violation de contrainte unique : La valeur \"" + uniqueValue + "\" existe déjà.");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
			} else {
				// General DataAccessException handling
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("message", "Erreur lors de la modification de l'offre de stage.");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
			}
		} catch (Exception e) {
			// Handle any other unexpected exceptions
			logger.error("Une erreur inattendue s'est produite", e);
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", "Une erreur inattendue s'est produite.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@GetMapping ("/get-offre-stage")
	public ResponseEntity<?> getOffreStage() {
		try {
			return ResponseEntity.ok(offreStageService.getOffresStage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucune offre de stage n'a été trouvée.");
		} catch (Exception e) {
			logger.error("Une erreur inattendue s'est produite", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur inattendue s'est produite.");
		}
	}

	@GetMapping ("/get-offre-stage/{id}")
	public ResponseEntity<?> getOffreStageById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(offreStageService.getOffreStageById(id));
		} catch (Exception e) {
			logger.error("Une erreur inattendue s'est produite", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur inattendue s'est produite.");
		}
	}

	@DeleteMapping ("/delete-offre-stage/{id}")
	public ResponseEntity<?> deleteOffreStage(@PathVariable Long id) {
		try {
			offreStageService.deleteOffreStage(id);
			return ResponseEntity.ok("Offre de stage supprimée avec succès !");
		} catch (Exception e) {
			logger.error("Une erreur inattendue s'est produite", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur inattendue s'est produite.");
		}
	}

	@PostMapping ("/refuser-offre-stage/{id}")
	public ResponseEntity<String> refuserOffreStage(@PathVariable Long id, @RequestBody String commentaireRefus) {
		try {
			offreStageService.refuserOffreStage(id, commentaireRefus);
			return new ResponseEntity<>(commentaireRefus, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Erreur lors du refus de l'offre de stage.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/accepter-offre-stage/{id}")
	public ResponseEntity<?> accepterOffreStage(@PathVariable Long id){
		try {
			offreStageService.accepterOffreStage(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Erreur lors du refus de l'offre de stage.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}