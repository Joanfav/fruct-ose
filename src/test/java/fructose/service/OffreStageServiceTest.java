package fructose.service;

import fructose.model.*;
import fructose.model.auth.Credentials;
import fructose.model.enumerator.Role;
import fructose.repository.DepartementRepository;
import fructose.repository.EmployeurRepository;
import fructose.repository.OffreStageRepository;
import fructose.service.dto.DepartementDTO;
import fructose.service.dto.EmployeurDTO;
import fructose.service.dto.OffreStageDTO;
import fructose.service.dto.UtilisateurDTO;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OffreStageServiceTest {
	
	@InjectMocks
	private OffreStageService offreStageService;
	
	@Mock
	private OffreStageRepository offreStageRepository;
	
	@Mock
	private EmployeurRepository employeurRepository;
	
	@Mock
	private OffreStageDTO offreStageDTO;
	
	@Mock
	private DepartementRepository departementRepository;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		offreStageDTO = new OffreStageDTO();
		offreStageDTO.setId(1L);
		offreStageDTO.setNom("Google");
		offreStageDTO.setPoste("Developpeur Java");
		offreStageDTO.setDescription("Faire du developpement Java chez Google");
		offreStageDTO.setCompagnie("Google");
		DepartementDTO departement = new DepartementDTO();
		departement.setNom("gestion_commerce");
		departement.setId(1L);
		offreStageDTO.setDepartementDTO(departement);
		offreStageDTO.setTauxHoraire(23.75);
		offreStageDTO.setAdresse("1600 Amphitheatre Parkway, Mountain View, CA 94043, Etats-Unis");
		offreStageDTO.setTypeEmploi("presentiel");
		offreStageDTO.setModaliteTravail("temps_plein");
		offreStageDTO.setDateDebut(LocalDate.now().plusMonths(1));
		offreStageDTO.setDateFin(LocalDate.now().plusMonths(6));
		offreStageDTO.setNombreHeuresSemaine(40);
		offreStageDTO.setNombrePostes(5);
		offreStageDTO.setDateLimiteCandidature(LocalDate.now().plusDays(14));
		EmployeurDTO employeurDTO = new EmployeurDTO();
		employeurDTO.setRole(Role.EMPLOYEUR);
		employeurDTO.setEmail("Mike");
		employeurDTO.setId(1L);
		employeurDTO.setDepartementDTO(departement);
		offreStageDTO.setOwnerDTO(employeurDTO);
		
		Authentication authentication = mock(Authentication.class);
		when(authentication.isAuthenticated()).thenReturn(true);
		when(authentication.getName()).thenReturn("Mike");
		Credentials credentials = mock(Credentials.class);
		when(credentials.getRole()).thenReturn(Role.EMPLOYEUR);
		when(credentials.getEmail()).thenReturn("Mike");
		when(authentication.getCredentials()).thenReturn(credentials);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		Utilisateur utilisateur = new Employeur();
		utilisateur.setCredentials(Credentials.builder().email("Mike").password("GG").role(Role.EMPLOYEUR).build());
		utilisateur.setDepartement(DepartementDTO.toEntity(departement));
		when(employeurRepository.findByEmail("Mike")).thenReturn(utilisateur);
	}
	
	
	@Test
	void testAddOffreStageSuccess() {
		offreStageService.addOffreStage(offreStageDTO);
		
		verify(offreStageRepository, times(1)).save(any(OffreStage.class));
	}

	@Test
	void testAddOffreStageOwnerDTONull() {
		offreStageDTO.setOwnerDTO(null);
		UtilisateurDTO utilisateurDTO = UtilisateurDTO.toDTO(offreStageService.getUtilisateurEnCours());

		offreStageService.addOffreStage(offreStageDTO, utilisateurDTO);

		assertNotNull(offreStageDTO.getOwnerDTO());
		assertEquals(utilisateurDTO, offreStageDTO.getOwnerDTO());
	}
	
	@Test
	void testAddOffreStageNomNull() {
		offreStageDTO.setNom(null);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("nom: Le nom ne peut pas être vide", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageNomVide() {
		offreStageDTO.setNom("");
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		String errorMessage = exception.getMessage();
		assertTrue(errorMessage.contains("Le nom ne peut pas être vide") ||
			errorMessage.contains("Le nom doit contenir entre 3 et 100 caractères"));
	}
	
	@Test
	void testAddOffreStageNomTropCourt() {
		offreStageDTO.setNom("AB");
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("nom: Le nom doit contenir entre 3 et 100 caractères", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageNomTropLong() {
		offreStageDTO.setNom("Lorem ipsum dolor sit amet consectetur adipiscing elit Sed non risus Suspendisse lectus tortor dignissim sit amet adipiscing nec ultricies sed dolor Cras elementum ultrices diam Maecenas ligula massa varius a semper congue euismod non mi");
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("nom: Le nom doit contenir entre 3 et 100 caractères", exception.getMessage());
	}
	
	@Test
	void testAddOffreStagePosteNull() {
		offreStageDTO.setPoste(null);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("poste: Le poste ne peut pas être vide", exception.getMessage());
	}
	
	@Test
	void testAddOffreStagePosteVide() {
		offreStageDTO.setPoste("");
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		for (String errorMessage : exception.getMessage().split(", ")) {
			assertTrue(errorMessage.equals("poste: Le poste ne peut pas être vide") ||
				errorMessage.equals("poste: Le poste doit contenir entre 3 et 100 caractères"));
		}
	}
	
	@Test
	void testAddOffreStagePosteTropCourt() {
		offreStageDTO.setPoste("AB");
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("poste: Le poste doit contenir entre 3 et 100 caractères", exception.getMessage());
	}
	
	@Test
	void testAddOffreStagePosteTropLong() {
		String randomString = new Random().ints(101, 0, 52)
			.mapToObj(i -> "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt(i))
			.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
			.toString();
		offreStageDTO.setPoste(randomString);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("poste: Le poste doit contenir entre 3 et 100 caractères", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageDescriptionNull() {
		offreStageDTO.setDescription(null);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("description: La description ne peut pas être vide", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageDescriptionVide() {
		offreStageDTO.setDescription("");
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		String errorMessage = exception.getMessage();
		assertTrue(errorMessage.contains("description: La description ne peut pas être vide") ||
			errorMessage.contains("description: La description doit contenir entre 10 et 500 caractères"));
	}
	
	@Test
	void testAddOffreStageDescriptionTropCourt() {
		offreStageDTO.setDescription("AB");
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("description: La description doit contenir entre 10 et 500 caractères", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageDescriptionTropLong() {
		String randomString = new Random().ints(501, 0, 62)
			.mapToObj(i -> "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(i))
			.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
			.toString();
		offreStageDTO.setDescription(randomString);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("description: La description doit contenir entre 10 et 500 caractères", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageCompagnieNull() {
		offreStageDTO.setCompagnie(null);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("compagnie: Le nom de la compagnie ne peut pas être vide", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageCompagnieVide() {
		offreStageDTO.setCompagnie("");
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		for (String errorMessage : exception.getMessage().split(", ")) {
			assertTrue(errorMessage.equals("compagnie: Le nom de la compagnie ne peut pas être vide") ||
				errorMessage.equals("compagnie: Le nom de la compagnie doit contenir entre 3 et 100 caractères"));
		}
	}
	
	@Test
	void testAddOffreStageCompagnieTropCourt() {
		offreStageDTO.setCompagnie("AB");
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("compagnie: Le nom de la compagnie doit contenir entre 3 et 100 caractères", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageCompagnieTropLong() {
		String randomString = new Random().ints(101, 0, 62)
			.mapToObj("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"::charAt)
			.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
			.toString();
		offreStageDTO.setCompagnie(randomString);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("compagnie: Le nom de la compagnie doit contenir entre 3 et 100 caractères", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageProgrammeEtudeNull() {
		offreStageDTO.setDepartementDTO(null);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("departementDTO: Le département ne peut pas être null", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageTauxHoraireNull() {
		offreStageDTO.setTauxHoraire(null);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("tauxHoraire: Le taux horaire ne peut pas être null", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageTauxHoraireInvalide() {
		offreStageDTO.setTauxHoraire(-1.0);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("tauxHoraire: Le taux horaire ne peut pas être négatif", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageTypeEmploiNull() {
		offreStageDTO.setTypeEmploi(null);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("typeEmploi: Le type d'emploi ne peut pas être null", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageTypeEmploiInvalid() {
		offreStageDTO.setTypeEmploi("Pina Colada");
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("typeEmploi: Le type d'emploi doit être l'un des suivants : Presentiel, Virtuel, Hybride", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageAdresseNull() {
		offreStageDTO.setAdresse(null);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("adresse: L'adresse ne peut pas être vide", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageAdresseVide() {
		offreStageDTO.setAdresse("");
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		for (String errorMessage : exception.getMessage().split(", ")) {
			assertTrue(errorMessage.equals("adresse: L'adresse ne peut pas être vide") ||
				errorMessage.equals("adresse: L'adresse doit contenir entre 3 et 100 caractères"));
		}
	}
	
	@Test
	void testAddOffreStageAdresseTropCourt() {
		offreStageDTO.setAdresse("AB");
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("adresse: L'adresse doit contenir entre 3 et 100 caractères", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageAdresseTropLong() {
		String randomString = new Random().ints(101, 0, 62)
			.mapToObj(i -> "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(i))
			.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
			.toString();
		offreStageDTO.setAdresse(randomString);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("adresse: L'adresse doit contenir entre 3 et 100 caractères", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageModaliteTravailNull() {
		offreStageDTO.setModaliteTravail(null);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("modaliteTravail: La modalité de travail ne peut pas être vide", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageModaliteTravailInvalide() {
		offreStageDTO.setModaliteTravail("Pina Colada");
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("modaliteTravail: La modalité de travail doit être l'une des suivantes : Temps plein, Temps partiel", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageDateDebutNull() {
		offreStageDTO.setDateDebut(null);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("dateDebut: La date de début ne peut pas être null", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageDateDebutPassee() {
		offreStageDTO.setDateDebut(LocalDate.of(2020, 5, 1));
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("La date de début doit être au moins 1 jour après la date limite de candidature", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageDateFinNull() {
		offreStageDTO.setDateFin(null);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("dateFin: La date de fin ne peut pas être null", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageDateDebutInferieureADateLimiteCandidaturePlusUnJour() {
		offreStageDTO.setDateDebut(LocalDate.now());
		offreStageDTO.setDateFin(LocalDate.now());
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("La date de début doit être au moins 1 jour après la date limite de candidature", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageDateFinInferieureADateDebutPlusUnJour() {
		offreStageDTO.setDateLimiteCandidature(LocalDate.now().plusDays(7));
		offreStageDTO.setDateDebut(LocalDate.now().plusDays(8));
		offreStageDTO.setDateFin(LocalDate.now().plusDays(8));
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("La date de fin doit être au moins 1 jour après la date de début", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageNombreHeuresSemaineTropPetit() {
		offreStageDTO.setNombreHeuresSemaine(0);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("nombreHeuresSemaine: Le nombre d'heures par semaine ne peut pas être inferieur a 1", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageNombreHeuresSemaineTropGrand() {
		offreStageDTO.setNombreHeuresSemaine(41);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("nombreHeuresSemaine: Le nombre d'heures par semaine ne peut pas être superieur a 40", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageNombrePostesTropPetit() {
		offreStageDTO.setNombrePostes(0);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("nombrePostes: Le nombre de postes ne peut pas être inferieur a 1", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageDateLimiteCandidatureNull() {
		offreStageDTO.setDateLimiteCandidature(null);
		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("dateLimiteCandidature: La date limite de candidature ne peut pas être null", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageDateLimiteCandidatureMemeJourDateDebut() {
		offreStageDTO.setDateDebut(LocalDate.now());
		offreStageDTO.setDateLimiteCandidature(LocalDate.now());
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.addOffreStage(offreStageDTO);
		});
		assertEquals("La date limite de candidature doit être au moins 7 jours après aujourd'hui", exception.getMessage());
	}
	
	@Test
	void testAddOffreStageNull() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.addOffreStage(null);
		});
		
		assertEquals("OffreStageDTO ne peut pas être nul", exception.getMessage());
	}


	
	@Test
	void testDeleteOffreStageSuccess() {
		OffreStage offreStage = OffreStageDTO.toEntity(offreStageDTO);
		when(offreStageRepository.existsById(offreStageDTO.getId())).thenReturn(true);
		when(offreStageRepository.findById(offreStageDTO.getId())).thenReturn(Optional.of(offreStage));
		
		offreStageService.deleteOffreStage(offreStageDTO.getId());
		
		verify(offreStageRepository, times(1)).deleteById(offreStageDTO.getId());
	}
	
	@Test
	void testDeleteOffreStageNotFound() {
		when(offreStageRepository.existsById(offreStageDTO.getId())).thenReturn(false);
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.deleteOffreStage(offreStageDTO.getId());
		});
		
		assertEquals("L'offre stage avec l'ID: " + offreStageDTO.getId() + " n'existe pas, alors il ne peut pas être supprimé", exception.getMessage());
	}
	
	@Test
	void testDeleteOffreStageIdNull() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.deleteOffreStage(null);
		});
		
		assertEquals("ID ne peut pas être nul", exception.getMessage());
	}
	
	@Test
	void testGetOffreStageByIdSuccess() {
		OffreStage offreStage = OffreStageDTO.toEntity(offreStageDTO);
		when(offreStageRepository.findById(offreStageDTO.getId())).thenReturn(Optional.of(offreStage));
		
		OffreStageDTO result = offreStageService.getOffreStageById(offreStageDTO.getId());
		assertNotNull(result);
	}
	
	@Test
	void testGetOffreStageByIdNotFound() {
		when(offreStageRepository.findById(offreStageDTO.getId())).thenReturn(Optional.empty());
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.getOffreStageById(offreStageDTO.getId());
		});
		
		assertEquals("L'offre stage avec l'ID: " + offreStageDTO.getId() + " n'existe pas, alors il ne peut pas être récupéré", exception.getMessage());
	}
	
	@Test
	void testGetOffreStageByIdIdNull() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.getOffreStageById(null);
		});
		
		assertEquals("ID ne peut pas être nul", exception.getMessage());
	}
	
	@Test
	void testUpdateOffreStageSuccess() {
		OffreStage offreStage = OffreStageDTO.toEntity(offreStageDTO);
		Employeur employeur = new Employeur();
		when(offreStageRepository.findById(offreStageDTO.getId())).thenReturn(Optional.of(offreStage));
		when(offreStageRepository.existsById(offreStageDTO.getId())).thenReturn(true);
		when(departementRepository.findById(offreStageDTO.getDepartementDTO().getId())).thenReturn(Optional.ofNullable(DepartementDTO.toEntity(offreStageDTO.getDepartementDTO())));
		when(employeurRepository.findById(offreStageDTO.getOwnerDTO().getId())).thenReturn(Optional.of(employeur));
		offreStageDTO.setNom("Microsoft");
		offreStageService.updateOffreStage(offreStageDTO);
		
		verify(offreStageRepository, times(1)).save(argThat(savedOffreStage ->
			savedOffreStage.getNom().equals("Microsoft")));
	}
	
	@Test
	void testUpdateOffreStageIdNull() {
		offreStageDTO.setId(null);
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.updateOffreStage(offreStageDTO);
		});
		
		assertEquals("L'offre de stage avec l'ID: null n'existe pas, alors il ne peut pas être mis à jour", exception.getMessage());
	}
	
	@Test
	void testUpdateOffreStageNotFound() {
		when(offreStageRepository.findById(offreStageDTO.getId())).thenReturn(Optional.empty());
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.updateOffreStage(offreStageDTO);
		});
		
		assertEquals("L'offre de stage avec l'ID: " + offreStageDTO.getId() + " n'existe pas, alors il ne peut pas être mis à jour", exception.getMessage());
	}
	
	@Test
	void testUpdateOffreStageNull() {
		when(offreStageRepository.findById(offreStageDTO.getId())).thenReturn(Optional.of(OffreStageDTO.toEntity(offreStageDTO)));
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.updateOffreStage(null);
		});
		
		assertEquals("OffreStageDTO ne peut pas être nul", exception.getMessage());
	}
	
	@Test
	void testUpdateOffreStageIdDoNotExist() {
		when(offreStageRepository.existsById(offreStageDTO.getId())).thenReturn(false);
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.updateOffreStage(offreStageDTO);
		});
		
		assertEquals("L'offre de stage avec l'ID: " + offreStageDTO.getId() + " n'existe pas, alors il ne peut pas être mis à jour", exception.getMessage());
	}
	
	@Test
	void testGetOffresStageNotFoundForEmployeur() {
		when(offreStageRepository.getAllByOwnerId(1L)).thenReturn(List.of());
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.getOffresStage();
		});
		
		assertEquals("Aucune offre de stage trouvée pour l'employeur avec l'email: Mike", exception.getMessage());
	}
	
	@Test
	void testGetOffresStageSuccessForAdmin() {
		List<OffreStageDTO> offresStage = List.of(offreStageDTO);
		List<OffreStage> offreStages = offresStage.stream()
			.map(OffreStageDTO::toEntity)
			.toList();
		Utilisateur utilisateur = new Employeur();
		utilisateur.setCredentials(Credentials.builder().email("Mike").password("GG").role(Role.ADMIN).build());
		when(employeurRepository.findByEmail("Mike")).thenReturn(utilisateur);
		when(offreStageRepository.findAll()).thenReturn(offreStages);
		
		offreStageService.getOffresStage();
		
		verify(offreStageRepository, times(1)).findAll();
	}
	
	@Test
	void testGetOffresStageNotFoundForAdmin() {
		Utilisateur utilisateur = new Employeur();
		utilisateur.setCredentials(Credentials.builder().email("Mike").password("GG").role(Role.ADMIN).build());
		when(employeurRepository.findByEmail("Mike")).thenReturn(utilisateur);
		when(offreStageRepository.findAll()).thenReturn(List.of());
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.getOffresStage();
		});
		
		assertEquals("Aucune offre stage a été créer pour l'instant", exception.getMessage());
	}
	
	@Test
	void testGetOffresStageSuccesForEmployeur() {
		List<OffreStageDTO> offresStage = List.of(offreStageDTO);
		List<OffreStage> offreStages = offresStage.stream()
			.map(OffreStageDTO::toEntity)
			.toList();
		Utilisateur utilisateur = new Employeur();
		utilisateur.setDepartement(new Departement());
		utilisateur.setCredentials(Credentials.builder().email("Mike").password("GG").role(Role.EMPLOYEUR).build());
		when(employeurRepository.findByEmail("Mike")).thenReturn(utilisateur);
		when(offreStageRepository.getAllByOwnerId(utilisateur.getDepartement().getId())).thenReturn(offreStages);
		
		offreStageService.getOffresStage();
		
		verify(offreStageRepository, times(1)).getAllByOwnerId(utilisateur.getDepartement().getId());
	}
	
	@Test
	void testGetOffresStageNotFoundForEtudiant() {
		Utilisateur utilisateur = new Employeur();
		utilisateur.setDepartement(new Departement());
		utilisateur.setCredentials(Credentials.builder().email("Mike").password("GG").role(Role.ETUDIANT).build());
		when(employeurRepository.findByEmail("Mike")).thenReturn(utilisateur);
		when(offreStageRepository.findByUserDepartement(utilisateur.getDepartement().getId())).thenReturn(List.of());
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.getOffresStage();
		});
		
		assertEquals("Aucune offre de stage trouvée pour l'étudiant dans le département: " + utilisateur.getDepartement(), exception.getMessage());
	}

	@Test
	void testGetOffresStageSuccessForProfesseur() {
		List<OffreStageDTO> offresStage = List.of(offreStageDTO);
		List<OffreStage> offreStages = offresStage.stream()
				.map(OffreStageDTO::toEntity)
				.toList();
		Utilisateur utilisateur = new Professeur();
		utilisateur.setDepartement(new Departement());
		utilisateur.setCredentials(Credentials.builder().email("Mike").password("GG").role(Role.PROFESSEUR).build());
		when(employeurRepository.findByEmail("Mike")).thenReturn(utilisateur);
		when(offreStageRepository.findByUserDepartementAndIsApproved(utilisateur.getDepartement().getId())).thenReturn(offreStages);

		offreStageService.getOffresStage();

		verify(offreStageRepository, times(1)).findByUserDepartementAndIsApproved(utilisateur.getDepartement().getId());
	}
	
	@Test
	void testGetOffresStageNull() {
		Utilisateur utilisateur = new Employeur();
		utilisateur.setCredentials(Credentials.builder().email("Mike").password("GG").role(null).build());
		when(employeurRepository.findByEmail("Mike")).thenReturn(utilisateur);
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> offreStageService.getOffresStage());
		
		assertEquals("Le role de l'utilisateur ne peut pas être nul", exception.getMessage());
	}
	
	@Test
	void testGetUtilisateurEnCoursSuccess() {
		Utilisateur utilisateur = new Employeur();
		utilisateur.setCredentials(Credentials.builder().email("Mike").password("GG").role(Role.EMPLOYEUR).build());
		when(employeurRepository.findByEmail("Mike")).thenReturn(utilisateur);
		
		Utilisateur result = offreStageService.getUtilisateurEnCours();
		assertNotNull(result);
	}
	
	@Test
	void testGetUtilisateurEnCoursNotFound() {
		Authentication authentication = mock(Authentication.class);
		when(authentication.isAuthenticated()).thenReturn(false);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			offreStageService.getUtilisateurEnCours();
		});
		
		assertEquals("Aucun utilisateur n'est connecté", exception.getMessage());
	}
	
	@Test
	void testRefuserOffreStageSuccess() {
		Long offreId = 1L;
		String commentaireRefus = "Not suitable";
		
		OffreStage offreStage = new OffreStage();
		when(offreStageRepository.findById(offreId)).thenReturn(Optional.of(offreStage));
		
		offreStageService.refuserOffreStage(offreId, commentaireRefus);
		
		verify(offreStageRepository, times(1)).save(offreStage);
		assert offreStage.getIsRefused();
		assert offreStage.getCommentaireRefus().equals(commentaireRefus);
	}
	
	@Test
	void testRefuserOffreStageNotFound() {
		Long offreId = 1L;
		String commentaireRefus = "Not suitable";
		
		when(offreStageRepository.findById(offreId)).thenReturn(Optional.empty());
		
		assertThrows(RuntimeException.class, () -> {
			offreStageService.refuserOffreStage(offreId, commentaireRefus);
		});
		
		verify(offreStageRepository, never()).save(any(OffreStage.class));
	}
	
	@Test
	void testRefuserOffreStageException() {
		Long offreId = 1L;
		String commentaireRefus = "Not suitable";
		
		when(offreStageRepository.findById(offreId)).thenThrow(new RuntimeException("Database error"));
		
		assertThrows(RuntimeException.class, () -> {
			offreStageService.refuserOffreStage(offreId, commentaireRefus);
		});
		
		verify(offreStageRepository, never()).save(any(OffreStage.class));
	}

	@Test
	void testAccepterOffreStageSuccess() {
		Long offreId = 1L;
		OffreStage offreStage = new OffreStage();
		when(offreStageRepository.findById(offreId)).thenReturn(Optional.of(offreStage));

		offreStageService.accepterOffreStage(offreId);

		verify(offreStageRepository, times(1)).save(offreStage);
		assertTrue(offreStage.getIsApproved());
	}

	@Test
	void testAccepterOffreStageNotFound() {
		Long offreId = 1L;
		when(offreStageRepository.findById(offreId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(RuntimeException.class, () -> {
			offreStageService.accepterOffreStage(offreId);
		});

		assertEquals("Une erreur est survenue lors du refus de l'offre de stage.", exception.getMessage());
		verify(offreStageRepository, never()).save(any(OffreStage.class));
	}

	@Test
	void testAccepterOffreStageException() {
		Long offreId = 1L;
		when(offreStageRepository.findById(offreId)).thenThrow(new RuntimeException("Database error"));

		Exception exception = assertThrows(RuntimeException.class, () -> {
			offreStageService.accepterOffreStage(offreId);
		});

		assertEquals("Une erreur est survenue lors du refus de l'offre de stage.", exception.getMessage());
		verify(offreStageRepository, never()).save(any(OffreStage.class));
	}
}