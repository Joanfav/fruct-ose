package fructose.controller;

import fructose.service.EvaluationService;
import fructose.service.UtilisateurService;
import fructose.service.dto.EvaluationEmployeurDTO;
import fructose.service.dto.EvaluationMilieuStageDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvaluationControllerTest {

    @Mock
    private EvaluationService evaluationService;

    @Mock
    UtilisateurService utilisateurService;

    @InjectMocks
    private EvaluationController evaluationController;

    public EvaluationControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void creerEvaluation_success() throws Exception {
        EvaluationEmployeurDTO evaluationDTO = new EvaluationEmployeurDTO();

        File tempFile = File.createTempFile("evaluation_stagiaire", ".pdf");
        tempFile.deleteOnExit();

        when(evaluationService.creerEvaluationEmployeur(evaluationDTO)).thenReturn(tempFile.getAbsolutePath());

        ResponseEntity<byte[]> response = evaluationController.creerEvaluation(evaluationDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
        verify(evaluationService, times(1)).creerEvaluationEmployeur(evaluationDTO);
    }


    @Test
    void creerEvaluation_fileNotFound() {
        EvaluationEmployeurDTO evaluationDTO = new EvaluationEmployeurDTO();
        String invalidPath = "invalid/path/evaluation_stagiaire.pdf";
        when(evaluationService.creerEvaluationEmployeur(evaluationDTO)).thenReturn(invalidPath);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> evaluationController.creerEvaluation(evaluationDTO));
        assertEquals(500, exception.getStatusCode().value());
        verify(evaluationService, times(1)).creerEvaluationEmployeur(evaluationDTO);
    }

    @Test
    void recupererEvaluationEleve_success() throws Exception {
        EvaluationEmployeurDTO evaluationDTO = new EvaluationEmployeurDTO();

        File tempFile = File.createTempFile("evaluation_stagiaire", ".pdf");
        tempFile.deleteOnExit();

        when(evaluationService.recupererEvaluationEmployeurParId(evaluationDTO.getId())).thenReturn(tempFile.getAbsolutePath());

        ResponseEntity<byte[]> response = evaluationController.recupererEvaluationEleve(evaluationDTO.getId());

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
        verify(evaluationService, times(1)).recupererEvaluationEmployeurParId(evaluationDTO.getId());
    }


    @Test
    void findAllEvaluation_success() {
        List<EvaluationEmployeurDTO> mockEvaluations = Arrays.asList(
                new EvaluationEmployeurDTO(),
                new EvaluationEmployeurDTO()
        );
        when(evaluationService.findAllEvaluationEmployeur()).thenReturn(mockEvaluations);

        ResponseEntity<?> response = evaluationController.findAllEvaluation();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof List);
        verify(evaluationService, times(1)).findAllEvaluationEmployeur();
    }

    @Test
    void findAllEvaluation_failure() {
        when(evaluationService.findAllEvaluationEmployeur()).thenThrow(new RuntimeException("Database error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> evaluationController.findAllEvaluation());
        assertEquals(500, exception.getStatusCode().value());
        verify(evaluationService, times(1)).findAllEvaluationEmployeur();
    }

    @Test
    void recupererEvaluationEleve_fileNotFound() throws Exception {
        // Arrange
        EvaluationEmployeurDTO evaluationDTO = new EvaluationEmployeurDTO();
        String nonExistentPdfPath = "mock/path/nonexistent.pdf";

        // Simuler que le service retourne un chemin vers un fichier inexistant
        when(evaluationService.recupererEvaluationEmployeurParId(evaluationDTO.getId())).thenReturn(nonExistentPdfPath);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            evaluationController.recupererEvaluationEleve(evaluationDTO.getId());
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Error generating evaluation PDF", exception.getReason());
        verify(evaluationService, times(1)).recupererEvaluationEmployeurParId(evaluationDTO.getId());
    }

    @Test
    void creerEvaluationMilieuStage_success() throws Exception {
        EvaluationMilieuStageDTO evaluationDTO = new EvaluationMilieuStageDTO();

        File tempFile = File.createTempFile("evaluation_stagiaire", ".pdf");
        tempFile.deleteOnExit();

        when(evaluationService.creerEvaluationMilieuStage(evaluationDTO)).thenReturn(tempFile.getAbsolutePath());
        when(utilisateurService.validationToken("valid-token")).thenReturn(true);

        ResponseEntity<byte[]> response = evaluationController.creerEvaluationMilieuStage("valid-token", evaluationDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
        verify(evaluationService, times(1)).creerEvaluationMilieuStage(evaluationDTO);
    }

    @Test
    void recupererEvaluationMilieuStage_success() throws Exception {
        EvaluationMilieuStageDTO evaluationDTO = new EvaluationMilieuStageDTO();

        File tempFile = File.createTempFile("evaluation_stagiaire", ".pdf");
        tempFile.deleteOnExit();

        when(evaluationService.recupererEvaluationMilieuStageParId(evaluationDTO.getId())).thenReturn(tempFile.getAbsolutePath());

        ResponseEntity<byte[]> response = evaluationController.recupererEvaluationMilieuStage(evaluationDTO.getId());

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
        verify(evaluationService, times(1)).recupererEvaluationMilieuStageParId(evaluationDTO.getId());
    }

    @Test
    void findAllEvaluationMilieuStage_success() {
        List<EvaluationMilieuStageDTO> mockEvaluations = Arrays.asList(
                new EvaluationMilieuStageDTO(),
                new EvaluationMilieuStageDTO()
        );
        when(evaluationService.findAllEvaluationMilieuStage()).thenReturn(mockEvaluations);

        ResponseEntity<?> response = evaluationController.findAllEvaluationMilieuStage();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof List);
        verify(evaluationService, times(1)).findAllEvaluationMilieuStage();
    }

    @Test
    void findAllEvaluationMilieuStage_failure() {
        when(evaluationService.findAllEvaluationMilieuStage()).thenThrow(new RuntimeException("Database error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> evaluationController.findAllEvaluationMilieuStage());
        assertEquals(500, exception.getStatusCode().value());
        verify(evaluationService, times(1)).findAllEvaluationMilieuStage();
    }

}
