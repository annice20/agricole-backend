package com.agriculture.backend.service;

import com.agriculture.backend.dto.DashboardDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class RapportRegionalService {

    private final DashboardService dashboardService;

    private static final Font TITRE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(20, 83, 45));
    private static final Font SOUS_TITRE = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
    private static final Font SECTION = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(21, 94, 56));
    private static final Font ENTETE_TABLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
    private static final Font CELLULE = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

    public RapportRegionalService(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    public byte[] genererRapportPdf(Long regionId, String nomRegion) {
        DashboardDTO dashboard = dashboardService.getDashboard(regionId);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            ajouterEntete(document, nomRegion);
            ajouterIndicateurs(document, dashboard);

            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Erreur lors de la génération du rapport PDF", e);
        }
    }

    private void ajouterEntete(Document document, String nomRegion) throws DocumentException {
        Paragraph titre = new Paragraph("Rapport Agricole Régional — " + nomRegion, TITRE);
        titre.setAlignment(Element.ALIGN_CENTER);
        document.add(titre);

        Paragraph sousTitre = new Paragraph(
                "République de Madagascar — Document généré le " +
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                SOUS_TITRE);
        sousTitre.setAlignment(Element.ALIGN_CENTER);
        sousTitre.setSpacingAfter(20);
        document.add(sousTitre);
    }

    private PdfPCell celluleEntete(String texte) {
        PdfPCell cell = new PdfPCell(new Phrase(texte, ENTETE_TABLE));
        cell.setBackgroundColor(new Color(21, 94, 56));
        cell.setPadding(6);
        return cell;
    }

    private PdfPCell celluleNormale(String texte) {
        PdfPCell cell = new PdfPCell(new Phrase(texte, CELLULE));
        cell.setPadding(5);
        return cell;
    }

    private void ajouterIndicateurs(Document document, DashboardDTO d) throws DocumentException {
        Paragraph section = new Paragraph("Indicateurs de la région", SECTION);
        section.setSpacingAfter(10);
        document.add(section);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell(celluleEntete("Indicateur"));
        table.addCell(celluleEntete("Valeur"));

        table.addCell(celluleNormale("Agriculteurs actifs"));
        table.addCell(celluleNormale(d.getAgriculteurs() + " (sur " + d.getAgriculteursTous() + " inscrits)"));

        table.addCell(celluleNormale("Programmes sollicités"));
        table.addCell(celluleNormale(String.valueOf(d.getProgrammes())));

        table.addCell(celluleNormale("Distributions effectuées"));
        table.addCell(celluleNormale(String.valueOf(d.getDistributions())));

        document.add(table);
    }
}