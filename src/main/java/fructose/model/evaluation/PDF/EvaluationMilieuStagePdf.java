package fructose.model.evaluation.PDF;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import fructose.model.Candidature;
import fructose.model.OffreStage;
import fructose.model.Utilisateur;
import fructose.model.evaluation.CritereEvaluation;
import fructose.model.evaluation.EvaluationMilieuStage;
import fructose.model.evaluation.SectionEvaluation;


public class EvaluationMilieuStagePdf {

    private final EvaluationMilieuStage evaluation;
    public EvaluationMilieuStagePdf(EvaluationMilieuStage evaluation) {
        this.evaluation = evaluation;
    }

    public String createPdf() {
        String dest = "evaluation_milieu_stage_" + ".pdf";
        Candidature candidature = evaluation.getCandidature();
        Utilisateur etudiant = candidature.getEtudiant();
        OffreStage offreStage = candidature.getOffreStage();

        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("ÉVALUATION DU MILIEU DE STAGE").setBold().setFontSize(16));

            document.add(new Paragraph("Nom de l'élève : " + etudiant.getFullName()));
            document.add(new Paragraph("Programme d’études :" + etudiant.getDepartement().getNom()));
            document.add(new Paragraph("Nom de l’entreprise : " + offreStage.getCompagnie()));
            document.add(new Paragraph("Nom du superviseur : " + offreStage.getOwner().getFullName()));
            document.add(new Paragraph("Fonction : " + offreStage.getNom()));
            document.add(new Paragraph("Date du stage : " + offreStage.getDateDebut() + " - " + offreStage.getDateFin()));
            document.add(new Paragraph("Stage : " + evaluation.getStageType().name()));
            document.add(new Paragraph("Nombre d'heures par semaine premier mois : " + evaluation.getNbHeuresParSemPremierMois()));
            document.add(new Paragraph("Nombre d'heures par semaine deuxième mois : " + evaluation.getNbHeuresParSemDeuxiemeMois()));
            document.add(new Paragraph("Nombre d'heures par semaine troisième mois : " + evaluation.getNbHeuresParSemTroisiemeMois()));
            document.add(new Paragraph("Salaire : " + evaluation.getTauxHoraire()));

            document.add(new Paragraph("\nÉVALUATION").setBold().setFontSize(12));
            for (SectionEvaluation section : evaluation.getSections()) {
                document.add(new Paragraph("Évaluation").setBold().setFontSize(12));

                Table table = new Table(2);
                table.addCell("Critère");
                table.addCell("Réponse");

                for (CritereEvaluation critere : section.getCriteres()) {
                    table.addCell(critere.getQuestion());
                    table.addCell(critere.getReponse().getLabel());
                }
                document.add(table);

                document.add(new Paragraph("Commentaires : " + (section.getCommentaire().isEmpty() ? "N/A" : section.getCommentaire())));
            }

            document.add(new Paragraph("\nOBSERVATIONS GÉNÉRALES").setBold().setFontSize(12));
            document.add(new Paragraph("Milieu de stage à privilégier pour : " + evaluation.getMilieuStageAPrivilegierPour().name()));
            document.add(new Paragraph("Capacité de stagiaires : " + evaluation.getCapaciteEtudiant().name()));
            document.add(new Paragraph("Mêmes stagiaires pour prochain stage : " + (evaluation.isMemeStagiaireProchainStage() ? "OUI" : "NON")));
            document.add(new Paragraph("Quart de travail variable : " + (evaluation.isQuartTravailVariable() ? "OUI" : "NON")));
            document.add(new Paragraph("Quart de travail variable : " + evaluation.getQuartTravail1Debut() + " - " + evaluation.getQuartTravail1Fin()));
            document.add(new Paragraph("Quart de travail variable : " + evaluation.getQuartTravail2Debut() + " - " + evaluation.getQuartTravail2Fin()));
            document.add(new Paragraph("Quart de travail variable : " + evaluation.getQuartTravail3Debut() + " - " + evaluation.getQuartTravail3Fin()));

            document.add(new Paragraph("\nSIGNATURE").setBold().setFontSize(12));
            document.add(new Paragraph("Signature du Gestionnaire : " + evaluation.getSignatureSuperviseur()));
            document.add(new Paragraph("Date de signature : " + evaluation.getDateSignatureSuperviseur()));

            document.close();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PDF creation failed: " + e.getMessage());
        }
    }
}
