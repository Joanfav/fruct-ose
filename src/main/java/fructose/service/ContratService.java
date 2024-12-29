package fructose.service;

import fructose.model.Contrat;
import fructose.model.ContratPdf;
import fructose.model.enumerator.EtatCandidature;
import fructose.repository.ContratRepository;
import fructose.service.dto.CandidatureDTO;
import fructose.service.dto.ContratDTO;
import fructose.service.dto.ContratSansCvDTO;
import fructose.service.dto.UtilisateurDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ContratService {
	
	private final ContratRepository contratRepository;
	private final CandidatureService candidatureService;
	private final UtilisateurService utilisateurService;

	public String generateContrat(CandidatureDTO candidatureDTO, UtilisateurDTO admin) {
		if (candidatureDTO == null || admin == null) {
			throw new IllegalArgumentException("CandidatureDTO et UtilisateurDTO ne peuvent pas être nuls");
		}
		try {
			ContratDTO contratDTO = new ContratDTO();
			contratDTO.setCandidatureDTO(candidatureDTO);
			contratDTO.setGestionnaire(admin);
			Contrat contrat = ContratDTO.toEntity(contratDTO);
			ContratPdf contratPdf = new ContratPdf(contrat);
			return contratPdf.returnPdf();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error generating contract PDF", e);
		}
	}

	public String generateContrat(ContratDTO contratDTO) {
		if (contratDTO == null) {
			throw new IllegalArgumentException("ContratDTO ne peut pas être nul");
		}
		if (contratDTO.getCandidatureDTO() == null) {
			throw new IllegalArgumentException("CandidatureDTO ne peut pas être nul");
		}
		if (contratDTO.getGestionnaire() == null) {
			throw new IllegalArgumentException("UtilisateurDTO ne peut pas être nul");
		}
		try {
			Contrat contrat = ContratDTO.toEntity(contratDTO);
			ContratPdf contratPdf = new ContratPdf(contrat);
			return contratPdf.returnPdf();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error generating contract PDF", e);
		}
	}
	
	public void saveContrat(ContratDTO contratDTO) {
		if (contratDTO == null) {
			throw new IllegalArgumentException("ContratDTO ne peut pas être nul");
		}
		Contrat contrat = ContratDTO.toEntity(contratDTO);
		contratRepository.save(contrat);
	}

	public ContratSansCvDTO getContratByCandidatureId(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID de candidature ne peut pas être nul");
		}
		ContratSansCvDTO contrat = contratRepository.findByCandidatureIdWithoutCv(id);
		if (contrat == null) {
			throw new IllegalArgumentException("Contrat avec ID de candidature: " + id + " n'existe pas");
		}
		return contrat;
	}

	public ContratDTO getContratById(Long contratId) {
		if (contratId == null) {
			throw new IllegalArgumentException("ID de contrat ne peut pas être nul");
		}
		return ContratDTO.toDTO(contratRepository.findById(contratId).orElseThrow(() -> new IllegalArgumentException("Contrat avec ID: " + contratId + " n'existe pas")));
	}

	public void signContrat(Long contratId, UtilisateurDTO utilisateurDTO) {
		if (contratId == null || utilisateurDTO == null) {
			throw new IllegalArgumentException("ID de contrat et UtilisateurDTO ne peuvent pas être nuls");
		}
		Contrat contrat = contratRepository.findById(contratId).orElseThrow(() -> new IllegalArgumentException("Contrat avec ID: " + contratId + " n'existe pas"));
		switch (utilisateurDTO.getRole()) {
			case EMPLOYEUR:
				contrat.setSignatureEmployeur(utilisateurDTO.getFullName());
				contrat.setDateSignatureEmployeur(LocalDate.now());
				break;
			case ETUDIANT:
				contrat.setSignatureEtudiant(utilisateurDTO.getFullName());
				contrat.setDateSignatureEtudiant(LocalDate.now());
				break;
			default:
				throw new IllegalArgumentException("Utilisateur n'est pas un employeur ou un étudiant");
		}
		if((!contrat.getSignatureEtudiant().equals("Non signe") && !contrat.getSignatureEtudiant().equals("Refuse")) && (!contrat.getSignatureEmployeur().equals("Non signe") && !contrat.getSignatureEmployeur().equals("Refuse"))){
			candidatureService.modifierEtatCandidature(contrat.getCandidature().getId(), EtatCandidature.POSTE_OBTENU);
		}
		contratRepository.save(contrat);
	}

	public void refuserContrat(Long id, UtilisateurDTO utilisateur) {
		if (id == null || utilisateur == null) {
			throw new IllegalArgumentException("ID de contrat et UtilisateurDTO ne peuvent pas être nuls");
		}
		Contrat contrat = contratRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Contrat avec ID: " + id + " n'existe pas"));
		switch (utilisateur.getRole()) {
			case EMPLOYEUR:
				contrat.setSignatureEmployeur("Refuse");
				break;
			case ETUDIANT:
				contrat.setSignatureEtudiant("Refuse");
				break;
			default:
				throw new IllegalArgumentException("Utilisateur n'est pas un employeur ou un étudiant");
		}
		contratRepository.save(contrat);
	}

	public List<ContratDTO> getContrats() {
		List<ContratDTO> contratDTOS = contratRepository.findAll().stream().map(ContratDTO::toDTO).toList();
		if (contratDTOS.isEmpty()) {
			throw new IllegalArgumentException("Aucun contrat trouvé");
		}
		return contratDTOS;
	}
}