package fructose.service.dto;

import fructose.model.OffreStage;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OffreStageDTO {
	
	private Long id;
	
	@NotEmpty(message = "Le nom ne peut pas être vide")
	@Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
	private String nom;
	
	@NotEmpty(message = "Le poste ne peut pas être vide")
	@Size(min = 3, max = 100, message = "Le poste doit contenir entre 3 et 100 caractères")
	private String poste;
	
	@NotEmpty(message = "La description ne peut pas être vide")
	@Size(min = 10, max = 500, message = "La description doit contenir entre 10 et 500 caractères")
	private String description;
	
	@NotEmpty(message = "Le nom de la compagnie ne peut pas être vide")
	@Size(min = 3, max = 100, message = "Le nom de la compagnie doit contenir entre 3 et 100 caractères")
	private String compagnie;
	
	@NotNull(message = "Le département ne peut pas être null")
	private DepartementDTO departementDTO;
	
	@NotNull(message = "Le taux horaire ne peut pas être null")
	@DecimalMin(value = "0.0", message = "Le taux horaire ne peut pas être négatif")
	private Double tauxHoraire;
	
	@NotNull(message = "Le type d'emploi ne peut pas être null")
	@Pattern(regexp = "^(presentiel|virtuel|hybride)$", message = "Le type d'emploi doit être l'un des suivants : Presentiel, Virtuel, Hybride")
	private String typeEmploi;
	
	@NotEmpty(message = "L'adresse ne peut pas être vide")
	@Size(min = 3, max = 100, message = "L'adresse doit contenir entre 3 et 100 caractères")
	private String adresse;
	
	@NotEmpty(message = "La modalité de travail ne peut pas être vide")
	@Pattern(regexp = "^(temps_plein|temps_partiel)$", message = "La modalité de travail doit être l'une des suivantes : Temps plein, Temps partiel")
	private String modaliteTravail;
	
	@NotNull(message = "La date de début ne peut pas être null")
	private LocalDate dateDebut;
	
	@NotNull(message = "La date de fin ne peut pas être null")
	private LocalDate dateFin;
	
	@Min(value = 1, message = "Le nombre d'heures par semaine ne peut pas être inferieur a 1")
	@Max(value = 40, message = "Le nombre d'heures par semaine ne peut pas être superieur a 40")
	private int nombreHeuresSemaine;
	
	@Min(value = 1, message = "Le nombre de postes ne peut pas être inferieur a 1")
	private int nombrePostes;
	
	@NotNull(message = "La date limite de candidature ne peut pas être null")
	private LocalDate dateLimiteCandidature;
	
	private UtilisateurDTO ownerDTO;
	
	private Boolean isApproved = false;
	
	private Boolean isRefused = false;
	
	private String commentaireRefus = "Commentaire par défaut";
	
	public static OffreStageDTO toDTO(OffreStage offreStage) {
		OffreStageDTO offreStageDTO = new OffreStageDTO();
		offreStageDTO.setId(offreStage.getId());
		offreStageDTO.setNom(offreStage.getNom());
		offreStageDTO.setPoste(offreStage.getPoste());
		offreStageDTO.setDescription(offreStage.getDescription());
		offreStageDTO.setCompagnie(offreStage.getCompagnie());
		offreStageDTO.setDepartementDTO(DepartementDTO.toDTO(offreStage.getDepartement()));
		offreStageDTO.setTauxHoraire(offreStage.getTauxHoraire());
		offreStageDTO.setTypeEmploi(offreStage.getTypeEmploi());
		offreStageDTO.setAdresse(offreStage.getAdresse());
		offreStageDTO.setModaliteTravail(offreStage.getModaliteTravail());
		offreStageDTO.setDateDebut(offreStage.getDateDebut());
		offreStageDTO.setDateFin(offreStage.getDateFin());
		offreStageDTO.setNombreHeuresSemaine(offreStage.getNombreHeuresSemaine());
		offreStageDTO.setNombrePostes(offreStage.getNombrePostes());
		offreStageDTO.setDateLimiteCandidature(offreStage.getDateLimiteCandidature());
		offreStageDTO.setOwnerDTO(UtilisateurDTO.toDTO(offreStage.getOwner()));
		offreStageDTO.setIsRefused(offreStage.getIsRefused());
		offreStageDTO.setIsApproved(offreStage.getIsApproved());
		offreStageDTO.setCommentaireRefus(offreStage.getCommentaireRefus());
		return offreStageDTO;
	}
	
	public static OffreStage toEntity(OffreStageDTO offreStageDTO) {
		OffreStage offreStage = new OffreStage();
		offreStage.setId(offreStageDTO.getId());
		offreStage.setNom(offreStageDTO.getNom());
		offreStage.setPoste(offreStageDTO.getPoste());
		offreStage.setDescription(offreStageDTO.getDescription());
		offreStage.setCompagnie(offreStageDTO.getCompagnie());
		offreStage.setDepartement(DepartementDTO.toEntity(offreStageDTO.getDepartementDTO()));
		offreStage.setTauxHoraire(offreStageDTO.getTauxHoraire());
		offreStage.setTypeEmploi(offreStageDTO.getTypeEmploi());
		offreStage.setAdresse(offreStageDTO.getAdresse());
		offreStage.setModaliteTravail(offreStageDTO.getModaliteTravail());
		offreStage.setDateDebut(offreStageDTO.getDateDebut());
		offreStage.setDateFin(offreStageDTO.getDateFin());
		offreStage.setNombreHeuresSemaine(offreStageDTO.getNombreHeuresSemaine());
		offreStage.setNombrePostes(offreStageDTO.getNombrePostes());
		offreStage.setDateLimiteCandidature(offreStageDTO.getDateLimiteCandidature());
		offreStage.setOwner(UtilisateurDTO.toEntity(offreStageDTO.getOwnerDTO()));
		offreStage.setIsApproved(offreStageDTO.getIsApproved());
		offreStage.setIsRefused(offreStageDTO.getIsRefused());
		offreStage.setCommentaireRefus(offreStageDTO.getCommentaireRefus());
		return offreStage;
	}
	
	public static List<OffreStageDTO> toDTOs(List<OffreStage> offresStage) {
		return offresStage.stream().map(OffreStageDTO::toDTO).toList();
	}
}