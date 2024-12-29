package fructose.model;

import fructose.model.enumerator.EtatCandidature;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table (name = "candidature")
public class Candidature {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	@Column (name = "id", nullable = false)
	private Long id;
	
	@ManyToOne
	@JoinColumn (name = "etudiant_id", nullable = false)
	@OnDelete (action = OnDeleteAction.CASCADE)
	private Utilisateur etudiant;
	
	@ManyToOne
	@JoinColumn (name = "offre_stage_id", nullable = false)
	@OnDelete (action = OnDeleteAction.CASCADE)
	private OffreStage offreStage;
	
	@ManyToOne
	@JoinColumn (name = "cv_id", nullable = false)
	@OnDelete (action = OnDeleteAction.CASCADE)
	private Cv cv;
	
	@Enumerated (EnumType.STRING)
	private EtatCandidature etat;
	
	private String commentaireRefus;
	
	private LocalDate dateEntrevue;
	
	public Candidature(Long id, Utilisateur etudiant, OffreStage offreStage, EtatCandidature etat, String commentaireRefus, LocalDate dateEtrevue) {
		this.id = id;
		this.etudiant = etudiant;
		this.offreStage = offreStage;
		this.etat = etat;
		this.commentaireRefus = commentaireRefus;
		this.dateEntrevue = dateEtrevue;
	}
}