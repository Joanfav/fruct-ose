package fructose.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table (name = "offre_stage")
public class OffreStage {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	@Column (name = "id", nullable = false)
	private Long id;
	
	@NotEmpty (message = "Le nom ne peut pas être vide")
	@Size (min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
	private String nom;
	
	@NotEmpty (message = "Le poste ne peut pas être vide")
	@Size (min = 3, max = 100, message = "Le poste doit contenir entre 3 et 100 caractères")
	private String poste;
	
	@NotEmpty (message = "La description ne peut pas être vide")
	@Size (min = 10, max = 500, message = "La description doit contenir entre 10 et 500 caractères")
	private String description;
	
	@NotEmpty (message = "Le nom de la compagnie ne peut pas être vide")
	@Size (min = 3, max = 100, message = "Le nom de la compagnie doit contenir entre 3 et 100 caractères")
	private String compagnie;
	
	@ManyToOne
	@JoinColumn (name = "departement_id")
	private Departement departement;
	
	@NotNull (message = "Le taux horaire ne peut pas être null")
	@DecimalMin (value = "0.0", message = "Le taux horaire ne peut pas être négatif")
	private Double tauxHoraire;
	
	@NotNull (message = "Le type d'emploi ne peut pas être null")
	@Pattern (regexp = "^(presentiel|virtuel|hybride)$", message = "Le type d'emploi doit être l'un des suivants : Presentiel, Virtuel, Hybride")
	private String typeEmploi;
	
	@NotEmpty (message = "L'adresse ne peut pas être vide")
	@Size (min = 3, max = 100, message = "L'adresse doit contenir entre 3 et 100 caractères")
	private String adresse;
	
	@NotEmpty (message = "La modalité de travail ne peut pas être vide")
	@Pattern (regexp = "^(temps_plein|temps_partiel)$", message = "La modalité de travail doit être l'une des suivantes : Temps plein, Temps partiel")
	private String modaliteTravail;
	
	@NotNull (message = "La date de début ne peut pas être null")
	private LocalDate dateDebut;
	
	@NotNull (message = "La date de fin ne peut pas être null")
	private LocalDate dateFin;
	
	@Min (value = 1, message = "Le nombre d'heures par semaine ne peut pas être inferieur a 1")
	@Max (value = 40, message = "Le nombre d'heures par semaine ne peut pas être superieur a 40")
	private int nombreHeuresSemaine;
	
	@Min (value = 1, message = "Le nombre de postes ne peut pas être inferieur a 1")
	private int nombrePostes;
	
	@NotNull (message = "La date limite de candidature ne peut pas être null")
	private LocalDate dateLimiteCandidature;
	
	@ManyToOne (cascade = CascadeType.MERGE)
	@JoinColumn (name = "owner_id")
	private Utilisateur owner;
	
	@Column (name = "is_refused", nullable = false)
	private Boolean isRefused = false;
	
	@Column (name = "is_approved", nullable = false)
	private Boolean isApproved = false;
	
	@Column (name = "commentaire_refus")
	private String commentaireRefus = "Commentaire par défaut";
	
	@OneToMany (mappedBy = "offreStage")
	private Set<Candidature> candidatures;
	
	public static OffreStage createOffreStage(String nom,
	                                          String poste,
	                                          String description,
	                                          String compagnie,
	                                          Departement departement,
	                                          Double tauxHoraire,
	                                          String typeEmploi,
	                                          String adresse,
	                                          String modaliteTravail,
	                                          LocalDate dateDebut,
	                                          LocalDate dateFin,
	                                          int nombreHeuresSemaine,
	                                          int nombrePostes,
	                                          LocalDate dateLimiteCandidature,
	                                          Utilisateur owner,
	                                          Boolean isRefused,
	                                          Boolean isApproved,
	                                          String commentaireRefus) {
		OffreStage offreStage = new OffreStage();
		offreStage.setNom(nom);
		offreStage.setPoste(poste);
		offreStage.setDescription(description);
		offreStage.setCompagnie(compagnie);
		offreStage.setDepartement(departement);
		offreStage.setTauxHoraire(tauxHoraire);
		offreStage.setTypeEmploi(typeEmploi);
		offreStage.setAdresse(adresse);
		offreStage.setModaliteTravail(modaliteTravail);
		offreStage.setDateDebut(dateDebut);
		offreStage.setDateFin(dateFin);
		offreStage.setNombreHeuresSemaine(nombreHeuresSemaine);
		offreStage.setNombrePostes(nombrePostes);
		offreStage.setDateLimiteCandidature(dateLimiteCandidature);
		offreStage.setOwner(owner);
		offreStage.setIsRefused(isRefused);
		offreStage.setIsApproved(isApproved);
		offreStage.setCommentaireRefus(commentaireRefus);
		return offreStage;
	}

	public Long getDureeEnSemaines() {
		return (dateFin.toEpochDay() - dateDebut.toEpochDay()) / 7;
	}
}