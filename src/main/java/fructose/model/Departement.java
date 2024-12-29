package fructose.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "departement")
@ToString
public class Departement {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty(message = "Le nom ne peut pas être vide")
	@Column(name = "nom", nullable = false)
	private String nom;
	
	// Ici, c'est les départements par défaut. À ne faire qu'une seule fois dans la base de donnée.
	static public List<String> getDepartementsParDefauts() {
		List<String> departementsDefauts = new ArrayList<>();
		departementsDefauts.add("CÉGEP ANDRÉ LAURENDEAU");
		departementsDefauts.add("cinema");
		departementsDefauts.add("gestion_commerce");
		departementsDefauts.add("gestion_operations_chaine_logistique");
		departementsDefauts.add("journalisme_multimedia");
		departementsDefauts.add("langues_trilinguisme_cultures");
		departementsDefauts.add("photographie_design_graphique");
		departementsDefauts.add("sciences_nature");
		departementsDefauts.add("sciences_humaines_administration_economie");
		departementsDefauts.add("sciences_humaines_individu_relations_humaines");
		departementsDefauts.add("sciences_humaines_monde_en_action");
		departementsDefauts.add("soins_infirmiers");
		departementsDefauts.add("soins_infirmiers_auxiliaires");
		departementsDefauts.add("techniques_education_enfance");
		departementsDefauts.add("techniques_bureautique");
		departementsDefauts.add("techniques_comptabilite_gestion");
		departementsDefauts.add("techniques_informatique");
		departementsDefauts.add("techniques_travail_social");
		departementsDefauts.add("technologie_architecture");
		departementsDefauts.add("technologie_estimation_evaluation_batiment");
		departementsDefauts.add("technologie_genie_civil");
		departementsDefauts.add("technologie_genie_electrique");
		departementsDefauts.add("technologie_genie_physique");
		departementsDefauts.add("tremplin_dec");
		
		return departementsDefauts;
	}
}