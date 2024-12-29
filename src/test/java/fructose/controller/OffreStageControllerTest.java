package fructose.controller;

import fructose.model.enumerator.Role;
import fructose.service.OffreStageService;
import fructose.service.UtilisateurService;
import fructose.service.dto.OffreStageDTO;
import fructose.service.dto.UtilisateurDTO;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OffreStageControllerTest {
	
	@InjectMocks
	private OffreStageController offreStageController;
	
	@Mock
	private OffreStageService offreStageService;
	
	//Ça dit 0 utilisations mais c'est faux. Si tu le delete tout plante!
	@Mock
	private UtilisateurService utilisateurService;
	
	@Mock
	private OffreStageDTO offreStageDTO;
	
	@Mock
	private BindingResult bindingResult;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void testCreerOffreStageSucces() {
		when(bindingResult.hasErrors()).thenReturn(false);
		doNothing().when(offreStageService).addOffreStage(any(OffreStageDTO.class));
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setRole(Role.ADMIN);
		when(utilisateurService.getUtilisateurByToken("token")).thenReturn(utilisateurDTO);
		ResponseEntity<?> response = offreStageController.creerOffreStage("token", offreStageDTO, bindingResult);
		
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Offre de stage créée avec succès !", response.getBody());
		
		verify(offreStageService, times(1)).addOffreStage(offreStageDTO);
	}
	
	@Test
	void testCreerOffreStageInvalide() {
		when(bindingResult.hasErrors()).thenReturn(true);
		when(bindingResult.getFieldErrors()).thenReturn(
			List.of(new FieldError("offreStageDTO", "nom", "Le nom n'est pas valide"))
		);

		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setRole(Role.ADMIN);
		when(utilisateurService.getUtilisateurByToken("token")).thenReturn(utilisateurDTO);
		
		ResponseEntity<?> response = offreStageController.creerOffreStage("token", offreStageDTO, bindingResult);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Erreur de validation : Le nom n'est pas valide", response.getBody());
		
		verify(bindingResult, times(1)).hasErrors();
		verify(bindingResult, times(1)).getFieldErrors();
		verifyNoInteractions(offreStageService);
	}
	
	@Test
	void testCreerOffreStageDataAccessException() {
		when(bindingResult.hasErrors()).thenReturn(false);
		doThrow(new DataAccessException("Database error") {
		}).when(offreStageService).addOffreStage(any(OffreStageDTO.class));

		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setRole(Role.ADMIN);
		when(utilisateurService.getUtilisateurByToken("token")).thenReturn(utilisateurDTO);
		
		ResponseEntity<?> response = offreStageController.creerOffreStage("token", offreStageDTO, bindingResult);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Erreur lors de la création de l'offre de stage.", response.getBody());
		
		verify(offreStageService, times(1)).addOffreStage(offreStageDTO);
	}
	
	@Test
	void testCreerOffreStageUniqueConstraintViolationException() {
		when(bindingResult.hasErrors()).thenReturn(false);
		SQLException sqlException = new SQLException("Unique constraint violation (1L)");
		ConstraintViolationException constraintViolationException = new ConstraintViolationException("Database error", sqlException, "constraint violation");
		doThrow(new DataAccessException("Database error", constraintViolationException) {
		}).when(offreStageService).addOffreStage(any(OffreStageDTO.class));

		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setRole(Role.ADMIN);
		when(utilisateurService.getUtilisateurByToken("token")).thenReturn(utilisateurDTO);

		ResponseEntity<?> response = offreStageController.creerOffreStage("token", offreStageDTO, bindingResult);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Violation de contrainte unique : La valeur \"1L\" existe déjà.", response.getBody());
		
		verify(offreStageService, times(1)).addOffreStage(offreStageDTO);
	}
	
	@Test
	void testCreerOffreStageException() {
		when(bindingResult.hasErrors()).thenReturn(false);
		doThrow(new RuntimeException()).when(offreStageService).addOffreStage(any(OffreStageDTO.class));

		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setRole(Role.ADMIN);
		when(utilisateurService.getUtilisateurByToken("token")).thenReturn(utilisateurDTO);
		ResponseEntity<?> response = offreStageController.creerOffreStage("token", offreStageDTO, bindingResult);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Une erreur inattendue s'est produite.", response.getBody());
		
		verify(offreStageService, times(1)).addOffreStage(offreStageDTO);
	}
	
	@Test
	public void testRefuserOffreStage_Success() {
		Long offreStageId = 1L;
		String commentaireRefus = "Les compétences requises sont trop élevées";
		
		doNothing().when(offreStageService).refuserOffreStage(offreStageId, commentaireRefus);
		
		ResponseEntity<String> response = offreStageController.refuserOffreStage(offreStageId, commentaireRefus);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Les compétences requises sont trop élevées", response.getBody());
		verify(offreStageService, times(1)).refuserOffreStage(offreStageId, commentaireRefus);
	}
	
	@Test
	public void testRefuserOffreStage_NotFound() {
		Long offreStageId = 1L;
		String commentaireRefus = "Les compétences requises sont trop élevées";
		
		doThrow(new RuntimeException("Offre de stage non trouvée")).when(offreStageService).refuserOffreStage(offreStageId, commentaireRefus);
		
		ResponseEntity<String> response = offreStageController.refuserOffreStage(offreStageId, commentaireRefus);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Erreur lors du refus de l'offre de stage.", response.getBody());
		verify(offreStageService, times(1)).refuserOffreStage(offreStageId, commentaireRefus);
	}
	
	@Test
	void testModifierOffreStageSucces() {
		when(bindingResult.hasErrors()).thenReturn(false);
		doNothing().when(offreStageService).updateOffreStage(offreStageDTO);
		
		ResponseEntity<?> response = offreStageController.modifierOffreStage("token", offreStageDTO, bindingResult);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Offre de stage modifiée avec succès !", ((Map<?, ?>) response.getBody()).get("message"));
		verify(offreStageService, times(1)).updateOffreStage(offreStageDTO);
	}
	
	@Test
	void testModifierOffreStageUniqueConstraintViolationException() {
		when(bindingResult.hasErrors()).thenReturn(false);
		SQLException sqlException = new SQLException("Unique constraint violation (1L)");
		ConstraintViolationException violation = new ConstraintViolationException("Database error", sqlException, "constraint violation");
		doThrow(new DataAccessException("Database error", violation) {
		}).when(offreStageService).updateOffreStage(any(OffreStageDTO.class));
		
		ResponseEntity<?> response = offreStageController.modifierOffreStage("token", offreStageDTO, bindingResult);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Violation de contrainte unique : La valeur \"1L\" existe déjà.", ((Map<?, ?>) response.getBody()).get("message"));
	}
	
	@Test
	void testGetOffreStageSuccess() {
		List<OffreStageDTO> offres = List.of(offreStageDTO);
		when(offreStageService.getOffresStage()).thenReturn(offres);
		
		ResponseEntity<?> response = offreStageController.getOffreStage();
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(offres, response.getBody());
		verify(offreStageService, times(1)).getOffresStage();
	}
	
	@Test
	void testGetOffreStageNotFound() {
		when(offreStageService.getOffresStage()).thenThrow(new IllegalArgumentException("No offers found"));
		
		ResponseEntity<?> response = offreStageController.getOffreStage();
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Aucune offre de stage n'a été trouvée.", response.getBody());
		verify(offreStageService, times(1)).getOffresStage();
	}
	
	@Test
	void testGetOffreStageByIdSuccess() {
		Long offreStageId = 1L;
		when(offreStageService.getOffreStageById(offreStageId)).thenReturn(offreStageDTO);
		
		ResponseEntity<?> response = offreStageController.getOffreStageById(offreStageId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(offreStageDTO, response.getBody());
		verify(offreStageService, times(1)).getOffreStageById(offreStageId);
	}
	
	@Test
	void testGetOffreStageByIdError() {
		Long offreStageId = 1L;
		when(offreStageService.getOffreStageById(offreStageId)).thenThrow(new RuntimeException("Unexpected error"));
		
		ResponseEntity<?> response = offreStageController.getOffreStageById(offreStageId);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Une erreur inattendue s'est produite.", response.getBody());
		verify(offreStageService, times(1)).getOffreStageById(offreStageId);
	}
	
	@Test
	void testModifierOffreStageWithValidationErrors() {
		// Simuler une erreur de validation
		when(bindingResult.hasErrors()).thenReturn(true);
		FieldError fieldError = new FieldError("offreStageDTO", "field", "Field error message");
		when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
		
		ResponseEntity<?> response = offreStageController.modifierOffreStage("token", offreStageDTO, bindingResult);
		
		// Vérification du statut et du contenu de la réponse
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Map<?, ?> body = (Map<?, ?>) response.getBody();
		assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
		assertEquals(List.of("Field error message"), body.get("errors"));
		verify(bindingResult, times(1)).getFieldErrors();
	}
	
	@Test
	void testModifierOffreStageUnexpectedError() {
		when(bindingResult.hasErrors()).thenReturn(false);
		doThrow(new RuntimeException("Unexpected error")).when(offreStageService).updateOffreStage(any(OffreStageDTO.class));
		
		ResponseEntity<?> response = offreStageController.modifierOffreStage("token", offreStageDTO, bindingResult);
		
		// Vérification du statut et du message d'erreur
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		Map<?, ?> body = (Map<?, ?>) response.getBody();
		assertEquals("Une erreur inattendue s'est produite.", body.get("message"));
	}
	
	@Test
	void testModifierOffreStageCatchUnexpectedException() {
		when(bindingResult.hasErrors()).thenReturn(false);
		doThrow(new RuntimeException("Unexpected error")).when(offreStageService).updateOffreStage(any(OffreStageDTO.class));
		
		ResponseEntity<?> response = offreStageController.modifierOffreStage("token", offreStageDTO, bindingResult);
		
		// Vérification de la réponse en cas d'exception inattendue
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		Map<?, ?> body = (Map<?, ?>) response.getBody();
		assertEquals("Une erreur inattendue s'est produite.", body.get("message"));
	}
	
	@Test
	void testDeleteOffreStageSuccess() {
		Long offreStageId = 1L;
		
		ResponseEntity<?> response = offreStageController.deleteOffreStage(offreStageId);
		
		// Vérification de la réponse de succès
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Offre de stage supprimée avec succès !", response.getBody());
		verify(offreStageService, times(1)).deleteOffreStage(offreStageId);
	}
	
	@Test
	void testDeleteOffreStageUnexpectedError() {
		Long offreStageId = 1L;
		doThrow(new RuntimeException("Unexpected error")).when(offreStageService).deleteOffreStage(offreStageId);
		
		ResponseEntity<?> response = offreStageController.deleteOffreStage(offreStageId);
		
		// Vérification de la réponse en cas d'exception inattendue
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Une erreur inattendue s'est produite.", response.getBody());
	}
	
	@Test
	void testApprouverOffreStageSuccess() {
		Long offreStageId = 1L;
		
		doNothing().when(offreStageService).accepterOffreStage(offreStageId);
		
		ResponseEntity<?> response = offreStageController.accepterOffreStage(offreStageId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(offreStageService, times(1)).accepterOffreStage(offreStageId);
	}
	
	@Test
	void testApprouverOffreStageException() {
		Long offreStageId = 1L;
		
		doThrow(new RuntimeException("Unexpected error")).when(offreStageService).accepterOffreStage(offreStageId);
		
		ResponseEntity<?> response = offreStageController.accepterOffreStage(offreStageId);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		verify(offreStageService, times(1)).accepterOffreStage(offreStageId);
	}
}