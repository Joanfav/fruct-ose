package fructose;

import fructose.model.*;
import fructose.model.enumerator.Role;
import fructose.service.DepartementService;
import fructose.service.OffreStageService;
import fructose.service.UtilisateurService;
import fructose.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class FructoseApplication implements CommandLineRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(FructoseApplication.class);
	private final UtilisateurService utilisateurService;
	private final OffreStageService offreStageService;
	private final DepartementService departementService;
	
	public FructoseApplication(UtilisateurService utilisateurService,
	                           OffreStageService offreStageService,
	                           DepartementService departementService) {
		this.utilisateurService = utilisateurService;
		this.offreStageService = offreStageService;
		this.departementService = departementService;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(FructoseApplication.class, args);
	}
	
	@Override
	public void run(String... args) {
		
		//DATES
		LocalDate dateDebut = LocalDate.of(2025, 1, 20);
		LocalDate dateFin = LocalDate.of(2025, 5, 31);
		LocalDate dateLimiteCandidature = LocalDate.of(2025, 1, 10);
		
		//DATES DANS LE CYBERPUNK 2077
		LocalDate dateDebutCYBERPUNK = LocalDate.of(2077, 1, 20);
		LocalDate dateFinCYBERPUNK = LocalDate.of(2077, 5, 31);
		LocalDate dateLimiteCandidatureCYBERPUNK = LocalDate.of(2077, 1, 10);
		
		// DEPARTEMENTS
		checkAndAddDepartement();
		Departement departementInformatique = getDepartement("techniques_informatique");
		Departement departementCegep = getDepartement("CÉGEP ANDRÉ LAURENDEAU");
		
		// ÉTUDIANTS APPROUVÉS
		createAndPersistUtilisateur("Etudiant", "Vazgen Markaryan", "vazgen@gmail.com", "Vazgen123!", "1111111", departementInformatique, null, true);
		createAndPersistUtilisateur("Etudiant", "Alexander Tudose", "alex@gmail.com", "Alex123!", "2222222", departementInformatique, null, true);
		createAndPersistUtilisateur("Etudiant", "Bernard Thomas Laforest", "bernard@gmail.com", "Bernard123!", "3333333", departementInformatique, null, true);
		createAndPersistUtilisateur("Etudiant", "Lou Jonathan Monty", "lou@gmail.com", "LouJM123!", "4444444", departementInformatique, null, true);
		createAndPersistUtilisateur("Etudiant", "Joan Favrichon", "joan@gmail.com", "Joan123!", "5555555", departementInformatique, null, true);
		
		// PROFESSEURS APPROUVÉS
		createAndPersistUtilisateur("Professeur", "François Lacoursière", "francois@gmail.com", "Francois123!", "6666666", departementInformatique, null, true);
		createAndPersistUtilisateur("Professeur", "Michel Généreux", "michel@gmail.com", "Michel123!", "7777777", departementInformatique, null, true);
		
		// EMPLOYEURS
		createAndPersistUtilisateur("Employeur", "Yves Guillemot", "ubisoft@gmail.com", "Ubisoft123!", null, null, "Ubisoft Incorporé", true);
		createAndPersistUtilisateur("Employeur", "Sundar Pichai", "google@gmail.com", "Google123!", null, null, "Google LLC", true);
		createAndPersistUtilisateur("Employeur", "Bobby Kotick", "activision@gmail.com", "Activision123!", null, null, "Activision Publishing Incorporé", true);
		
		// ADMINS
		createAndPersistUtilisateur("Admin", "Didier Tremblay", "didieradmin@gmail.com", "Didieradmin123!", "1212121", departementCegep, null, true);
		createAndPersistUtilisateur("Admin", "Karim Mihoubi", "karimadmin@gmail.com", "Karimadmin123!", "2121212", departementCegep, null, true);
		System.out.println();
		
		// OFFRES DE STAGE
		createAndPersistOffreStage("Développeur Java", "Développer des applications Java", "Développeur Java", "Ubisoft Incorporé", departementInformatique, 20.0, "presentiel", "Saint-Laurent, Montréal, Québec, Canada", "temps_partiel", dateDebutCYBERPUNK, dateFinCYBERPUNK, 30, 10, dateLimiteCandidatureCYBERPUNK, "ubisoft@gmail.com");
		createAndPersistOffreStage("Développeur C++", "Développer des jeux vidéo", "Développeur C++", "Ubisoft Incorporé", departementInformatique, 30.0, "hybride", "Santa Monica, Californie, États-Unis", "temps_plein", dateDebut, dateFin, 40, 15, dateLimiteCandidature, "ubisoft@gmail.com");
		createAndPersistOffreStage("Développeur Python", "Développer des applications Python", "Développeur Python", "Ubisoft Incorporé", departementInformatique, 22.0, "presentiel", "Redmond, Washington, États-Unis", "temps_partiel", dateDebut, dateFin, 25, 8, dateLimiteCandidature, "ubisoft@gmail.com");
		createAndPersistOffreStage("Développeur Frontend", "Créer des interfaces utilisateur", "Développeur Frontend", "Ubisoft Incorporé", departementInformatique, 28.0, "virtuel", "Menlo Park, Californie, États-Unis", "temps_plein", dateDebut, dateFin, 35, 12, dateLimiteCandidature, "ubisoft@gmail.com");
		createAndPersistOffreStage("Développeur Ruby", "Développer des applications Ruby", "Développeur Ruby", "Ubisoft Incorporé", departementInformatique, 26.0, "virtuel", "San Francisco, Californie, États-Unis", "temps_plein", dateDebut, dateFin, 23, 7, dateLimiteCandidature, "ubisoft@gmail.com");
		createAndPersistOffreStage("Ingénieur en machine learning", "Développer des modèles ML", "Ingénieur en ML", "Ubisoft Incorporé", departementInformatique, 35.0, "hybride", "Sunnyvale, Californie, États-Unis", "temps_partiel", dateDebut, dateFin, 20, 6, dateLimiteCandidature, "ubisoft@gmail.com");
		createAndPersistOffreStage("Développeur Go", "Développer des applications Go", "Développeur Go", "Ubisoft Incorporé", departementInformatique, 28.0, "presentiel", "Calgary, Alberta, Canada", "temps_plein", dateDebut, dateFin, 19, 8, dateLimiteCandidature, "ubisoft@gmail.com");
		createAndPersistOffreStage("Ingénieur en cloud", "Gérer les infrastructures cloud", "Ingénieur Cloud", "Ubisoft Incorporé", departementInformatique, 34.0, "virtuel", "Seattle, Washington, États-Unis", "temps_partiel", dateDebut, dateFin, 18, 9, dateLimiteCandidature, "ubisoft@gmail.com");
		createAndPersistOffreStage("Développeur Backend", "Développer la logique serveur", "Développeur Backend", "Ubisoft Incorporé", departementInformatique, 24.0, "hybride", "Seattle, Washington, États-Unis", "temps_partiel", dateDebutCYBERPUNK, dateFinCYBERPUNK, 20, 6, dateLimiteCandidatureCYBERPUNK, "ubisoft@gmail.com");
		createAndPersistOffreStage("Analyste de données", "Analyser les données", "Analyste de données", "Ubisoft Incorporé", departementInformatique, 27.0, "virtuel", "Redwood City, Californie, États-Unis", "temps_partiel", dateDebut, dateFin, 18, 9, dateLimiteCandidature, "ubisoft@gmail.com");

		createAndPersistOffreStage("Concepteur UX/UI", "Concevoir des expériences utilisateur", "Concepteur UX/UI", "Google LLC", departementInformatique, 29.0, "hybride", "San Jose, Californie, États-Unis", "temps_plein", dateDebut, dateFin, 22, 5, dateLimiteCandidature, "google@gmail.com");
		createAndPersistOffreStage("Testeur QA", "Tester des applications", "Testeur QA", "Google LLC", departementInformatique, 21.0, "presentiel", "San Francisco, Californie, États-Unis", "temps_partiel", dateDebut, dateFin, 19, 8, dateLimiteCandidature, "google@gmail.com");
		createAndPersistOffreStage("Développeur Kotlin", "Développer des applications Android", "Développeur Kotlin", "Google LLC", departementInformatique, 31.0, "presentiel", "Mountain View, Californie, États-Unis", "temps_partiel", dateDebut, dateFin, 20, 8, dateLimiteCandidature, "google@gmail.com");
		createAndPersistOffreStage("Développeur Swift", "Développer des applications iOS", "Développeur Swift", "Google LLC", departementInformatique, 33.0, "virtuel", "Cupertino, Californie, États-Unis", "temps_plein", dateDebut, dateFin, 20, 6, dateLimiteCandidature, "google@gmail.com");
		createAndPersistOffreStage("Ingénieur QA", "Assurer la qualité des logiciels", "Ingénieur QA", "Google LLC", departementInformatique, 29.0, "hybride", "San Francisco, Californie, États-Unis", "temps_partiel", dateDebut, dateFin, 25, 10, dateLimiteCandidature, "google@gmail.com");
		createAndPersistOffreStage("Consultant en IT", "Conseiller les entreprises sur les technologies", "Consultant IT", "Google LLC", departementInformatique, 32.0, "presentiel", "New York, New York, États-Unis", "temps_plein", dateDebut, dateFin, 22, 9, dateLimiteCandidature, "google@gmail.com");
		
		createAndPersistOffreStage("Développeur en réalité virtuelle", "Créer des applications VR", "Développeur VR", "Activision Publishing, Inc", departementInformatique, 29.0, "hybride", "Menlo Park, Californie, États-Unis", "temps_plein", dateDebutCYBERPUNK, dateFinCYBERPUNK, 17, 6, dateLimiteCandidatureCYBERPUNK, "activision@gmail.com");
		createAndPersistOffreStage("Développeur en blockchain", "Développer des solutions blockchain", "Développeur Blockchain", "Activision Publishing, Inc", departementInformatique, 38.0, "virtuel", "San Francisco, Californie, États-Unis", "temps_plein", dateDebut, dateFin, 18, 7, dateLimiteCandidature, "activision@gmail.com");
		createAndPersistOffreStage("Ingénieur en intelligence artificielle", "Développer des modèles IA", "Ingénieur IA", "Activision Publishing, Inc", departementInformatique, 40.0, "hybride", "Washington, D.C., États-Unis", "temps_partiel", dateDebut, dateFin, 20, 9, dateLimiteCandidature, "activision@gmail.com");
		createAndPersistOffreStage("Développeur en systèmes embarqués", "Développer des systèmes embarqués", "Développeur Systèmes", "Activision Publishing, Inc", departementInformatique, 27.0, "presentiel", "Miami, Floride, États-Unis", "temps_plein", dateDebut, dateFin, 22, 6, dateLimiteCandidature, "activision@gmail.com");
		createAndPersistOffreStage("Développeur Mobile", "Développer des applications mobiles", "Développeur Mobile", "Activision Publishing, Inc", departementInformatique, 23.0, "virtuel", "Toronto, Ontario, Canada", "temps_plein", dateDebut, dateFin, 20, 10, dateLimiteCandidature, "activision@gmail.com");
		createAndPersistOffreStage("Ingénieur en sécurité", "Sécuriser les systèmes", "Ingénieur en sécurité", "Activision Publishing, Inc", departementInformatique, 32.0, "hybride", "Santa Clara, Californie, États-Unis", "temps_partiel", dateDebut, dateFin, 30, 12, dateLimiteCandidature, "activision@gmail.com");
		createAndPersistOffreStage("Administrateur système", "Gérer les systèmes informatiques", "Administrateur système", "Activision Publishing, Inc", departementInformatique, 22.0, "presentiel", "Santa Clara, Californie, États-Unis", "temps_plein", dateDebut, dateFin, 15, 6, dateLimiteCandidature, "activision@gmail.com");
		createAndPersistOffreStage("Développeur PHP", "Développer des applications PHP", "Développeur PHP", "Activision Publishing, Inc", departementInformatique, 24.0, "virtuel", "Raleigh, Caroline du Nord, États-Unis", "temps_partiel", dateDebut, dateFin, 20, 8, dateLimiteCandidature, "activision@gmail.com");
		
		// APPROUVER OFFRES DE STAGE
		System.out.println(); // Ajouter une ligne vide pour la lisibilité
		approuverOffresStage(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 19L, 20L, 21L);
		
		// REFUSER OFFRES DE STAGE
		System.out.println(); // Ajouter une ligne vide pour la lisibilité
		refuserOffresStage( 13L, 14L, 15L, 22L, 23L, 24L);
	}
	
	//ADD METHODS
	private void createAndPersistUtilisateur(String role, String nom, String email, String password, String matricule, Departement departement, String entreprise, boolean isApproved) {
		UtilisateurDTO utilisateurDTO = switch (role) {
			case "Etudiant" -> createEtudiant(nom, email, password, matricule, departement, isApproved);
			case "Professeur" -> createProfesseur(nom, email, password, matricule, departement, isApproved);
			case "Employeur" -> createEmployeur(nom, email, password, departement, entreprise, isApproved);
			case "Admin" -> createAdmin(nom, email, password, matricule, departement, isApproved);
			default -> throw new IllegalArgumentException("Invalid role: " + role);
		};
		checkAndAddUtilisateur(utilisateurDTO, Role.valueOf(role.toUpperCase()));
	}
	
	private void createAndPersistOffreStage(String nom, String description, String poste, String entreprise, Departement departement, double salaire, String modeTravail, String adresse, String typeContrat, LocalDate dateDebut, LocalDate dateFin, int heuresParSemaine, int nombrePostes, LocalDate dateLimite, String emailEmployeur) {
		UtilisateurDTO employeurDTO = utilisateurService.getUtilisateurByEmail(emailEmployeur);
		OffreStage offreStage = OffreStage.createOffreStage(nom, description, poste, entreprise, departement, salaire, modeTravail, adresse, typeContrat, dateDebut, dateFin, heuresParSemaine, nombrePostes, dateLimite, UtilisateurDTO.toEntity(employeurDTO), false, false, "Commentaire par défaut");
		OffreStageDTO offreStageDTO = OffreStageDTO.toDTO(offreStage);
		checkAndAddOffreStage(offreStageDTO, employeurDTO);
	}
	
	private void checkAndAddDepartement() {
		List<Departement> departements = departementService.getAllDepartements();
		if (!departements.isEmpty()) {
			System.out.println("Les départements existent déjà dans la base de données");
			return;
		}
		
		StringBuilder resultMessage = new StringBuilder();
		boolean allSuccess = true;
		
		for (String departementName : Departement.getDepartementsParDefauts()) {
			try {
				DepartementDTO departementDTO = new DepartementDTO();
				departementDTO.setNom(departementName);
				departementService.addDepartement(departementDTO);
			} catch (Exception e) {
				resultMessage.append("Échec de l'ajout du département \"").append(departementName).append("\"\n");
				allSuccess = false;
			}
		}
		
		if (allSuccess) {
			resultMessage.append("Tous les départements ont été créés avec succès.");
		} else {
			resultMessage.append("Certains départements n'ont pas pu être créés.");
		}
		System.out.println("\n" + resultMessage.toString().trim() + "\n");
	}
	
	public void approuverOffresStage(Long... offreIds) {
		for (Long offreStageId : offreIds) {
			try {
				offreStageService.accepterOffreStage(offreStageId);
				System.out.println("OFFRE STAGE avec le nom \"" + offreStageService.getOffreStageById(offreStageId).getNom() + "\" approuvée avec succès !");
			} catch (Exception e) {
				logger.error("Erreur lors de l'approbation de l'offre de stage avec ID {}", offreStageId, e);
			}
		}
	}
	
	public void refuserOffresStage(Long... offreIds) {
		for (Long offreStageId : offreIds) {
			try {
				offreStageService.refuserOffreStage(offreStageId, "Commentaire de refus par défaut");
				System.out.println("OFFRE STAGE avec le nom \"" + offreStageService.getOffreStageById(offreStageId).getNom() + "\" refusée avec le commentaire : \"" + offreStageService.getOffreStageById(offreStageId).getCommentaireRefus() + "\"");
			} catch (Exception e) {
				logger.error("Erreur lors du refus de l'offre de stage avec ID {}", offreStageId, e);
			}
		}
	}
	
	//UTILITY METHODS
	private Departement getDepartement(String nom) {
		return DepartementDTO.toEntity(departementService.getDepartementByNom(nom));
	}
	
	private UtilisateurDTO createEtudiant(String nom, String email, String password, String matricule, Departement departement, boolean isApproved) {
		Utilisateur etudiant = Utilisateur.createUtilisateur("Etudiant", nom, email, password, matricule, departement, null, isApproved);
		return EtudiantDTO.toDTO((Etudiant) etudiant);
	}
	
	private UtilisateurDTO createProfesseur(String nom, String email, String password, String matricule, Departement departement, boolean isApproved) {
		Utilisateur professeur = Utilisateur.createUtilisateur("Professeur", nom, email, password, matricule, departement, null, isApproved);
		return ProfesseurDTO.toDTO((Professeur) professeur);
	}
	
	private UtilisateurDTO createEmployeur(String nom, String email, String password, Departement departement, String entreprise, boolean isApproved) {
		Utilisateur employeur = Utilisateur.createUtilisateur("Employeur", nom, email, password, null, departement, entreprise, isApproved);
		return EmployeurDTO.toDTO((Employeur) employeur);
	}
	
	private UtilisateurDTO createAdmin(String nom, String email, String password, String matricule, Departement departement, boolean isApproved) {
		Utilisateur admin = Utilisateur.createUtilisateur("Admin", nom, email, password, matricule, departement, null, isApproved);
		return AdminDTO.toDTO((Admin) admin);
	}
	
	private void checkAndAddOffreStage(OffreStageDTO offreStage, UtilisateurDTO ownerDTO) {
		offreStageService.addOffreStage(offreStage, ownerDTO);
		System.out.println("OFFRE STAGE avec le nom \"" + offreStage.getNom() + "\" a été ajoutée avec succès");
	}
	
	private void checkAndAddUtilisateur(UtilisateurDTO utilisateurDTO, Role role) {
		List<UtilisateurDTO> utilisateurList = utilisateurService.getUtilisateurs(role);
		boolean exists = utilisateurList.stream().anyMatch(utilisateur -> utilisateur.getEmail().equals(utilisateurDTO.getEmail()));
		
		if (exists) {
			System.out.println(role + " avec email \"" + utilisateurDTO.getEmail() + "\" existe déjà dans la base de données (Password " + utilisateurDTO.getPassword() + ")");
		} else {
			utilisateurService.addUtilisateur(utilisateurDTO);
			System.out.println(role + " avec email \"" + utilisateurDTO.getEmail() + "\" a été ajouté avec succès (Password " + utilisateurDTO.getPassword() + ")");
		}
	}
}