package fructose.service;

import fructose.model.Departement;
import fructose.model.OffreStage;
import fructose.model.Utilisateur;
import fructose.model.enumerator.Role;
import fructose.repository.DepartementRepository;
import fructose.repository.EmployeurRepository;
import fructose.repository.OffreStageRepository;
import fructose.service.dto.OffreStageDTO;
import fructose.service.dto.UtilisateurDTO;
import jakarta.validation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Validated
@Service
public class OffreStageService {
	private final OffreStageRepository offreStageRepository;
	private final EmployeurRepository employeurRepository;
	private final DepartementRepository departementRepository;
	private final Validator validator;
	
	
	public OffreStageService(OffreStageRepository offreStageRepository, EmployeurRepository employeurRepository, DepartementRepository departementRepository) {
		this.offreStageRepository = offreStageRepository;
		this.employeurRepository = employeurRepository;
		this.departementRepository = departementRepository;
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		this.validator = factory.getValidator();
	}
	
	public Utilisateur getUtilisateurEnCours() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			return employeurRepository.findByEmail(authentication.getName());
		}
		throw new IllegalArgumentException("Aucun utilisateur n'est connecté");
	}
	
	public void addOffreStage(OffreStageDTO offreStageDTO) {
		if (offreStageDTO == null) {
			throw new IllegalArgumentException("OffreStageDTO ne peut pas être nul");
		}
		validateAddOffreStage(offreStageDTO, offreStageDTO.getOwnerDTO());
	}
	
	public void addOffreStage(OffreStageDTO offreStageDTO, UtilisateurDTO utilisateurDTO) {
		validateAddOffreStage(offreStageDTO, utilisateurDTO);
	}
	
	private void validateAddOffreStage(OffreStageDTO offreStageDTO, UtilisateurDTO utilisateurDTO) {
		if (offreStageDTO == null) {
			throw new IllegalArgumentException("OffreStageDTO ne peut pas être nul");
		}
		if (offreStageDTO.getOwnerDTO() == null) {
			offreStageDTO.setOwnerDTO(utilisateurDTO);
		}else if (utilisateurDTO != null) {
			if (utilisateurDTO.getRole() == Role.ADMIN) {
				offreStageDTO.setIsApproved(true);
			}
		}
		validateOffreStage(offreStageDTO);
		OffreStage offreStage = OffreStageDTO.toEntity(offreStageDTO);
		offreStageRepository.save(offreStage);
	}
	
	public void updateOffreStage(OffreStageDTO offreStageDTO) {
		if (offreStageDTO == null) {
			throw new IllegalArgumentException("OffreStageDTO ne peut pas être nul");
		}
		if (!offreStageRepository.existsById(offreStageDTO.getId())) {
			throw new IllegalArgumentException("L'offre de stage avec l'ID: " + offreStageDTO.getId() + " n'existe pas, alors il ne peut pas être mis à jour");
		}
		
		OffreStage existingOffreStage = offreStageRepository.findById(offreStageDTO.getId()).orElseThrow(() -> new IllegalArgumentException("OffreStage not found"));
		
		Departement departement = departementRepository.findById(offreStageDTO.getDepartementDTO().getId()).orElseThrow(() -> new IllegalArgumentException("Le département ne peut pas être nul"));

		Utilisateur owner = employeurRepository.findById(offreStageDTO.getOwnerDTO().getId()).orElseThrow(() -> new IllegalArgumentException("Owner not found"));
		
		existingOffreStage.setNom(offreStageDTO.getNom());
		existingOffreStage.setPoste(offreStageDTO.getPoste());
		existingOffreStage.setDescription(offreStageDTO.getDescription());
		existingOffreStage.setCompagnie(offreStageDTO.getCompagnie());
		existingOffreStage.setDepartement(departement);
		existingOffreStage.setTauxHoraire(offreStageDTO.getTauxHoraire());
		existingOffreStage.setTypeEmploi(offreStageDTO.getTypeEmploi());
		existingOffreStage.setAdresse(offreStageDTO.getAdresse());
		existingOffreStage.setModaliteTravail(offreStageDTO.getModaliteTravail());
		existingOffreStage.setDateDebut(offreStageDTO.getDateDebut());
		existingOffreStage.setDateFin(offreStageDTO.getDateFin());
		existingOffreStage.setNombreHeuresSemaine(offreStageDTO.getNombreHeuresSemaine());
		existingOffreStage.setNombrePostes(offreStageDTO.getNombrePostes());
		existingOffreStage.setNombrePostes(offreStageDTO.getNombrePostes());
		existingOffreStage.setDateLimiteCandidature(offreStageDTO.getDateLimiteCandidature());
		existingOffreStage.setOwner(owner);
		existingOffreStage.setIsRefused(false);
		existingOffreStage.setIsApproved(false);
		offreStageRepository.save(existingOffreStage);
	}

	public void deleteOffreStage(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID ne peut pas être nul");
		}
		if (!offreStageRepository.existsById(id)) {
			throw new IllegalArgumentException("L'offre stage avec l'ID: " + id + " n'existe pas, alors il ne peut pas être supprimé");
		}
		offreStageRepository.deleteById(id);
	}
	
	private void validateOffreStage(OffreStageDTO offreStageDTO) {
		if (offreStageDTO == null) {
			throw new IllegalArgumentException("OffreStageDTO ne peut pas être nul");
		}
		Set<ConstraintViolation<OffreStageDTO>> violations = validator.validate(offreStageDTO);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		if (offreStageDTO.getDateLimiteCandidature().isBefore(LocalDate.now().plusDays(7))) {
			throw new IllegalArgumentException("La date limite de candidature doit être au moins 7 jours après aujourd'hui");
		}
		if (offreStageDTO.getDateDebut().isBefore(offreStageDTO.getDateLimiteCandidature().plusDays(1))) {
			throw new IllegalArgumentException("La date de début doit être au moins 1 jour après la date limite de candidature");
		}
		if (offreStageDTO.getDateFin().isBefore(offreStageDTO.getDateDebut().plusDays(1))) {
			throw new IllegalArgumentException("La date de fin doit être au moins 1 jour après la date de début");
		}
	}
	
	public OffreStageDTO getOffreStageById(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID ne peut pas être nul");
		}
		OffreStage offreStage = offreStageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("L'offre stage avec l'ID: " + id + " n'existe pas, alors il ne peut pas être récupéré"));
		return OffreStageDTO.toDTO(offreStage);
	}
	
	public List<OffreStageDTO> getOffresStage() {
		Utilisateur utilisateur = getUtilisateurEnCours();
		List<OffreStageDTO> offresStage;
		if (utilisateur.getRole() == null) {
			throw new IllegalArgumentException("Le role de l'utilisateur ne peut pas être nul");
		}
		switch (utilisateur.getRole()) {
			case ADMIN -> {
				offresStage = OffreStageDTO.toDTOs(offreStageRepository.findAll());
				if (offresStage.isEmpty()) {
					throw new IllegalArgumentException("Aucune offre stage a été créer pour l'instant");
				}
			}
			case EMPLOYEUR -> {
				offresStage = OffreStageDTO.toDTOs(offreStageRepository.getAllByOwnerId(utilisateur.getId()));
				if (offresStage.isEmpty()) {
					throw new IllegalArgumentException("Aucune offre de stage trouvée pour l'employeur avec l'email: " + utilisateur.getEmail());
				}
			}
			case PROFESSEUR -> {
				offresStage = OffreStageDTO.toDTOs(offreStageRepository.findByUserDepartementAndIsApproved(utilisateur.getDepartement().getId()));
				if (offresStage.isEmpty()) {
					throw new IllegalArgumentException("Aucune offre de stage approuvée trouvée pour le professeur dans le département: " + utilisateur.getDepartement());
				}
			}
			case ETUDIANT -> {
				offresStage = OffreStageDTO.toDTOs(offreStageRepository.findByUserDepartementAndIsApproved(utilisateur.getDepartement().getId()));
				if (offresStage.isEmpty()) {
					throw new IllegalArgumentException("Aucune offre de stage trouvée pour l'étudiant dans le département: " + utilisateur.getDepartement());
				}
			}
			default ->
				throw new IllegalArgumentException("Aucune offre de stage n'a été trouvée pour le role inconnue");
		}
		return offresStage;
	}
	
	public void refuserOffreStage(Long offreId, String commentaireRefus) {
		try {
			OffreStage offreStage = offreStageRepository.findById(offreId).orElseThrow(null);
			offreStage.setIsRefused(true);
			offreStage.setCommentaireRefus(commentaireRefus);
			offreStageRepository.save(offreStage);
		} catch (Exception e) {
			throw new RuntimeException("Une erreur est survenue lors du refus de l'offre de stage.", e);
		}
	}

    public void accepterOffreStage(Long id) {
		try {
			OffreStage offreStage = offreStageRepository.findById(id).orElseThrow(null);
			offreStage.setIsApproved(true);
			offreStageRepository.save(offreStage);
		} catch (Exception e) {
			throw new RuntimeException("Une erreur est survenue lors du refus de l'offre de stage.", e);
		}
    }
}