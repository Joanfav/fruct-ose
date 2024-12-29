package fructose.controller;

import fructose.model.enumerator.Role;
import fructose.service.CandidatureService;
import fructose.service.ContratService;
import fructose.service.UtilisateurService;
import fructose.service.dto.CandidatureDTO;
import fructose.service.dto.ContratDTO;
import fructose.service.dto.ContratSansCvDTO;
import fructose.service.dto.UtilisateurDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Collections;

@RestController
@RequestMapping ("/contrats")
@RequiredArgsConstructor
public class ContratController {

	private final ContratService contratService;
	private final UtilisateurService utilisateurService;
	private final CandidatureService candidatureService;

	private void validateToken(String token) {
		if (!utilisateurService.validationToken(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
	}

	@GetMapping ("/generate/{id}")
	public ResponseEntity<byte[]> generateContrat(@RequestHeader ("Authorization") String token, @PathVariable Long id) {
		validateToken(token); // Validates the token
		try {
			UtilisateurDTO utilisateur = utilisateurService.getUtilisateurByToken(token);
			CandidatureDTO candidatureDTO = candidatureService.getCandidatureById(id);

			String pdfPath = contratService.generateContrat(candidatureDTO, utilisateur);
			return getPDFResponseEntity(pdfPath);
		}catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating contract", e);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<byte[]> generateContratAlreadyExists(@RequestHeader ("Authorization") String token, @PathVariable Long id) {
		validateToken(token);
		try {
			ContratDTO contrat = contratService.getContratById(id);
			if (contrat == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract not found");
			}

			String pdfPath = contratService.generateContrat(contrat);
			return getPDFResponseEntity(pdfPath);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating contract", e);
		}
	}

	private ResponseEntity<byte[]> getPDFResponseEntity(String pdfPath) {
		try {
			File pdfFile = new File(pdfPath);
			if (!pdfFile.exists()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Generated PDF not found");
			}

			byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDisposition(ContentDisposition.inline().filename("contract_stage.pdf").build());
			headers.setContentLength(pdfBytes.length);

			Files.delete(pdfFile.toPath());

			return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading contract PDF", e);
		}
	}

	@PostMapping
	public void saveContrat(@RequestHeader ("Authorization") String token, @Valid @RequestBody CandidatureDTO candidatureDTO) {
		validateToken(token);
		try {
			UtilisateurDTO utilisateur = utilisateurService.getUtilisateurByToken(token);
			ContratDTO contratDTO = new ContratDTO();
			contratDTO.setCandidatureDTO(candidatureDTO);
			contratDTO.setGestionnaire(utilisateur);
			contratDTO.setSignatureGestionnaire(utilisateur.getFullName());
			contratDTO.setDateSignatureGestionnaire(LocalDate.now());
			contratService.saveContrat(contratDTO);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error saving contract", e);
		}
	}

	@GetMapping ("/candidatures/{id}")
	public ContratSansCvDTO getContratByCandidatureId(@RequestHeader ("Authorization") String token, @PathVariable Long id) {
		validateToken(token);
		try {
			return contratService.getContratByCandidatureId(id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting contract by contrat id because ", e);
		}
	}

	@PatchMapping("/{id}/signer")
	public void signerContrat(@RequestHeader ("Authorization") String token, @PathVariable Long id) {
		validateToken(token);
		try {
			UtilisateurDTO utilisateur = utilisateurService.getUtilisateurByToken(token);
			contratService.signContrat(id, utilisateur);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error signing contract", e);
		}
	}

	@PatchMapping("{id}/refuser")
	public void refuserContrat(@RequestHeader("Authorization") String token, @PathVariable Long id) {
		validateToken(token);
		try {
			UtilisateurDTO utilisateur = utilisateurService.getUtilisateurByToken(token);
			contratService.refuserContrat(id, utilisateur);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error refusing contract", e);
		}
	}

	@GetMapping
	public ResponseEntity<?> getContrats(@RequestHeader("Authorization") String token) {
		validateToken(token);
		UtilisateurDTO utilisateur = utilisateurService.getUtilisateurByToken(token);
		if (!utilisateur.getRole().equals(Role.ADMIN)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not an admin");
		}
		try {
			return new ResponseEntity<>(contratService.getContrats(), HttpStatus.OK);
		}catch (IllegalArgumentException e) {
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting contracts", e);
		}
	}

}