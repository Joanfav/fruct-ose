package fructose.model;

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
@Table (name = "contrat")
public class Contrat {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	@Column (name = "id", nullable = false)
	private Long id;
	
	@ManyToOne
	@JoinColumn (name = "candidature_id", nullable = false)
	@OnDelete (action = OnDeleteAction.CASCADE)
	private Candidature candidature;
	
	@ManyToOne
	@JoinColumn (name = "gestionnaire_id", nullable = false)
	private Utilisateur gestionnaire;
	
	@Column (name = "signature_gestionnaire", nullable = false)
	private String signatureGestionnaire;
	
	@Column (name = "date_signature_gestionnaire", nullable = false)
	private LocalDate dateSignatureGestionnaire;
	
	@Column (name = "signature_employeur")
	private String signatureEmployeur = "Non signe";
	
	@Column (name = "date_signature_employeur")
	private LocalDate dateSignatureEmployeur;
	
	@Column (name = "signature_etudiant")
	private String signatureEtudiant = "Non signe";
	
	@Column (name = "date_signature_etudiant")
	private LocalDate dateSignatureEtudiant;
}