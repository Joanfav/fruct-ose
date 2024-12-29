package fructose.controller;

import fructose.service.CvService;
import fructose.service.UtilisateurService;
import fructose.service.dto.CvDTO;
import fructose.service.dto.UtilisateurDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CvControllerTest {
	private static final int MAX_FILE_SIZE_B = 1048576;
	private MockMvc mockMvc;
	
	@Mock
	private CvService cvService;
	
	@Mock
	private UtilisateurService utilisateurService;
	
	@InjectMocks
	private CvController cvController;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(cvController).build();
	}
	
	@Test
	public void testEnregistrerCV_Success() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "test content".getBytes());
		String token = "validToken";
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(new UtilisateurDTO());
		doNothing().when(cvService).addCv(any(MultipartFile.class), any(UtilisateurDTO.class), eq(false), eq(false));
		
		ResponseEntity<String> response = cvController.enregistrerCV(token, file);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Fichier PDF enregistré avec succès.", response.getBody());
	}
	
	@Test
	public void testEnregistrerCV_Failure_FileEmpty() {
		MockMultipartFile file = new MockMultipartFile("file", "", "", new byte[0]);
		String token = "validToken";
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		
		ResponseEntity<String> response = cvController.enregistrerCV(token, file);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Le fichier est vide. Veuillez télécharger un fichier PDF valide.", response.getBody());
	}
	
	@Test
	public void testEnregistrerCV_Failure_InvalidFileType() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
		String token = "validToken";
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		
		ResponseEntity<String> response = cvController.enregistrerCV(token, file);
		
		assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
		assertEquals("Le fichier n'est pas au format PDF. Veuillez télécharger un fichier PDF.", response.getBody());
	}
	
	@Test
	public void testEnregistrerCV_Failure_TokenInvalid() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "test content".getBytes());
		String token = "invalidToken";
		
		when(utilisateurService.validationToken(token)).thenReturn(false);
		
		ResponseEntity<String> response = cvController.enregistrerCV(token, file);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Le Token est invalid", response.getBody());
	}
	
	@Test
	public void testEnregistrerCV_Failure_ReadError() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "test content".getBytes());
		String token = "validToken";
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(new UtilisateurDTO());
		doThrow(new IOException("Error")).when(cvService).addCv(any(MultipartFile.class), any(UtilisateurDTO.class), eq(false), eq(false));
		
		ResponseEntity<String> response = cvController.enregistrerCV(token, file);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Erreur lors de la lecture du fichier.", response.getBody());
	}
	
	@Test
	public void testEnregistrerCV_Failure_UnexpectedError() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "test content".getBytes());
		String token = "validToken";
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(new UtilisateurDTO());
		doThrow(new RuntimeException("Unexpected error")).when(cvService).addCv(any(MultipartFile.class), any(UtilisateurDTO.class), eq(false), eq(false));
		
		ResponseEntity<String> response = cvController.enregistrerCV(token, file);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Une erreur inattendue s'est produite.", response.getBody());
	}
	
	@Test
	public void testGetAllCvs_Success() {
		// Simuler la validation du token et la récupération de CVs
		when(utilisateurService.validationToken("validToken")).thenReturn(true);
		when(cvService.getAllCvs()).thenReturn(Collections.singletonList(new CvDTO())); // Ajouter un CV fictif
		
		ResponseEntity<List<CvDTO>> response = cvController.getAllCvs("validToken");
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size()); // Vérifiez que la liste contient un élément
	}
	
	@Test
	public void testGetAllCvs_RuntimeException() {
		// Simuler la validation du token et une exception lors de la récupération des CVs
		when(utilisateurService.validationToken("validToken")).thenReturn(true);
		when(cvService.getAllCvs()).thenThrow(new RuntimeException("Database error"));
		
		ResponseEntity<List<CvDTO>> response = cvController.getAllCvs("validToken");
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}
	
	@Test
	public void testGetAllCvs_Unauthorized() {
		String token = "invalidToken";
		
		when(utilisateurService.validationToken(token)).thenReturn(false);
		
		ResponseEntity<List<CvDTO>> response = cvController.getAllCvs(token);
		
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	@Test
	public void testGetCVs_Success() {
		String token = "validToken";
		when(utilisateurService.validationToken(token)).thenReturn(true);
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		
		List<CvDTO> cvDTOList = Arrays.asList(new CvDTO(), new CvDTO());
		when(cvService.getCvsByUser(utilisateurDTO)).thenReturn(cvDTOList);
		
		ResponseEntity<?> response = cvController.getCVs(token);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(cvDTOList, response.getBody());
	}
	
	@Test
	public void testGetCVs_InvalidToken() {
		String token = "invalidToken";
		when(utilisateurService.validationToken(token)).thenReturn(false);
		
		ResponseEntity<?> response = cvController.getCVs(token);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Le Token est invalid", response.getBody());
	}
	
	// Tests pour la méthode getCVFile
	@Test
	public void testGetCVFile_Success() {
		String token = "validToken";
		Long cvId = 1L;
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		byte[] fileContent = new byte[]{1, 2, 3};
		when(cvService.getCvFileContentById(cvId)).thenReturn(fileContent);
		
		ResponseEntity<byte[]> response = cvController.getCVContenuFile(token, cvId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(fileContent, response.getBody());
	}
	
	@Test
	public void testGetCVFile_FileNotFound() {
		String token = "validToken";
		Long cvId = 999L;
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(cvService.getCvFileContentById(cvId)).thenReturn(null);
		
		ResponseEntity<byte[]> response = cvController.getCVContenuFile(token, cvId);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
	}
	
	@Test
	public void testGetCVFile_InvalidToken() {
		String token = "invalidToken";
		Long cvId = 1L;
		
		when(utilisateurService.validationToken(token)).thenReturn(false);
		
		ResponseEntity<byte[]> response = cvController.getCVContenuFile(token, cvId);
		
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	@Test
	public void testDeleteCv_Success() {
		String token = "validToken";
		Long cvId = 1L;
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		when(cvService.deleteCvById(cvId, utilisateurDTO)).thenReturn(true);
		
		ResponseEntity<String> response = cvController.deleteCv(token, cvId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Le CV a été supprimé avec succès.", response.getBody());
	}
	
	@Test
	public void testDeleteCv_NotFound() {
		String token = "validToken";
		Long cvId = 999L;
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		when(cvService.deleteCvById(cvId, utilisateurDTO)).thenReturn(false);
		
		ResponseEntity<String> response = cvController.deleteCv(token, cvId);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Le CV n'a pas été trouvé ou l'utilisateur n'est pas autorisé à le supprimer.", response.getBody());
	}
	
	@Test
	public void testDeleteCv_Unauthorized() {
		String token = "invalidToken";
		Long cvId = 1L;
		
		when(utilisateurService.validationToken(token)).thenReturn(false);
		
		ResponseEntity<String> response = cvController.deleteCv(token, cvId);
		
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertEquals("Le Token est invalide", response.getBody());
	}
	
	@Test
	public void testDeleteCv_Exception() {
		String token = "validToken";
		Long cvId = 1L;
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		doThrow(new RuntimeException("Unexpected error")).when(cvService).deleteCvById(cvId, utilisateurDTO);
		
		ResponseEntity<String> response = cvController.deleteCv(token, cvId);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Une erreur inattendue s'est produite lors de la suppression du CV.", response.getBody());
	}
	
	@Test
	public void testGetCVFile_InternalServerError() {
		String token = "validToken";
		Long cvId = 1L;
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(cvService.getCvFileContentById(cvId)).thenThrow(new RuntimeException("Unexpected error"));
		
		ResponseEntity<byte[]> response = cvController.getCVContenuFile(token, cvId);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}
	
	@Test
	public void testEnregistrerCV_Failure_FileTooLarge() throws Exception {
		byte[] largeFileContent = new byte[(int) (MAX_FILE_SIZE_B + 1)]; // 1 octet de plus que la limite
		MockMultipartFile file = new MockMultipartFile("file", "largeFile.pdf", MediaType.APPLICATION_PDF_VALUE, largeFileContent);
		String token = "validToken";
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		
		ResponseEntity<String> response = cvController.enregistrerCV(token, file);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Le fichier doit faire moins de 1024 Kb.", response.getBody());
	}
	
	@Test
	public void testRefuserCv_Success() {
		Long cvId = 1L;
		String commentaireRefus = "Le CV ne correspond pas aux attentes";
		
		doNothing().when(cvService).refuserCv(cvId, commentaireRefus);
		
		ResponseEntity<String> response = cvController.refuserCv(cvId, commentaireRefus);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Le CV ne correspond pas aux attentes", response.getBody());
		verify(cvService, times(1)).refuserCv(cvId, commentaireRefus);
	}
	
	@Test
	public void testRefuserCv_NotFound() {
		Long cvId = 1L;
		String commentaireRefus = "Le CV ne correspond pas aux attentes";
		
		doThrow(new RuntimeException("CV non trouvé")).when(cvService).refuserCv(cvId, commentaireRefus);
		
		ResponseEntity<String> response = cvController.refuserCv(cvId, commentaireRefus);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Erreur lors du refus du CV.", response.getBody());
		verify(cvService, times(1)).refuserCv(cvId, commentaireRefus);
	}
	
	@Test
	void testAccepterOffreStage_Success() {
		Long cvId = 1L;
		
		doNothing().when(cvService).accepterCv(cvId);
		
		ResponseEntity<?> response = cvController.accepterOffreStage(cvId);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(cvService, times(1)).accepterCv(cvId);
	}
	
	@Test
	void testAccepterOffreStage_Failure() {
		Long cvId = 1L;
		
		doThrow(new RuntimeException("Unexpected error")).when(cvService).accepterCv(cvId);
		
		ResponseEntity<?> response = cvController.accepterOffreStage(cvId);
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Erreur lors du refus de l'offre de stage.", response.getBody());
		verify(cvService, times(1)).accepterCv(cvId);
	}
	
	@Test
	void testGetCVFile_Unauthorized() {
		String token = "invalidToken";
		Long cvId = 1L;
		
		when(utilisateurService.validationToken(token)).thenReturn(false);
		
		ResponseEntity<CvDTO> response = cvController.getCVFile(token, cvId);
		
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		verify(utilisateurService, times(1)).validationToken(token);
		verify(cvService, never()).getCvById(cvId);
	}
	
	@Test
	void testGetCVFile_NotFound() {
		String token = "validToken";
		Long cvId = 1L;
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(cvService.getCvById(cvId)).thenReturn(null);
		
		ResponseEntity<CvDTO> response = cvController.getCVFile(token, cvId);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		verify(utilisateurService, times(1)).validationToken(token);
		verify(cvService, times(1)).getCvById(cvId);
	}
}