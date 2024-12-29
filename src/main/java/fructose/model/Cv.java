package fructose.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cv")
public class Cv {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;
	
	@Column(name = "filename", nullable = false)
	@Nullable
	private String filename;
	
	// Stocke données binaires volumineuses (PDF)
	@Lob
	@Column(name = "file_content")
	@Nullable
	@Basic(fetch = FetchType.LAZY)
	private byte[] fileContent;
	
	@ManyToOne
	@JoinColumn(name = "utilisateur_id", nullable = false)
	private Utilisateur utilisateur;
	
	@Column(name = "is_approved", nullable = false)
	private Boolean isApproved = false;
	
	@Column(name = "is_refused", nullable = false)
	private Boolean isRefused = false;
	
	@Column(name = "commentaire_refus")
	private String commentaireRefus = "Refusé par Defaut";
	
	
	public Cv(Long id, @Nullable String filename, Boolean isApproved, Boolean isRefused, Utilisateur utilisateur, String commentaireRefus) {
		this.id = id;
		this.filename = filename;
		this.isApproved = isApproved;
		this.isRefused = isRefused;
		this.utilisateur = utilisateur;
		this.commentaireRefus = commentaireRefus;
	}
}