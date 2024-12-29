package fructose.service.dto;

import fructose.model.Cv;
import jakarta.persistence.Lob;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CvDTO {
	private Long id;
	private String filename;
	
	@Lob
	private byte[] fileContent;
	private Boolean isApproved;
	private Boolean isRefused;
	private Long utilisateurId;
	private String commentaireRefus;
	
	public static CvDTO toDTO(Cv cv) {
		CvDTO cvDTO = new CvDTO();
		cvDTO.setId(cv.getId());
		cvDTO.setFilename(cv.getFilename());
		cvDTO.setFileContent(cv.getFileContent());
		cvDTO.setIsApproved(cv.getIsApproved());
		cvDTO.setIsRefused(cv.getIsRefused());
		cvDTO.setUtilisateurId(cv.getUtilisateur().getId());
		cvDTO.setCommentaireRefus(cv.getCommentaireRefus());
		return cvDTO;
	}
}