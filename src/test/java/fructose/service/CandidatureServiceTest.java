package fructose.service;

import fructose.model.*;
import fructose.model.auth.Credentials;
import fructose.model.enumerator.EtatCandidature;
import fructose.model.enumerator.Role;
import fructose.repository.CandidatureRepository;
import fructose.repository.CvRepository;
import fructose.repository.OffreStageRepository;
import fructose.service.dto.CandidatureDTO;
import fructose.service.dto.CvDTO;
import fructose.service.dto.EtudiantDTO;
import fructose.service.dto.OffreStageDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CandidatureServiceTest {

    @InjectMocks
    private CandidatureService candidatureService;

    @Mock
    private CandidatureRepository candidatureRepository;

    @Mock
    private OffreStageRepository offreStageRepository;

    @Mock
    private CvRepository cvRepository;

    private OffreStage offreStage;
    private Candidature candidature;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Departement departement = new Departement();
        departement.setId(1L);
        departement.setNom("Informatique");

        Credentials credentials = Credentials.builder()
                .email("etudiant@example.com")
                .role(Role.ETUDIANT)
                .build();
        Etudiant etudiant = new Etudiant();
        etudiant.setCredentials(credentials);
        etudiant.setDepartement(departement);

        Credentials credentials1 = Credentials.builder()
                .email("employeur@example.com")
                .role(Role.EMPLOYEUR)
                .build();
        Employeur employeur = new Employeur();
        employeur.setCredentials(credentials1);
        employeur.setDepartement(departement);

        offreStage = new OffreStage();
        offreStage.setNom("Stage en Développement");
        offreStage.setCompagnie("TechCorp");
        offreStage.setDepartement(departement);
        offreStage.setOwner(employeur);

        candidature = new Candidature();
        candidature.setId(1L);

        candidature.setEtudiant(etudiant);
        candidature.setOffreStage(offreStage);
        candidature.setEtat(EtatCandidature.EN_ATTENTE);
    }

    @Test
    void testRefuserCandidatureSuccess() {
        when(candidatureRepository.findById(1L)).thenReturn(Optional.of(candidature));

        candidatureService.refuserCandidature(1L, "Manque de compétences");

        verify(candidatureRepository, times(1)).findById(1L);
        verify(candidatureRepository, times(1)).save(candidature);
    }

    @Test
    void testRefuserCandidatureNotFound() {
        when(candidatureRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> candidatureService.refuserCandidature(1L, "Commentaire"));

        verify(candidatureRepository, times(1)).findById(1L);
        verify(candidatureRepository, never()).save(any(Candidature.class));
    }

    @Test
    void testApprouverCandidatureSuccess() {
        when(candidatureRepository.findById(1L)).thenReturn(Optional.of(candidature));

        LocalDate validDate = LocalDate.now().plusDays(4); // Date valide : 4 jours après aujourd'hui

        candidatureService.approuverCandidature(1L, validDate);

        verify(candidatureRepository, times(1)).findById(1L);
        verify(candidatureRepository, times(1)).save(candidature);

        assertEquals(EtatCandidature.ENTREVUE_PROPOSE, candidature.getEtat(), "L'état de la candidature devrait être 'ENTREVUE_PROPOSE'.");
        assertEquals(validDate, candidature.getDateEntrevue(), "La date d'entrevue n'a pas été correctement mise à jour.");
    }


    @Test
    void testApprouverCandidatureNotFound() {
        // Arrange
        long candidatureId = 1L;
        when(candidatureRepository.findById(candidatureId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                candidatureService.approuverCandidature(candidatureId, LocalDate.now().plusDays(4))
        );

        assertEquals("Une erreur est survenue lors de l'approbation de la candidature.", exception.getMessage());

        // Verify
        verify(candidatureRepository, times(1)).findById(candidatureId);
        verify(candidatureRepository, never()).save(any(Candidature.class));
    }

    @Test
    void testApprouverCandidatureInvalidDate() {
        LocalDate invalidDate = LocalDate.now().plusDays(2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                candidatureService.approuverCandidature(1L, invalidDate)
        );

        assertEquals("La date d'entrevue doit être entre 3 jours à partir d'aujourd'hui et dans un mois.", exception.getMessage());
    }

    @Test
    void testApprouverCandidatureRepositoryException() {
        when(candidatureRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        LocalDate validDate = LocalDate.now().plusDays(4); // Valid date: 4 days from today

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                candidatureService.approuverCandidature(1L, validDate)
        );

        assertEquals("Une erreur est survenue lors de l'approbation de la candidature.", exception.getMessage());

        verify(candidatureRepository, times(1)).findById(1L);
        verify(candidatureRepository, never()).save(any(Candidature.class));
    }

    @Test
    void testGetOffreStageDetailsByEtudiantId() {
        Long etudiantId = 1L;
        List<Candidature> candidatures = Collections.singletonList(candidature);

        when(candidatureRepository.findByEtudiantIdWithoutCv(etudiantId)).thenReturn(candidatures);
        when(offreStageRepository.findById(offreStage.getId())).thenReturn(Optional.of(offreStage));

        List<Map<String, Object>> result = candidatureService.getOffreStageDetailsByEtudiantId(etudiantId);

        verify(candidatureRepository, times(1)).findByEtudiantIdWithoutCv(etudiantId);
        verify(offreStageRepository, times(1)).findById(offreStage.getId());

        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("id", candidature.getId());
        expectedData.put("dateEntrevue", candidature.getDateEntrevue());
        expectedData.put("etat", candidature.getEtat());
        expectedData.put("commentaireRefus", candidature.getCommentaireRefus());
        expectedData.put("nomOffre", offreStage.getNom());
        expectedData.put("compagnie", offreStage.getCompagnie());
        expectedData.put("offreStageId", offreStage.getId());

        List<Map<String, Object>> expected = Collections.singletonList(expectedData);
        assertEquals(expected, result, "Le résultat devrait correspondre aux données attendues.");
    }

    @Test
    void testGetCandidaturesByOffreStageIdSuccess() {
        Long employeurId = 1L;

        // Setting up Candidature, CV, Etudiant, and related objects
        Candidature candidature = new Candidature();
        candidature.setId(1L);
        candidature.setEtat(EtatCandidature.EN_ATTENTE);
        candidature.setCommentaireRefus("N/A");

        Credentials studentCredentials = Credentials.builder()
                .email("Mike@gmail.com")
                .password("GG")
                .role(Role.ETUDIANT)
                .build();
        Utilisateur etudiant = new Utilisateur();
        etudiant.setFullName("John Doe");
        etudiant.setCredentials(studentCredentials);
        candidature.setEtudiant(etudiant);

        Credentials employerCredentials = Credentials.builder()
                .email("Blabla@gmail.com")
                .password("GG")
                .role(Role.EMPLOYEUR)
                .build();
        Utilisateur employeur = new Utilisateur();
        employeur.setFullName("John Employer");
        employeur.setCredentials(employerCredentials);

        Departement departement = new Departement();
        departement.setId(1L);
        etudiant.setDepartement(departement);

        Departement departement2 = new Departement();
        departement.setId(2L);
        employeur.setDepartement(departement2);

        Cv cv = new Cv();
        cv.setId(1L);
        cv.setUtilisateur(etudiant);
        cv.setCommentaireRefus("Refusé par Defaut");
        candidature.setCv(cv);

        // Setting up OffreStage and linking to Employeur
        OffreStage offreStage = new OffreStage();
        offreStage.setId(1L);
        offreStage.setNom("Stage en Développement");
        offreStage.setCompagnie("TechCorp");
        offreStage.setDepartement(departement);
        offreStage.setTauxHoraire(15.0);
        offreStage.setTypeEmploi("presentiel");
        offreStage.setAdresse("123 Rue Exemple");
        offreStage.setModaliteTravail("temps_plein");
        offreStage.setDateDebut(LocalDate.of(2023, 1, 1));
        offreStage.setDateFin(LocalDate.of(2023, 6, 30));
        offreStage.setNombreHeuresSemaine(40);
        offreStage.setNombrePostes(2);
        offreStage.setDateLimiteCandidature(LocalDate.of(2022, 12, 31));
        offreStage.setOwner(employeur);
        offreStage.setIsApproved(false);
        offreStage.setIsRefused(false);
        offreStage.setCommentaireRefus("Commentaire par défaut");
        candidature.setOffreStage(offreStage);

        List<Candidature> candidatures = List.of(candidature);

        // Mocking repository methods
        when(candidatureRepository.findByCandidatureByOwnerWithoutCv(employeurId)).thenReturn(candidatures);
        when(candidatureRepository.getCvId(candidature.getId())).thenReturn(cv.getId()); // Mock getCvId
        when(cvRepository.getAllById(cv.getId())).thenReturn(cv); // Mock getAllById

        // Calling the service method
        List<Map<String, Object>> result = candidatureService.findByCandidatureByOwner(employeurId);

        // Verifying repository calls
        verify(candidatureRepository, times(1)).findByCandidatureByOwnerWithoutCv(employeurId);
        verify(candidatureRepository, times(1)).getCvId(candidature.getId()); // Verify getCvId call
        verify(cvRepository, times(1)).getAllById(cv.getId()); // Verify getAllById call

        // Expected output structure
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("candidature", CandidatureDTO.toDTO(candidature));
        expectedData.put("etudiant", EtudiantDTO.toDTO(etudiant));
        expectedData.put("idOffreStage", offreStage.getId());
        expectedData.put("cvId", cv.getId());

        List<Map<String, Object>> expected = List.of(expectedData);

        // Asserting results
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }


    @Test
    void testGetCandidaturesByOffreStageIdNoCandidatures() {
        Long employeurId = 1L;

        // Mock an empty list
        when(candidatureRepository.findByCandidatureByOwnerWithoutCv(employeurId)).thenReturn(Collections.emptyList());

        // Execute service method
        List<Map<String, Object>> result = candidatureService.findByCandidatureByOwner(employeurId);

        // Verify repository interaction
        verify(candidatureRepository, times(1)).findByCandidatureByOwnerWithoutCv(employeurId);

        // Assert the result is an empty list
        assertTrue(result.isEmpty());
    }

    @Test
    void testPostulerSuccess() {
        Candidature candidature = new Candidature();
        candidature.setId(1L);
        candidature.setEtat(EtatCandidature.EN_ATTENTE);
        candidature.setCommentaireRefus("N/A");

        Credentials credentials = Credentials.builder().email("Mike@gmail.com").password("GG").role(Role.ETUDIANT).build();
        Utilisateur etudiant = new Utilisateur();
        etudiant.setFullName("John Doe");
        etudiant.setCredentials(credentials);

        Credentials credentials2 = Credentials.builder().email("Blabla@gmail.com").password("GG").role(Role.EMPLOYEUR).build();
        Utilisateur employeur = new Utilisateur();
        employeur.setFullName("John Doe");
        employeur.setCredentials(credentials2);

        Departement departement = new Departement();
        departement.setId(1L);
        etudiant.setDepartement(departement);
        candidature.setEtudiant(etudiant);

        Departement departements = new Departement();
        departements.setId(2L);
        employeur.setDepartement(departements);

        Cv cv = new Cv();
        cv.setId(1L);
        cv.setUtilisateur(etudiant);
        cv.setCommentaireRefus("Refusé par Defaut");
        candidature.setCv(cv);

        OffreStage offreStage = new OffreStage();
        offreStage.setId(1L);
        offreStage.setNom("Stage en Développement");
        offreStage.setCompagnie("TechCorp");
        offreStage.setDepartement(departement);
        offreStage.setTauxHoraire(15.0);
        offreStage.setTypeEmploi("presentiel");
        offreStage.setAdresse("123 Rue Exemple");
        offreStage.setModaliteTravail("temps_plein");
        offreStage.setDateDebut(LocalDate.of(2023, 1, 1));
        offreStage.setDateFin(LocalDate.of(2023, 6, 30));
        offreStage.setNombreHeuresSemaine(40);
        offreStage.setNombrePostes(2);
        offreStage.setDateLimiteCandidature(LocalDate.of(2022, 12, 31));
        offreStage.setOwner(employeur);
        offreStage.setIsApproved(false);
        offreStage.setIsRefused(false);
        offreStage.setCommentaireRefus("Commentaire par défaut");
        candidature.setOffreStage(offreStage);

        when(offreStageRepository.getReferenceById(offreStage.getId())).thenReturn(offreStage);
        when(candidatureRepository.getCandidatureNumbers(etudiant.getId(), offreStage.getId())).thenReturn(0L);
        when(cvRepository.getReferenceById(1L)).thenReturn(cv);

        candidatureService.postuler(EtudiantDTO.toDTO(etudiant), offreStage.getId(), 1L);

        verify(offreStageRepository, times(1)).getReferenceById(offreStage.getId());
        verify(candidatureRepository, times(1)).getCandidatureNumbers(etudiant.getId(), offreStage.getId());
        verify(cvRepository, times(1)).getReferenceById(1L);
        verify(candidatureRepository, times(1)).save(any(Candidature.class));
    }

    @Test
    void testPostulerCandidatureDejaSoumise() {
        Credentials credentials = Credentials.builder().email("Mike@gmail.com").password("GG").role(Role.ETUDIANT).build();
        Utilisateur etudiant = new Utilisateur();
        etudiant.setFullName("John Doe");
        etudiant.setCredentials(credentials);

        Departement departement = new Departement();
        departement.setId(1L);
        etudiant.setDepartement(departement);

        Cv cv = new Cv();
        cv.setId(1L);
        cv.setUtilisateur(etudiant);

        OffreStage offreStage = new OffreStage();
        offreStage.setId(1L);
        offreStage.setNom("Stage en Développement");
        offreStage.setCompagnie("TechCorp");

        when(offreStageRepository.getReferenceById(offreStage.getId())).thenReturn(offreStage);
        when(candidatureRepository.getCandidatureNumbers(etudiant.getId(), offreStage.getId())).thenReturn(1L);
        when(cvRepository.getReferenceById(1L)).thenReturn(cv);

        assertThrows(IllegalArgumentException.class, () -> candidatureService.postuler(EtudiantDTO.toDTO(etudiant), offreStage.getId(), 1L));

        verify(offreStageRepository, times(1)).getReferenceById(offreStage.getId());
        verify(candidatureRepository, times(1)).getCandidatureNumbers(etudiant.getId(), offreStage.getId());
        verify(cvRepository, times(1)).getReferenceById(1L);
        verify(candidatureRepository, never()).save(any(Candidature.class));
    }

    @Test
    void testModifierEtatCandidatureSuccess() {
        when(candidatureRepository.findById(1L)).thenReturn(Optional.of(candidature));

        candidatureService.modifierEtatCandidature(1L, EtatCandidature.ENTREVUE_PROPOSE);

        verify(candidatureRepository, times(1)).findById(1L);
        verify(candidatureRepository, times(1)).save(candidature);
        assertEquals(EtatCandidature.ENTREVUE_PROPOSE, candidature.getEtat());
    }

    @Test
    void testModifierEtatCandidatureNotFound() {
        when(candidatureRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                candidatureService.modifierEtatCandidature(1L, EtatCandidature.ENTREVUE_PROPOSE)
        );

        verify(candidatureRepository, times(1)).findById(1L);
        verify(candidatureRepository, never()).save(any(Candidature.class));
    }

    @Test
    void testModifierEtatCandidatureRepositoryException() {
        when(candidatureRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () ->
                candidatureService.modifierEtatCandidature(1L, EtatCandidature.ENTREVUE_PROPOSE)
        );

        verify(candidatureRepository, times(1)).findById(1L);
        verify(candidatureRepository, never()).save(any(Candidature.class));
    }

    @Test
    void testGetCandidaturesByEtatAccepteApresEntrevue() {

        List<Candidature> candidatures = List.of(candidature);
        when(candidatureRepository.findByEtatWithoutCv(EtatCandidature.ACCEPTE_APRES_ENTREVUE)).thenReturn(candidatures);

        List<CandidatureDTO> result = candidatureService.getCandidaturesByEtatAccepteApresEntrevue();

        assertEquals(1, result.size());
        assertEquals(candidature.getId(), result.getFirst().getId());
    }

    @Test
    void testGetCandidaturesByEtatAccepteApresEntrevueException() {
        when(candidatureRepository.findByEtatWithoutCv(EtatCandidature.ACCEPTE_APRES_ENTREVUE)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                candidatureService.getCandidaturesByEtatAccepteApresEntrevue()
        );

        assertEquals("Une erreur est survenue lors de la récupération des candidatures par état accepté après entrevue.", exception.getMessage());
    }

    @Test
    void testGetCandidatureByIdSuccess() {
        System.out.println(candidature);
        when(candidatureRepository.findById(1L)).thenReturn(Optional.of(candidature));

        CandidatureDTO result = candidatureService.getCandidatureById(1L);

        assertEquals(candidature.getId(), result.getId());
    }

    @Test
    void testGetCandidatureByIdNotFound() {
        when(candidatureRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                candidatureService.getCandidatureById(1L)
        );

        assertEquals("Candidature avec ID: 1 n'existe pas", exception.getMessage());
    }
    @Test
    void testFindStagiaireByOwner() {
        Long ownerId = 1L;

        Candidature candidature = new Candidature();
        candidature.setId(1L);
        candidature.setEtat(EtatCandidature.POSTE_OBTENU);

        Credentials studentCredentials = Credentials.builder()
                .email("student@example.com")
                .password("password")
                .role(Role.ETUDIANT)
                .build();
        Utilisateur etudiant = new Utilisateur();
        etudiant.setFullName("John Doe");
        etudiant.setCredentials(studentCredentials);

        Departement departement = new Departement();
        departement.setId(1L);
        etudiant.setDepartement(departement);

        candidature.setEtudiant(etudiant);

        Cv cv = new Cv();
        cv.setId(1L);
        cv.setUtilisateur(etudiant);
        candidature.setCv(cv);

        Credentials credentials2 = Credentials.builder().email("Blabla@gmail.com").password("GG").role(Role.EMPLOYEUR).build();
        Utilisateur employeur = new Utilisateur();
        employeur.setFullName("John Doe");
        employeur.setCredentials(credentials2);

        OffreStage offreStage = new OffreStage();
        offreStage.setId(1L);
        offreStage.setNom("Stage en Développement");
        offreStage.setCompagnie("TechCorp");
        offreStage.setDepartement(departement);
        offreStage.setTauxHoraire(15.0);
        offreStage.setTypeEmploi("presentiel");
        offreStage.setAdresse("123 Rue Exemple");
        offreStage.setModaliteTravail("temps_plein");
        offreStage.setDateDebut(LocalDate.of(2023, 1, 1));
        offreStage.setDateFin(LocalDate.of(2023, 6, 30));
        offreStage.setNombreHeuresSemaine(40);
        offreStage.setNombrePostes(2);
        offreStage.setDateLimiteCandidature(LocalDate.of(2022, 12, 31));
        offreStage.setOwner(employeur);
        offreStage.setIsApproved(false);
        offreStage.setIsRefused(false);
        offreStage.setCommentaireRefus("Commentaire par défaut");
        candidature.setOffreStage(offreStage);

        List<Candidature> candidatures = List.of(candidature);

        // Mocking repository methods
        when(candidatureRepository.findStagiaireByOwner(ownerId, EtatCandidature.POSTE_OBTENU)).thenReturn(candidatures);
        when(candidatureRepository.getCvId(candidature.getId())).thenReturn(cv.getId());
        when(cvRepository.getAllById(cv.getId())).thenReturn(cv);

        // Calling the service method
        List<Map<String, Object>> result = candidatureService.findStagiaireByOwner(ownerId);

        // Verifying repository calls
        verify(candidatureRepository, times(1)).findStagiaireByOwner(ownerId, EtatCandidature.POSTE_OBTENU);
        verify(candidatureRepository, times(1)).getCvId(candidature.getId());
        verify(cvRepository, times(1)).getAllById(cv.getId());

        // Expected output structure
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("candidature", CandidatureDTO.toDTO(candidature));
        expectedData.put("etudiant", EtudiantDTO.toDTO(etudiant));
        expectedData.put("idOffreStage", offreStage.getId());
        expectedData.put("cvId", cv.getId());

        List<Map<String, Object>> expected = List.of(expectedData);

        // Asserting results
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
}