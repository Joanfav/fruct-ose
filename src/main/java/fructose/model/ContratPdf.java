package fructose.model;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.util.Objects;

public class ContratPdf {
	
	private final Contrat contrat;
	
	public ContratPdf(Contrat contrat) {
		this.contrat = contrat;
	}
	
	public String returnPdf() {
		OffreStage offreStage = contrat.getCandidature().getOffreStage();
		Utilisateur employeur = offreStage.getOwner();
		Utilisateur etudiant = contrat.getCandidature().getEtudiant();
		Utilisateur gestionnaire = contrat.getGestionnaire();
		
		String dest = "contract_stage" + contrat.getCandidature().getId() + ".pdf";
		try {
			PdfWriter writer = new PdfWriter(dest);
			PdfDocument pdf = new PdfDocument(writer);
			Document document = new Document(pdf);
			
			document.add(new Paragraph("Le gestionnaire de stage: " + gestionnaire.getFullName()));
			document.add(new Paragraph("L'employeur: " + employeur.getFullName()));
			document.add(new Paragraph("L'étudiant(e): " + etudiant.getFullName()));
			
			Table table = new Table(2);
			table.addCell("ENDROIT DU STAGE");
			table.addCell(offreStage.getAdresse());
			table.addCell("DUREE DU STAGE");
			table.addCell("Date de début: " + offreStage.getDateDebut() + "\nDate de fin: " + offreStage.getDateFin() + "\nNombre total de semaines: " + offreStage.getDureeEnSemaines());
			table.addCell("HORAIRE DE TRAVAIL");
			table.addCell("Horaire de travail: " + getTypeEmploi(offreStage.getTypeEmploi()) + ", " + getModaliteTravail(offreStage.getModaliteTravail()) + "\nNombre total d'heures par semaine: " + offreStage.getNombreHeuresSemaine() + "h");
			table.addCell("SALAIRE");
			table.addCell("Salaire horaire: " + offreStage.getTauxHoraire() + "$");
			document.add(table);
			
			document.add(new Paragraph("TACHES ET RESPONSABILITES DU STAGIAIRE"));
			document.add(new Paragraph(offreStage.getDescription()));
			document.add(new Paragraph("RESPONSABILITES"));
			document.add(new Paragraph("Le Collège s'engage à:\n..."));
			document.add(new Paragraph("L’entreprise s’engage à:\n..."));
			document.add(new Paragraph("L’étudiant s’engage à:\n..."));
			document.add(new Paragraph("SIGNATURES"));

			document.add(new Paragraph("Signature de l’étudiant(e): " + (!Objects.equals(contrat.getSignatureEtudiant(), "Non signe") ? contrat.getSignatureEtudiant() : "_______________________") + "\n " + "Date de signature : " + (contrat.getDateSignatureEtudiant() != null ? contrat.getDateSignatureEtudiant() : "_______________________")));
			document.add(new Paragraph("Signature de l’employeur: " + (!Objects.equals(contrat.getSignatureEmployeur(), "Non signe") ? contrat.getSignatureEmployeur() : "_______________________") + "\n " + "Date de signature : " + (contrat.getDateSignatureEmployeur() != null ? contrat.getDateSignatureEmployeur() : "_______________________")));
			document.add(new Paragraph("Signature du gestionnaire de stage: " + (contrat.getSignatureGestionnaire() != null ? contrat.getSignatureGestionnaire() : "_______________________") + "\n " + " Date de signature : " + (contrat.getDateSignatureGestionnaire() != null ? contrat.getDateSignatureGestionnaire().toString() : "_______________________")));
			document.close();
			return dest;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("PDF creation failed: " + e.getMessage());
		}
	}
	
	private String getModaliteTravail(String modalite) {
		if (modalite.equals("temps_plein")) {
			return "Temps plein";
		} else if (modalite.equals("temps_partiel")) {
			return "Temps partiel";
		} else {
			return "Inconnu";
		}
	}
	
	private String getTypeEmploi(String type) {
		if (type.equals("presentiel")) {
			return "Présentiel";
		} else if (type.equals("virtuel")) {
			return "Virtuel";
		} else if (type.equals("hybride")) {
			return "Hybride";
		} else {
			return "Inconnu";
		}
	}
}