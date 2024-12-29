package fructose.service;

import fructose.model.Cv;
import fructose.model.Utilisateur;
import fructose.model.enumerator.Role;
import fructose.repository.CvRepository;
import fructose.service.dto.CvDTO;
import fructose.service.dto.DepartementDTO;
import fructose.service.dto.UtilisateurDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CvServiceTest {
	
	@Mock
	private CvRepository cvRepository;
	
	@InjectMocks
	private CvService cvService;
	
	private UtilisateurDTO utilisateurDTO;
	
	@BeforeEach
	void setUp() {
		utilisateurDTO = new UtilisateurDTO();
		utilisateurDTO.setId(1L);
		utilisateurDTO.setFullName("John Doe");
		utilisateurDTO.setEmail("john.doe@example.com");
		utilisateurDTO.setPassword("password123");
		utilisateurDTO.setMatricule("1234567");
		utilisateurDTO.setRole(Role.ETUDIANT);
		utilisateurDTO.setDepartementDTO(new DepartementDTO());
		utilisateurDTO.setCompanyName("TechCorp");
	}
	
	@Test
	void testAddCv_Success() throws IOException {
		MockMultipartFile mockFile = new MockMultipartFile(
				"cv",
				"testCv.pdf",
				"application/pdf",
				"Dummy PDF Content".getBytes()
		);
		
		cvService.addCv(mockFile, utilisateurDTO, false, false);
		
		verify(cvRepository, times(1)).save(any(Cv.class));
	}
	
	@Test
	void testAddCv_Failure_DatabaseError() throws IOException {
		MockMultipartFile mockFile = new MockMultipartFile(
				"cv",
				"testCv.pdf",
				"application/pdf",
				"Dummy PDF Content".getBytes()
		);
		
		doThrow(new RuntimeException("Database error")).when(cvRepository).save(any(Cv.class));
		
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			cvService.addCv(mockFile, utilisateurDTO, false, false);
		});
		
		assertEquals("Une erreur inattendue est survenue lors de l'enregistrement du fichier PDF.", exception.getMessage());
	}
	
	
	@Test
	void testGetAllCvs_Success() {
		Utilisateur utilisateur = UtilisateurDTO.toEntity(utilisateurDTO);
		Cv cv1 = new Cv();
		Cv cv2 = new Cv();
		cv1.setUtilisateur(utilisateur);
		cv2.setUtilisateur(utilisateur);
		List<Cv> cvList = Arrays.asList(cv1, cv2);
		
		when(cvRepository.findAll()).thenReturn(cvList);
		
		List<CvDTO> result = cvService.getAllCvs();
		
		assertEquals(2, result.size());
	}
	
	@Test
	void testGetAllCvs_Failure() {
		doThrow(new RuntimeException("Database error")).when(cvRepository).findAll();
		
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			cvService.getAllCvs();
		});
		
		assertEquals("Une erreur est survenue lors de la récupération des CVs.", exception.getMessage());
	}
	
	@Test
	void testGetCvsByUser_Success() {
		Utilisateur utilisateur = UtilisateurDTO.toEntity(utilisateurDTO);
		
		Cv cv1 = new Cv();
		cv1.setId(1L);
		cv1.setFilename("testCv1.pdf");
		cv1.setUtilisateur(utilisateur);
		
		Cv cv2 = new Cv();
		cv2.setId(2L);
		cv2.setFilename("testCv2.pdf");
		cv2.setUtilisateur(utilisateur);
		
		List<Cv> cvList = Arrays.asList(cv1, cv2);
		
		when(cvRepository.getAllByUserId(utilisateurDTO.getId())).thenReturn(cvList);
		
		List<CvDTO> result = cvService.getCvsByUser(utilisateurDTO);
		
		assertEquals(2, result.size());
		assertEquals("testCv1.pdf", result.get(0).getFilename());
		assertEquals("testCv2.pdf", result.get(1).getFilename());
	}
	
	@Test
	void testGetCvFileContentById_Success() {
		Cv cv = new Cv();
		cv.setId(1L);
		cv.setFileContent("Dummy PDF Content".getBytes());
		
		when(cvRepository.findById(1L)).thenReturn(java.util.Optional.of(cv));
		
		byte[] result = cvService.getCvFileContentById(1L);
		
		assertEquals("Dummy PDF Content", new String(result));
	}
	
	@Test
	void testGetCvFileContentById_NotFound() {
		when(cvRepository.findById(1L)).thenReturn(java.util.Optional.empty());
		
		byte[] result = cvService.getCvFileContentById(1L);
		
		assertEquals(null, result);
	}
	
	@Test
	void testDeleteCvById_Success() {
		Cv cv = new Cv();
		cv.setId(1L);
		cv.setUtilisateur(UtilisateurDTO.toEntity(utilisateurDTO));
		
		when(cvRepository.findById(1L)).thenReturn(java.util.Optional.of(cv));
		
		boolean result = cvService.deleteCvById(1L, utilisateurDTO);
		
		assertEquals(true, result);
		verify(cvRepository, times(1)).delete(cv);
	}
	
	@Test
	void testDeleteCvById_Unauthorized() {
		Cv cv = new Cv();
		cv.setId(1L);
		Utilisateur autreUtilisateur = new Utilisateur();
		autreUtilisateur.setId(2L);
		cv.setUtilisateur(autreUtilisateur);
		
		when(cvRepository.findById(1L)).thenReturn(java.util.Optional.of(cv));
		
		boolean result = cvService.deleteCvById(1L, utilisateurDTO);
		
		assertEquals(false, result);
		verify(cvRepository, never()).delete(cv);
	}
	
	@Test
	void testDeleteCvById_Failure() {
		doThrow(new RuntimeException("Database error")).when(cvRepository).findById(anyLong());
		
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			cvService.deleteCvById(1L, utilisateurDTO);
		});
		
		assertEquals("Une erreur est survenue lors de la suppression du CV.", exception.getMessage());
	}
	
	@Test
	public void testRefuserCv_Success() {
		Long cvId = 1L;
		String commentaireRefus = "Le CV ne correspond pas aux critères";
		
		Cv cv = new Cv();
		cv.setId(cvId);
		when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
		
		cvService.refuserCv(cvId, commentaireRefus);
		
		verify(cvRepository, times(1)).findById(cvId);
		verify(cvRepository, times(1)).save(any(Cv.class));
		assert cv.getIsRefused();
		assert commentaireRefus.equals(cv.getCommentaireRefus());
	}
	
	@Test
	public void testRefuserCv_CvNotFound() {
		Long cvId = 1L;
		String commentaireRefus = "Le CV ne correspond pas aux critères";
		
		when(cvRepository.findById(cvId)).thenReturn(Optional.empty());
		
		assertThrows(RuntimeException.class, () -> cvService.refuserCv(cvId, commentaireRefus),
				"Une erreur est survenue lors de la suppression du CV.");
		
		verify(cvRepository, times(1)).findById(cvId);
		verify(cvRepository, never()).save(any(Cv.class));
	}
	
	@Test
	void testAccepterCv_Success() {
		Long cvId = 1L;
		Cv cv = new Cv();
		cv.setId(cvId);
		
		when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
		
		cvService.accepterCv(cvId);
		
		verify(cvRepository, times(1)).findById(cvId);
		verify(cvRepository, times(1)).save(cv);
		assertTrue(cv.getIsApproved());
	}
	
	@Test
	void testAccepterCv_CvNotFound() {
		Long cvId = 1L;
		
		when(cvRepository.findById(cvId)).thenReturn(Optional.empty());
		
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			cvService.accepterCv(cvId);
		});
		
		assertEquals("Une erreur est survenue lors de la suppression du CV.", exception.getMessage());
		verify(cvRepository, times(1)).findById(cvId);
		verify(cvRepository, never()).save(any(Cv.class));
	}
	
	@Test
	void testGetCvById_Success() {
		Long cvId = 1L;
		Cv cv = new Cv();
		cv.setId(cvId);
		
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setId(1L);
		cv.setUtilisateur(utilisateur);
		
		when(cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
		
		CvDTO result = cvService.getCvById(cvId);
		
		assertNotNull(result);
		assertEquals(cvId, result.getId());
		verify(cvRepository, times(1)).findById(cvId);
	}
	
	@Test
	void testGetCvById_NotFound() {
		Long cvId = 1L;
		
		when(cvRepository.findById(cvId)).thenReturn(Optional.empty());
		
		CvDTO result = cvService.getCvById(cvId);
		
		assertNull(result);
		verify(cvRepository, times(1)).findById(cvId);
	}
}