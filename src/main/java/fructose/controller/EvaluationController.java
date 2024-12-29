package fructose.controller;

import fructose.service.EvaluationService;
import fructose.service.UtilisateurService;
import fructose.service.dto.EvaluationEmployeurDTO;
import fructose.service.dto.EvaluationMilieuStageDTO;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final UtilisateurService utilisateurService;

    private void validateToken(String token) {
        if (!utilisateurService.validationToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    public EvaluationController(EvaluationService evaluationService, UtilisateurService utilisateurService) {
        this.evaluationService = evaluationService;
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/employeur/creer")
    public ResponseEntity<byte[]> creerEvaluation(@RequestBody EvaluationEmployeurDTO evaluationDTO){
        try {
            String pdfPath = evaluationService.creerEvaluationEmployeur(evaluationDTO);
            return getResponseEntity(pdfPath);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating evaluation PDF", e);
        }
    }

    @GetMapping("/employeur/{id}")
    public ResponseEntity<byte[]> recupererEvaluationEleve(@PathVariable Long id){
        try {
            String pdfPath = evaluationService.recupererEvaluationEmployeurParId(id);
            return getResponseEntity(pdfPath);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating evaluation PDF", e);
        }
    }

    @GetMapping("/employeur/all-evaluation")
    public ResponseEntity<?> findAllEvaluation(){
        try {
            List<EvaluationEmployeurDTO> evaluationEmployeurDTOS = evaluationService.findAllEvaluationEmployeur();
            return new ResponseEntity<>(evaluationEmployeurDTOS, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating evaluation PDF", e);
        }
    }

    @PostMapping("/milieu-stage/creer")
    public ResponseEntity<byte[]> creerEvaluationMilieuStage(@RequestHeader("Authorization") String token, @RequestBody EvaluationMilieuStageDTO evaluationDTO){
        validateToken(token);
        try {
            String pdfPath = evaluationService.creerEvaluationMilieuStage(evaluationDTO);
            return getResponseEntity(pdfPath);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating evaluation PDF", e);
        }
    }

    @GetMapping("/milieu-stage/{id}")
    public ResponseEntity<byte[]> recupererEvaluationMilieuStage(@PathVariable Long id){
        try {
            String pdfPath = evaluationService.recupererEvaluationMilieuStageParId(id);
            return getResponseEntity(pdfPath);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating evaluation PDF", e);
        }
    }

    @GetMapping("/milieu-stage/all-evaluation")
    public ResponseEntity<?> findAllEvaluationMilieuStage(){
        try {
            List<EvaluationMilieuStageDTO> evaluationEmployeurDTOS = evaluationService.findAllEvaluationMilieuStage();
            return new ResponseEntity<>(evaluationEmployeurDTOS, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating evaluation PDF", e);
        }
    }

    private ResponseEntity<byte[]> getResponseEntity(String pdfPath) throws IOException {
        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Generated PDF not found");
        }
        byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("evaluation_stagiaire.pdf").build());
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

}