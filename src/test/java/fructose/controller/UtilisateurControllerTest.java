package fructose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fructose.model.enumerator.Role;
import fructose.service.DepartementService;
import fructose.service.UtilisateurService;
import fructose.service.dto.DepartementDTO;
import fructose.service.dto.UtilisateurDTO;
import fructose.service.dto.auth.LoginDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UtilisateurControllerTest {
	
	private MockMvc mockMvc;
	
	@InjectMocks
	private UtilisateurController utilisateurController;
	
	@Mock
	private UtilisateurService utilisateurService;
	
	@Mock
	private DepartementService departementService;
	
	@Mock
	private BindingResult bindingResult;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(utilisateurController).build();
	}
	
	@Test
	void testCreerUtilisateur_Success() {
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setRole(Role.ETUDIANT);
		when(bindingResult.hasErrors()).thenReturn(false);
		when(utilisateurService.isValidRole(String.valueOf(utilisateurDTO.getRole()))).thenReturn(true);
		
		ResponseEntity<?> response = utilisateurController.creerUtilisateur(utilisateurDTO, bindingResult);
		
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Utilisateur créé avec succès !", response.getBody());
	}
	
	@Test
	void testCreerUtilisateur_ValidationErrors() {
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		when(bindingResult.hasErrors()).thenReturn(true);
		when(bindingResult.getFieldErrors()).thenReturn(
			List.of(new FieldError("utilisateurDTO", "email", "L'adresse courriel doit être valide"))
		);
		
		ResponseEntity<?> response = utilisateurController.creerUtilisateur(utilisateurDTO, bindingResult);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Erreur de validation : L'adresse courriel doit être valide", response.getBody());
	}
	
	@Test
	void testCreerUtilisateur_DataAccessException() {
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setRole(Role.ETUDIANT);
		when(bindingResult.hasErrors()).thenReturn(false);
		when(utilisateurService.isValidRole(utilisateurDTO.getRole().toString())).thenReturn(true);
		doThrow(new DataAccessException("Database error") {
		}).when(utilisateurService).addUtilisateur(utilisateurDTO);
		
		ResponseEntity<?> response = utilisateurController.creerUtilisateur(utilisateurDTO, bindingResult);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Erreur lors de la création de l'utilisateur.", response.getBody());
	}
	
	@Test
	void testCreerUtilisateur_GenericException() {
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setRole(Role.ETUDIANT);
		when(bindingResult.hasErrors()).thenReturn(false);
		when(utilisateurService.isValidRole(utilisateurDTO.getRole().toString())).thenReturn(true);
		doThrow(new RuntimeException("Generic error")).when(utilisateurService).addUtilisateur(utilisateurDTO);
		
		ResponseEntity<?> response = utilisateurController.creerUtilisateur(utilisateurDTO, bindingResult);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Une erreur inattendue s'est produite.", response.getBody());
	}
	
	@Test
	void testCreerUtilisateur_ConstraintViolationException() {
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setRole(Role.ETUDIANT);
		when(bindingResult.hasErrors()).thenReturn(false);
		when(utilisateurService.isValidRole(utilisateurDTO.getRole().toString())).thenReturn(true);
		
		// Simuler une violation de contrainte unique
		org.hibernate.exception.ConstraintViolationException violationException =
			new org.hibernate.exception.ConstraintViolationException(
				"Violation de contrainte unique",
				new SQLException("ERREUR: duplicate key value violates unique constraint \"unique_email\" (email)"),
				"email"
			);
		
		doThrow(new DataAccessException("Database error", violationException) {
		}).when(utilisateurService).addUtilisateur(utilisateurDTO);
		
		ResponseEntity<?> response = utilisateurController.creerUtilisateur(utilisateurDTO, bindingResult);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Violation de contrainte unique : La valeur \"email\" existe déjà.", response.getBody());
	}
	
	@Test
	public void testConnexionWithValidationErrors() {
		
		LoginDTO loginDTO = new LoginDTO("invalid", "123");
		
		when(bindingResult.hasErrors()).thenReturn(true);
		when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("email", "email", "Email invalide")));
		
		ResponseEntity<?> response = utilisateurController.connexion(loginDTO, bindingResult);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Erreur de validation : Email invalide", response.getBody());
	}
	
	@Test
	public void testConnexion_Success() throws Exception {
		
		LoginDTO loginDTO = new LoginDTO("user@example.com", "password");
		when(utilisateurService.authenticateUser(anyString(), anyString())).thenReturn("mockToken");
		
		mockMvc.perform(post("/connexion")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(loginDTO)))
			.andExpect(status().isOk())
			.andExpect(content().string("Token : mockToken"));
	}
	
	@Test
	public void testConnexionException() throws Exception {
		
		LoginDTO loginDTO = new LoginDTO("user@example.com", "wrongPassword");
		when(utilisateurService.authenticateUser(anyString(), anyString())).thenThrow(new RuntimeException("Authentication failed"));
		
		mockMvc.perform(post("/connexion")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(loginDTO)))
			.andExpect(status().isBadRequest())
			.andExpect(content().string("Authentication failed"));
	}
	
	@Test
	void testCheckEmail() {
		String email = "test@example.com";
		
		when(utilisateurService.isEmailTaken(email)).thenReturn(true);
		ResponseEntity<Map<String, Boolean>> response = utilisateurController.checkEmail(email);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(Objects.requireNonNull(response.getBody()).get("emailTaken"));
	}
	
	@Test
	void testCheckMatricule() {
		String matricule = "123456";
		when(utilisateurService.isMatriculeTaken(matricule)).thenReturn(true);
		
		ResponseEntity<Map<String, Boolean>> response = utilisateurController.checkMatricule(matricule);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(Objects.requireNonNull(response.getBody()).get("matriculeTaken"));
	}
	
	@Test
	void testValiderToken_Valid() {
		String token = "Bearer validToken";
		when(utilisateurService.validationToken("validToken")).thenReturn(true);
		
		ResponseEntity<?> response = utilisateurController.validerToken(token);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Token valide : true", response.getBody());
	}
	
	@Test
	void testValiderToken_Invalid() {
		String token = "Bearer invalidToken";
		when(utilisateurService.validationToken("invalidToken")).thenReturn(false);
		
		ResponseEntity<?> response = utilisateurController.validerToken(token);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Token valide : false", response.getBody());
	}
	
	@Test
	void testCreerUtilisateur_InvalidRole() {
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setRole(Role.ETUDIANT);
		
		when(bindingResult.hasErrors()).thenReturn(false);
		when(utilisateurService.isValidRole(utilisateurDTO.getRole().toString())).thenReturn(false); // Rôle invalide
		
		ResponseEntity<?> response = utilisateurController.creerUtilisateur(utilisateurDTO, bindingResult);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Rôle invalide.", response.getBody());
	}
	
	@Test
	void testValiderToken_Exception() {
		String token = "Bearer invalidToken";
		
		when(utilisateurService.validationToken("invalidToken")).thenThrow(new RuntimeException("Invalid token"));
		
		ResponseEntity<?> response = utilisateurController.validerToken(token);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Token invalide.", response.getBody());
	}
	
	@Test
	void testGetInfosUtilisateur_Exception() {
		String token = "Bearer validToken";
		
		when(utilisateurService.getUtilisateurByToken(token)).thenThrow(new RuntimeException("Utilisateur non trouvé"));
		
		ResponseEntity<?> response = utilisateurController.getInfosUtilisateur(token);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Impossible de récupérer les infos utilisateur: Utilisateur non trouvé", response.getBody());
	}
	
	@Test
	void testGetInfosUtilisateur_Success() {
		String token = "Bearer validToken";
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setRole(Role.ETUDIANT);
		
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		
		ResponseEntity<?> response = utilisateurController.getInfosUtilisateur(token);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(utilisateurDTO, response.getBody());
	}
	
	@Test
	void testGetNonApprovedUsers_Success() {
		String token = "Bearer validToken";
		List<UtilisateurDTO> mockUsers = List.of(new UtilisateurDTO(), new UtilisateurDTO());
		
		when(utilisateurService.verifyRoleEligibilityByToken(token, Role.ADMIN)).thenReturn(true);
		when(utilisateurService.getNonApprovedUsers()).thenReturn(mockUsers);
		
		ResponseEntity<List<UtilisateurDTO>> response = utilisateurController.getNonApprovedUsers(token);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(mockUsers, response.getBody());
	}

	@Test
	void testGetNonApprovedUsers_Unauthorized() {
		String token = "Bearer invalidToken";

		when(utilisateurService.verifyRoleEligibilityByToken(token, Role.ADMIN)).thenReturn(false);

		ResponseEntity<List<UtilisateurDTO>> response = utilisateurController.getNonApprovedUsers(token);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNull(response.getBody());
	}
	
	@Test
	void testApproveUser_Success() {
		Long userId = 1L;
		String token = "Bearer validToken";
		
		when(utilisateurService.verifyRoleEligibilityByToken(token, Role.ADMIN)).thenReturn(true);
		
		ResponseEntity<?> response = utilisateurController.approveUser(token, userId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("User approved successfully.", response.getBody());
	}

	@Test
	void testApproveUser_Unauthorized() {
		Long userId = 1L;
		String token = "Bearer invalidToken";

		when(utilisateurService.verifyRoleEligibilityByToken(token, Role.ADMIN)).thenReturn(false);

		ResponseEntity<?> response = utilisateurController.approveUser(token, userId);

		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		assertEquals("403 Unauthorized", response.getBody());
	}
	
	@Test
	void testApproveUser_Exception() {
		Long userId = 1L;
		String token = "Bearer validToken";
		
		when(utilisateurService.verifyRoleEligibilityByToken(token, Role.ADMIN)).thenReturn(true);
		doThrow(new RuntimeException("Approval failed")).when(utilisateurService).approveUser(userId);
		
		ResponseEntity<?> response = utilisateurController.approveUser(token, userId);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Error approving user: Approval failed", response.getBody());
	}
	
	@Test
	void testDeleteUtilisateurByID_Success() {
		Long userId = 1L;
		String token = "Bearer validToken";
		
		when(utilisateurService.verifyRoleEligibilityByToken(token, Role.ADMIN)).thenReturn(true);
		
		ResponseEntity<?> response = utilisateurController.deleteUtilisateurByID(token, userId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("User rejected and deleted successfully.", response.getBody());
	}

	@Test
	void testDeleteUtilisateurByID_Unauthorized() {
		Long userId = 1L;
		String token = "Bearer invalidToken";

		when(utilisateurService.verifyRoleEligibilityByToken(token, Role.ADMIN)).thenReturn(false);

		ResponseEntity<?> response = utilisateurController.deleteUtilisateurByID(token, userId);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("403 Unauthorized", response.getBody());
	}
	
	@Test
	void testDeleteUtilisateurByID_Exception() {
		Long userId = 1L;
		String token = "Bearer validToken";
		
		when(utilisateurService.verifyRoleEligibilityByToken(token, Role.ADMIN)).thenReturn(true);
		doThrow(new RuntimeException("Deletion failed")).when(utilisateurService).deleteUtilisateurByID(userId, utilisateurService.getRoleById(userId));
		
		ResponseEntity<?> response = utilisateurController.deleteUtilisateurByID(token, userId);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Error rejecting user: Deletion failed", response.getBody());
	}
	
	@Test
	void testCheckDepartement() {
		String validDepartementName = "ValidDepartement";
		String invalidDepartementName = "InvalidDepartement";
		DepartementDTO departementDTO = new DepartementDTO();
		departementDTO.setNom(validDepartementName);
		
		when(departementService.getDepartementByNom(validDepartementName)).thenReturn(departementDTO);
		when(departementService.getDepartementByNom(invalidDepartementName)).thenReturn(null);
		
		ResponseEntity<Map<String, DepartementDTO>> validResponse = utilisateurController.checkDepartement(validDepartementName);
		assertEquals(HttpStatus.OK, validResponse.getStatusCode());
		assertEquals(departementDTO, validResponse.getBody().get("departement"));
		
		ResponseEntity<Map<String, DepartementDTO>> invalidResponse = utilisateurController.checkDepartement(invalidDepartementName);
		assertEquals(HttpStatus.NOT_FOUND, invalidResponse.getStatusCode());
		assertNull(invalidResponse.getBody());
	}

	@Test
	void testGetEmployeursApproved_Success() {
		String token = "Bearer validToken";
		List<UtilisateurDTO> mockEmployeurs = List.of(new UtilisateurDTO(), new UtilisateurDTO());

		when(utilisateurService.verifyRoleEligibilityByToken(token, Role.ADMIN)).thenReturn(true);
		when(utilisateurService.getEmployeursApproved()).thenReturn(mockEmployeurs);

		ResponseEntity<List<UtilisateurDTO>> response = utilisateurController.getEmployeursApproved(token);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(mockEmployeurs, response.getBody());
	}

	@Test
	void testGetEmployeursApproved_Unauthorized() {
		String token = "Bearer invalidToken";

		when(utilisateurService.verifyRoleEligibilityByToken(token, Role.ADMIN)).thenReturn(false);

		ResponseEntity<List<UtilisateurDTO>> response = utilisateurController.getEmployeursApproved(token);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void testGetEmployeur_Success() {
		String token = "Bearer validToken";
		Long employeurId = 1L;
		UtilisateurDTO mockEmployeur = new UtilisateurDTO();

		when(utilisateurService.verifyRoleEligibilityByToken(token, Role.ADMIN)).thenReturn(true);
		when(utilisateurService.getEmployeurById(employeurId)).thenReturn(mockEmployeur);

		ResponseEntity<?> response = utilisateurController.getEmployeur(token, employeurId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(mockEmployeur, ((Map<String, UtilisateurDTO>) response.getBody()).get("owner"));
	}

	@Test
	void testGetEmployeur_NotFound() {
		String token = "Bearer validToken";
		Long employeurId = 1L;

		when(utilisateurService.verifyRoleEligibilityByToken(token, Role.ADMIN)).thenReturn(true);
		when(utilisateurService.getEmployeurById(employeurId)).thenReturn(null);

		ResponseEntity<?> response = utilisateurController.getEmployeur(token, employeurId);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Employeur not found", response.getBody());
	}

	@Test
	void testGetEmployeur_Unauthorized() {
		String token = "Bearer invalidToken";
		Long employeurId = 1L;

		when(utilisateurService.verifyRoleEligibilityByToken(token, Role.ADMIN)).thenReturn(false);

		ResponseEntity<?> response = utilisateurController.getEmployeur(token, employeurId);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("403 Unauthorized", response.getBody());
	}

	@Test
	void testGetEmployeur_Exception() {
		String token = "Bearer validToken";
		Long employeurId = 1L;

		when(utilisateurService.verifyRoleEligibilityByToken(token, Role.ADMIN)).thenReturn(true);
		when(utilisateurService.getEmployeurById(employeurId)).thenThrow(new RuntimeException("Error retrieving employeur"));

		ResponseEntity<?> response = utilisateurController.getEmployeur(token, employeurId);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Error retrieving employeur: Error retrieving employeur", response.getBody());
	}
}