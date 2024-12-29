package fructose.controller;

import fructose.model.enumerator.Role;
import fructose.service.CandidatureService;
import fructose.service.ContratService;
import fructose.service.UtilisateurService;
import fructose.service.dto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContratControllerTest {
	
	@Mock
	private ContratService contratService;
	
	@Mock
	private UtilisateurService utilisateurService;
	
	@Mock
	private CandidatureService candidatureService;
	
	@InjectMocks
	private ContratController contratController;
	
	private CandidatureDTO candidatureDTO;
	
	private UtilisateurDTO admin;
	
	private OffreStageDTO offreStageDTO;
	
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testSaveContrat_Success() {
		String token = "validToken";
		CandidatureDTO candidatureDTO = new CandidatureDTO();
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		
		assertDoesNotThrow(() -> {
			contratController.saveContrat(token, candidatureDTO);
		});
		
		verify(contratService).saveContrat(any(ContratDTO.class));
	}
	
	@Test
	public void testGenerateContrat_Unauthorized() {
		String token = "invalidToken";
		Long id = 1L;
		
		when(utilisateurService.validationToken(token)).thenReturn(false);
		
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.generateContrat(token, id);
		});
		
		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
	}
	
	@Test
	public void testSaveContrat_Unauthorized() {
		String token = "invalidToken";
		CandidatureDTO candidatureDTO = new CandidatureDTO();
		
		when(utilisateurService.validationToken(token)).thenReturn(false);
		
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.saveContrat(token, candidatureDTO);
		});
		
		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
	}
	
	@Test
	public void testGenerateContrat_PdfNotFound() {
		String token = "validToken";
		Long id = 1L;
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		CandidatureDTO candidatureDTO = new CandidatureDTO();
		String pdfPath = "path/to/contract.pdf";
		File pdfFile = mock(File.class);
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		when(candidatureService.getCandidatureById(id)).thenReturn(candidatureDTO);
		when(contratService.generateContrat(candidatureDTO, utilisateurDTO)).thenReturn(pdfPath);
		when(pdfFile.exists()).thenReturn(false);
		
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.generateContrat(token, id);
		});
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
	}
	
	@Test
	public void testGenerateContrat_IOException() throws IOException {
		String token = "validToken";
		Long id = 1L;
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		CandidatureDTO candidatureDTO = new CandidatureDTO();
		String pdfPath = "path/to/contract.pdf";
		File pdfFile = mock(File.class);
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		when(candidatureService.getCandidatureById(id)).thenReturn(candidatureDTO);
		when(contratService.generateContrat(candidatureDTO, utilisateurDTO)).thenReturn(pdfPath);
		when(pdfFile.exists()).thenReturn(true);
		
		try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
			mockedFiles.when(() -> Files.readAllBytes(pdfFile.toPath())).thenThrow(IOException.class);
			
			ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
				contratController.generateContrat(token, id);
			});
			
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
		}
	}
	
	@Test
	public void testGenerateContrat_GeneralException() {
		String token = "validToken";
		Long id = 1L;
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		CandidatureDTO candidatureDTO = new CandidatureDTO();
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		when(candidatureService.getCandidatureById(id)).thenReturn(candidatureDTO);
		when(contratService.generateContrat(candidatureDTO, utilisateurDTO)).thenThrow(RuntimeException.class);
		
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.generateContrat(token, id);
		});
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
	}
	
	@Test
	public void testSaveContrat_GeneralException() {
		String token = "validToken";
		CandidatureDTO candidatureDTO = new CandidatureDTO();
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		
		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		doThrow(RuntimeException.class).when(contratService).saveContrat(any(ContratDTO.class));
		
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.saveContrat(token, candidatureDTO);
		});
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
	}

	@Test
	public void testGenerateContrat_Success() throws IOException {
		String token = "validToken";
		Long id = 1L;
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		CandidatureDTO candidatureDTO = new CandidatureDTO();
		String pdfPath = "contract_stage1.pdf";
		byte[] pdfBytes = new byte[]{1, 2, 3};

		// Create the file before the test
		Files.write(new File(pdfPath).toPath(), pdfBytes);

		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		when(candidatureService.getCandidatureById(id)).thenReturn(candidatureDTO);
		when(contratService.generateContrat(candidatureDTO, utilisateurDTO)).thenReturn(pdfPath);

		try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
			mockedFiles.when(() -> Files.readAllBytes(any())).thenReturn(pdfBytes);
			mockedFiles.when(() -> Files.delete(any())).thenAnswer(invocation -> null);

			ResponseEntity<byte[]> response = contratController.generateContrat(token, id);

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertArrayEquals(pdfBytes, response.getBody());
		}

		// Delete the file after the test
		Files.deleteIfExists(new File(pdfPath).toPath());
	}

	@Test
	public void testRefuserContrat_Success() {
		String token = "validToken";
		Long id = 1L;
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		ContratDTO contratDTO = new ContratDTO();

		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		when(contratService.getContratById(id)).thenReturn(contratDTO);

		assertDoesNotThrow(() -> {
			contratController.refuserContrat(token, id);
		});

		verify(contratService).refuserContrat(id, utilisateurDTO);
	}

	@Test
	public void testRefuserContrat_Unauthorized() {
		String token = "invalidToken";
		Long id = 1L;

		when(utilisateurService.validationToken(token)).thenReturn(false);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.refuserContrat(token, id);
		});

		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
	}

	@Test
	public void testRefuserContrat_GeneralException() {
		String token = "validToken";
		Long id = 1L;
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		ContratDTO contratDTO = new ContratDTO();

		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		when(contratService.getContratById(id)).thenReturn(contratDTO);
		doThrow(RuntimeException.class).when(contratService).refuserContrat(id, utilisateurDTO);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.refuserContrat(token, id);
		});

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
	}

	@Test
	public void testSignContrat_Success() {
		String token = "validToken";
		Long id = 1L;
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		ContratDTO contratDTO = new ContratDTO();

		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		when(contratService.getContratById(id)).thenReturn(contratDTO);

		assertDoesNotThrow(() -> {
			contratController.signerContrat(token, id);
		});

		verify(contratService).signContrat(id, utilisateurDTO);
	}

	@Test
	public void testSignContrat_Unauthorized() {
		String token = "invalidToken";
		Long id = 1L;

		when(utilisateurService.validationToken(token)).thenReturn(false);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.signerContrat(token, id);
		});

		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

		verify(contratService, never()).signContrat(id, null);
	}

	@Test
	public void testSignContrat_GeneralException() {
		String token = "validToken";
		Long id = 1L;
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		ContratDTO contratDTO = new ContratDTO();

		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		when(contratService.getContratById(id)).thenReturn(contratDTO);

		doThrow(RuntimeException.class).when(contratService).signContrat(id, utilisateurDTO);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.signerContrat(token, id);
		});

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
	}

	@Test
	public void testGetContratByCandidatureId_Success() {
		Long candidatureId = 1L;
		ContratSansCvDTO contratDTO = new ContratSansCvDTO();

		when(contratService.getContratByCandidatureId(candidatureId)).thenReturn(contratDTO);
		when(utilisateurService.validationToken("validToken")).thenReturn(true);

		assertEquals(contratDTO, contratController.getContratByCandidatureId("validToken", candidatureId));
	}

	@Test
	public void testGetContratByCandidatureId_Unauthorized() {
		Long candidatureId = 1L;

		when(utilisateurService.validationToken("invalidToken")).thenReturn(false);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.getContratByCandidatureId("invalidToken", candidatureId);
		});

		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
	}

	@Test
	public void testGetContratByCandidatureId_GeneralException() {
		Long candidatureId = 1L;

		when(utilisateurService.validationToken("validToken")).thenReturn(true);
		doThrow(RuntimeException.class).when(contratService).getContratByCandidatureId(candidatureId);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.getContratByCandidatureId("validToken", candidatureId);
		});

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
	}

	@Test
	public void testGenerateContratAlreadyExists_Success() throws IOException {
		String token = "validToken";
		Long id = 1L;
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		CandidatureDTO candidatureDTO = new CandidatureDTO();
		String pdfPath = "contract_stage1.pdf";
		byte[] pdfBytes = new byte[]{1, 2, 3};

		Files.write(new File(pdfPath).toPath(), pdfBytes);

		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(contratService.getContratById(id)).thenReturn(new ContratDTO());
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		when(candidatureService.getCandidatureById(id)).thenReturn(candidatureDTO);
		when(contratService.generateContrat(any(ContratDTO.class))).thenReturn(pdfPath);

		try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
			mockedFiles.when(() -> Files.readAllBytes(any())).thenReturn(new byte[]{1, 2, 3});
			mockedFiles.when(() -> Files.delete(any())).thenAnswer(invocation -> null);

			ResponseEntity<byte[]> response = contratController.generateContratAlreadyExists(token, id);

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertArrayEquals(new byte[]{1, 2, 3}, response.getBody());
		}

		// Delete the file after the test
		Files.deleteIfExists(new File(pdfPath).toPath());
	}

	@Test
	public void testGenerateContratAlreadyExists_Unauthorized() {
		String token = "invalidToken";
		Long id = 1L;

		when(utilisateurService.validationToken(token)).thenReturn(false);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.generateContratAlreadyExists(token, id);
		});

		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
	}

	@Test
	public void testGenerateContratAlreadyExists_PdfNotFound() {
		String token = "validToken";
		Long id = 1L;
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		ContratDTO contratDTO = new ContratDTO();
		String pdfPath = "path/to/contract.pdf";

		File pdfFile = mock(File.class);

		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		when(contratService.getContratById(id)).thenReturn(contratDTO);
		when(contratService.generateContrat(contratDTO)).thenReturn(pdfPath);
		when(pdfFile.exists()).thenReturn(false);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.generateContratAlreadyExists(token, id);
		});

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
	}

	@Test
	public void testGetContrats_Success() {
		String token = "validToken";
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setRole(Role.ADMIN);

		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		when(contratService.getContrats()).thenReturn(Collections.emptyList());

		ResponseEntity<?> response = contratController.getContrats(token);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(Collections.emptyList(), response.getBody());
	}

	@Test
	public void testGetContrats_Unauthorized() {
		String token = "invalidToken";

		when(utilisateurService.validationToken(token)).thenReturn(false);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.getContrats(token);
		});

		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
	}

	@Test
	public void testGetContrats_Forbidden() {
		String token = "validToken";
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setRole(Role.ETUDIANT);

		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.getContrats(token);
		});

		assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
	}

	@Test
	public void testGetContrats_InternalServerError() {
		String token = "validToken";
		UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setRole(Role.ADMIN);

		when(utilisateurService.validationToken(token)).thenReturn(true);
		when(utilisateurService.getUtilisateurByToken(token)).thenReturn(utilisateurDTO);
		when(contratService.getContrats()).thenThrow(RuntimeException.class);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			contratController.getContrats(token);
		});

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
	}
}