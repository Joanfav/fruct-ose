package fructose.service;

import fructose.model.Candidature;
import fructose.model.Contrat;
import static org.mockito.ArgumentMatchers.argThat;
import fructose.model.enumerator.Role;
import fructose.repository.ContratRepository;
import fructose.service.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContratServiceTest {
	
	@Mock
	private ContratRepository contratRepository;
	
	@InjectMocks
	private ContratService contratService;
	
	private CandidatureDTO candidatureDTO;
	private UtilisateurDTO admin;
	private OffreStageDTO offreStageDTO;
	
	@BeforeEach
	void setUp() {
		DepartementDTO departementDTO= new DepartementDTO(1L, "Informatique");
		admin = new UtilisateurDTO();
		admin.setId(1L);
		admin.setFullName("Test User");
		admin.setEmail("test@example.com");
		admin.setPassword("password");
		admin.setMatricule("12345");
		admin.setRole(Role.ETUDIANT);
		admin.setCompanyName("Test Company");
		admin.setIsApproved(true);
		admin.setDepartementDTO(departementDTO);
		
		offreStageDTO = new OffreStageDTO();
		offreStageDTO.setId(1L);
		offreStageDTO.setNom("Test Offer");
		offreStageDTO.setDescription("Test Offer Description");
		offreStageDTO.setTauxHoraire(1000.0);
		offreStageDTO.setOwnerDTO(admin);
		offreStageDTO.setAdresse("Test Address");
		offreStageDTO.setDateFin(LocalDate.now());
		offreStageDTO.setDateDebut(LocalDate.now());
		offreStageDTO.setTypeEmploi("presentiel");
		offreStageDTO.setModaliteTravail("temps_plein");
		offreStageDTO.setDepartementDTO(departementDTO);
		
		
		
		candidatureDTO = new CandidatureDTO();
		candidatureDTO.setId(1L);
		candidatureDTO.setEtudiantDTO(admin);
		candidatureDTO.setOffreStageDTO(offreStageDTO);
	}
	
	
	
	@Test
	public void testGenerateContrat_Success() {
		String result = contratService.generateContrat(candidatureDTO, admin);
		
		assertEquals("contract_stage1.pdf", result);
	}
	
	@Test
	public void testGenerateContrat_ExceptionCandidatureNull() {
		assertThrows(RuntimeException.class, () -> {
			contratService.generateContrat(null, admin);
		});
	}
	
	@Test
	public void testGenerateContrat_ExceptionAdminNull() {
		assertThrows(RuntimeException.class, () -> {
			contratService.generateContrat(candidatureDTO, null);
		});
	}
	
	@Test
	public void testSaveContrat() {
		ContratDTO contratDTO = new ContratDTO();
		contratDTO.setCandidatureDTO(candidatureDTO);
		contratDTO.setGestionnaire(admin);
		
		Contrat expectedContrat = ContratDTO.toEntity(contratDTO);
		
		contratService.saveContrat(contratDTO);
		
		verify(contratRepository, times(1)).save(argThat(contrat ->
			contrat.getCandidature().getId().equals(expectedContrat.getCandidature().getId()) &&
				contrat.getCandidature().getEtudiant().getId().equals(expectedContrat.getCandidature().getEtudiant().getId()) &&
				contrat.getCandidature().getOffreStage().getId().equals(expectedContrat.getCandidature().getOffreStage().getId()) &&
				contrat.getGestionnaire().getId().equals(expectedContrat.getGestionnaire().getId())
		));
	}

	@Test
	public void testRefuserContratSuccessEtudiant() {
		Long contratId = 1L;
		UtilisateurDTO etudiant = new UtilisateurDTO();
		etudiant.setId(2L);
		etudiant.setRole(Role.ETUDIANT);

		Contrat contrat = new Contrat();
		contrat.setId(contratId);

		when(contratRepository.findById(contratId)).thenReturn(Optional.of(contrat));

		contratService.refuserContrat(contratId, etudiant);

		verify(contratRepository, times(1)).save(argThat(savedContrat ->
				"Refuse".equals(savedContrat.getSignatureEtudiant()) &&
						savedContrat.getId().equals(contratId)
		));
	}

	@Test
	public void testRefuserContratSuccessEmployeur() {
		Long contratId = 1L;
		UtilisateurDTO employeur = new UtilisateurDTO();
		employeur.setId(2L);
		employeur.setRole(Role.EMPLOYEUR);

		Contrat contrat = new Contrat();
		contrat.setId(contratId);

		when(contratRepository.findById(contratId)).thenReturn(Optional.of(contrat));

		contratService.refuserContrat(contratId, employeur);

		verify(contratRepository, times(1)).save(argThat(savedContrat ->
				"Refuse".equals(savedContrat.getSignatureEmployeur()) &&
						savedContrat.getId().equals(contratId)
		));
	}

	@Test
	public void testRefuserContrat_ExceptionContratNotFound() {
		Long contratId = 1L;
		UtilisateurDTO utilisateur = new UtilisateurDTO();
		utilisateur.setId(2L);
		utilisateur.setRole(Role.ETUDIANT);

		when(contratRepository.findById(contratId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> {
			contratService.refuserContrat(contratId, utilisateur);
		});
	}

	@Test
	public void testRefuserContrat_ExceptionIdNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			contratService.refuserContrat(null, admin);
		});
	}

	@Test
	public void testRefuserContrat_ExceptionUtilisateurNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			contratService.refuserContrat(1L, null);
		});
	}

	@Test
	public void testRefuserContrat_ExceptionUtilisateurNotEmployeurOrEtudiant() {
		UtilisateurDTO utilisateur = new UtilisateurDTO();
		utilisateur.setId(2L);
		utilisateur.setRole(Role.ADMIN);

		assertThrows(IllegalArgumentException.class, () -> {
			contratService.refuserContrat(1L, utilisateur);
		});
	}

	@Test
	public void testSignerContratSuccessEmployeur() {
		Long contratId = 1L;
		UtilisateurDTO employeur = new UtilisateurDTO();
		employeur.setId(2L);
		employeur.setRole(Role.EMPLOYEUR);
		employeur.setFullName("Employeur");

		Contrat contrat = new Contrat();
		contrat.setId(contratId);

		when(contratRepository.findById(contratId)).thenReturn(Optional.of(contrat));

		contratService.signContrat(contratId, employeur);

		verify(contratRepository, times(1)).save(argThat(savedContrat ->
				"Employeur".equals(savedContrat.getSignatureEmployeur()) &&
						savedContrat.getDateSignatureEmployeur().equals(LocalDate.now()) &&
						savedContrat.getId().equals(contratId)
		));
	}

	@Test
	public void testSignerContratSuccessEtudiant() {
		Long contratId = 1L;
		UtilisateurDTO etudiant = new UtilisateurDTO();
		etudiant.setId(2L);
		etudiant.setRole(Role.ETUDIANT);
		etudiant.setFullName("Etudiant");

		Contrat contrat = new Contrat();
		contrat.setId(contratId);

		when(contratRepository.findById(contratId)).thenReturn(Optional.of(contrat));

		contratService.signContrat(contratId, etudiant);

		verify(contratRepository, times(1)).save(argThat(savedContrat ->
				"Etudiant".equals(savedContrat.getSignatureEtudiant()) &&
						savedContrat.getDateSignatureEtudiant().equals(LocalDate.now()) &&
						savedContrat.getId().equals(contratId)
		));
	}

	@Test
	public void testSignerContrat_ExceptionContratNotFound() {
		Long contratId = 1L;
		UtilisateurDTO utilisateur = new UtilisateurDTO();
		utilisateur.setId(2L);
		utilisateur.setRole(Role.ETUDIANT);

		when(contratRepository.findById(contratId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> {
			contratService.signContrat(contratId, utilisateur);
		});
	}

	@Test
	public void testSignerContrat_ExceptionIdNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			contratService.signContrat(null, admin);
		});
	}

	@Test
	public void testSignerContrat_ExceptionUtilisateurNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			contratService.signContrat(1L, null);
		});
	}

	@Test
	public void testSignerContrat_ExceptionUtilisateurNotEmployeurOrEtudiant() {
		UtilisateurDTO utilisateur = new UtilisateurDTO();
		utilisateur.setId(2L);
		utilisateur.setRole(Role.ADMIN);

		assertThrows(IllegalArgumentException.class, () -> {
			contratService.signContrat(1L, utilisateur);
		});
	}

	@Test
	public void testGetContratByIdSuccess() {
		Long contratId = 1L;
		ContratDTO contrat = new ContratDTO();
		contrat.setId(contratId);
		contrat.setCandidatureDTO(candidatureDTO);
		contrat.setGestionnaire(admin);


		when(contratRepository.findById(contratId)).thenReturn(Optional.of(ContratDTO.toEntity(contrat)));

		ContratDTO result = contratService.getContratById(contratId);

		assertEquals(contratId, result.getId());
	}

	@Test
	public void testGetContratById_ExceptionIdNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			contratService.getContratById(null);
		});
	}

	@Test
	public void testGetContratById_ExceptionContratNotFound() {
		Long contratId = 1L;
		when(contratRepository.findById(contratId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> {
			contratService.getContratById(contratId);
		});
	}

	@Test
	public void testGetContratByCandidatureIdSuccess() {
		Long candidatureId = 1L;
		ContratSansCvDTO contrat = new ContratSansCvDTO();
		contrat.setId(1L);
		contrat.setCandidatureId(candidatureId);
		contrat.setGestionnaireId(admin.getId());
		contrat.setSignatureGestionnaire("Test User");
		contrat.setDateSignatureGestionnaire(LocalDate.now());
		contrat.setSignatureEmployeur("Non signe");
		contrat.setSignatureEtudiant("Non signe");

		when(contratRepository.findByCandidatureIdWithoutCv(candidatureId)).thenReturn(contrat);

		ContratSansCvDTO result = contratService.getContratByCandidatureId(candidatureId);

		assertEquals(candidatureId, result.getCandidatureId());
	}

	@Test
	public void testGetContratByCandidatureId_ExceptionIdNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			contratService.getContratByCandidatureId(null);
		});
	}

	@Test
	public void testGetContratByCandidatureId_ExceptionContratNotFound() {
		Long candidatureId = 1L;
		when(contratRepository.findByCandidatureIdWithoutCv(candidatureId)).thenReturn(null);

		assertThrows(IllegalArgumentException.class, () -> {
			contratService.getContratByCandidatureId(candidatureId);
		});
	}

	@Test
	public void testSaveContrat_ExceptionCandidatureNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			contratService.getContratByCandidatureId(null);
		});
	}

	@Test
	public void testSaveContrat_ExceptionCandidatureIdNull() {
		ContratDTO contratDTO = new ContratDTO();
		contratDTO.setCandidatureDTO(null);
		contratDTO.setGestionnaire(admin);

		assertThrows(IllegalArgumentException.class, () -> {
			contratService.getContratByCandidatureId(contratDTO.getId());
		});
	}

	@Test
	public void testSaveContrat_ExceptionGestionnaireNull() {
		ContratDTO contratDTO = new ContratDTO();
		contratDTO.setCandidatureDTO(candidatureDTO);
		contratDTO.setGestionnaire(null);

		assertThrows(IllegalArgumentException.class, () -> {
			contratService.getContratByCandidatureId(contratDTO.getId());
		});
	}

	@Test
	public void testSaveContrat_ExceptionContratDTONull() {
		assertThrows(IllegalArgumentException.class, () -> {
			contratService.saveContrat(null);
		});
	}

	@Test
	public void testGenerateContratWithContratSuccess() {
		ContratDTO contratDTO = new ContratDTO();
		contratDTO.setCandidatureDTO(candidatureDTO);
		contratDTO.setGestionnaire(admin);

		Contrat contrat = ContratDTO.toEntity(contratDTO);

		String result = contratService.generateContrat(contratDTO);

		assertEquals("contract_stage1.pdf", result);
	}

	@Test
	public void testGenerateContratWithContrat_ExceptionContratNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			contratService.generateContrat(null);
		});
	}

	@Test
	public void testGenerateContratWithContrat_ExceptionCandidatureNull() {
		ContratDTO contratDTO = new ContratDTO();
		contratDTO.setCandidatureDTO(null);
		contratDTO.setGestionnaire(admin);

		assertThrows(IllegalArgumentException.class, () -> {
			contratService.generateContrat(contratDTO);
		});
	}

	@Test
	public void testGenerateContratWithContrat_ExceptionGestionnaireNull() {
		ContratDTO contratDTO = new ContratDTO();
		contratDTO.setCandidatureDTO(candidatureDTO);
		contratDTO.setGestionnaire(null);

		assertThrows(IllegalArgumentException.class, () -> {
			contratService.generateContrat(contratDTO);
		});
	}

	@Test
	public void testGetContratsSuccess() {
		Contrat contrat1 = new Contrat();
		Contrat contrat2 = new Contrat();
		contrat1.setCandidature(CandidatureDTO.toEntity(candidatureDTO));
		contrat1.setGestionnaire(UtilisateurDTO.toEntity(admin));
		contrat2.setCandidature(CandidatureDTO.toEntity(candidatureDTO));
		contrat2.setGestionnaire(UtilisateurDTO.toEntity(admin));
		List<Contrat> contrats = List.of(contrat1, contrat2);

		when(contratRepository.findAll()).thenReturn(contrats);

		List<ContratDTO> result = contratService.getContrats();

		assertEquals(2, result.size());
	}

	@Test
	public void testGetContrats_ExceptionNoContratsFound() {
		when(contratRepository.findAll()).thenReturn(List.of());

		assertThrows(IllegalArgumentException.class, () -> {
			contratService.getContrats();
		});
	}
}