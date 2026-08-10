package com.agriculture.backend.service;

import com.agriculture.backend.dto.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class RapportNationalService {

    private final DashboardService dashboardService;
    private final RepartitionAidesService repartitionAidesService;
    private final FinancementSuiviService financementSuiviService;
    private final AnalyseRegionaleService analyseRegionaleService;

    private static final Font TITRE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(20, 83, 45));
    private static final Font SOUS_TITRE = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
    private static final Font SECTION = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(21, 94, 56));
    private static final Font SOUS_SECTION = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
    private static final Font ENTETE_TABLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
    private static final Font CELLULE = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

    public RapportNationalService(DashboardService dashboardService,
                                   RepartitionAidesService repartitionAidesService,
                                   FinancementSuiviService financementSuiviService,
                                   AnalyseRegionaleService analyseRegionaleService) {
        this.dashboardService = dashboardService;
        this.repartitionAidesService = repartitionAidesService;
        this.financementSuiviService = financementSuiviService;
        this.analyseRegionaleService = analyseRegionaleService;
    }

    public byte[] genererRapportPdf() {
    	DashboardDTO dashboard = dashboardService.getDashboard(null); // vue nationale complète
        RepartitionAidesDTO repartition = repartitionAidesService.getRepartition();
        SuiviFinancementsDTO financements = financementSuiviService.getSuivi();
        AnalyseRegionaleDTO regional = analyseRegionaleService.getAnalyse();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            ajouterEntete(document);
            ajouterIndicateursGeneraux(document, dashboard);
            ajouterRepartitionAides(document, repartition);
            ajouterSuiviFinancements(document, financements);
            ajouterAnalyseRegionale(document, regional);

            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Erreur lors de la génération du rapport PDF", e);
        }
    }

    private void ajouterEntete(Document document) throws DocumentException {
        Paragraph titre = new Paragraph("Rapport Agricole National", TITRE);
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

    private Paragraph titreSection(String texte) {
        Paragraph p = new Paragraph(texte, SECTION);
        p.setSpacingBefore(15);
        p.setSpacingAfter(8);
        return p;
    }

    private Paragraph sousTitreSection(String texte) {
        Paragraph p = new Paragraph(texte, SOUS_SECTION);
        p.setSpacingBefore(10);
        p.setSpacingAfter(6);
        return p;
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

    private void ajouterIndicateursGeneraux(Document document, DashboardDTO d) throws DocumentException {
        document.add(titreSection("1. Indicateurs Généraux"));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell(celluleEntete("Indicateur"));
        table.addCell(celluleEntete("Valeur"));

        table.addCell(celluleNormale("Agriculteurs actifs"));
        table.addCell(celluleNormale(d.getAgriculteurs() + " (sur " + d.getAgriculteursTous() + " inscrits)"));

        table.addCell(celluleNormale("Programmes d'aide"));
        table.addCell(celluleNormale(String.valueOf(d.getProgrammes())));

        table.addCell(celluleNormale("Distributions effectuées"));
        table.addCell(celluleNormale(String.valueOf(d.getDistributions())));

        table.addCell(celluleNormale("Montant total des financements"));
        table.addCell(celluleNormale(String.format("%,.0f Ariary", d.getMontantTotalFinancement())));

        document.add(table);
    }

    private void ajouterRepartitionAides(Document document, RepartitionAidesDTO r) throws DocumentException {
        document.add(titreSection("2. Répartition des Aides"));

        Paragraph resume = new Paragraph(
                r.getTotalDistributions() + " distribution(s) pour un montant total de " +
                        String.format("%,.0f Ariary", r.getMontantTotalDistribue()), CELLULE);
        resume.setSpacingAfter(8);
        document.add(resume);

        PdfPTable tableType = new PdfPTable(3);
        tableType.setWidthPercentage(100);
        tableType.addCell(celluleEntete("Type d'aide"));
        tableType.addCell(celluleEntete("Nombre"));
        tableType.addCell(celluleEntete("Montant"));
        for (RepartitionTypeAideDTO item : r.getParType()) {
            tableType.addCell(celluleNormale(item.getTypeAide().toString()));
            tableType.addCell(celluleNormale(String.valueOf(item.getNombre())));
            tableType.addCell(celluleNormale(String.format("%,.0f Ar", item.getMontantTotal())));
        }
        document.add(tableType);

        document.add(sousTitreSection("Par statut de demande"));

        PdfPTable tableStatut = new PdfPTable(2);
        tableStatut.setWidthPercentage(100);
        tableStatut.addCell(celluleEntete("Statut"));
        tableStatut.addCell(celluleEntete("Nombre"));
        for (RepartitionStatutDTO item : r.getParStatut()) {
            tableStatut.addCell(celluleNormale(item.getStatut().toString()));
            tableStatut.addCell(celluleNormale(String.valueOf(item.getNombre())));
        }
        document.add(tableStatut);
    }

    private void ajouterSuiviFinancements(Document document, SuiviFinancementsDTO f) throws DocumentException {
        document.add(titreSection("3. Suivi des Financements"));

        Paragraph resume = new Paragraph(
                f.getNombreOrganismes() + " organisme(s) financeur(s), montant total : " +
                        String.format("%,.0f Ariary", f.getMontantTotalGlobal()), CELLULE);
        resume.setSpacingAfter(8);
        document.add(resume);

        PdfPTable tableOrganisme = new PdfPTable(3);
        tableOrganisme.setWidthPercentage(100);
        tableOrganisme.addCell(celluleEntete("Organisme"));
        tableOrganisme.addCell(celluleEntete("Nombre"));
        tableOrganisme.addCell(celluleEntete("Montant"));
        for (RepartitionOrganismeDTO item : f.getParOrganisme()) {
            tableOrganisme.addCell(celluleNormale(item.getOrganisme() != null ? item.getOrganisme() : "Non précisé"));
            tableOrganisme.addCell(celluleNormale(String.valueOf(item.getNombre())));
            tableOrganisme.addCell(celluleNormale(String.format("%,.0f Ar", item.getMontantTotal())));
        }
        document.add(tableOrganisme);

        document.add(sousTitreSection("Couverture budgétaire par programme"));

        PdfPTable tableProgramme = new PdfPTable(4);
        tableProgramme.setWidthPercentage(100);
        tableProgramme.addCell(celluleEntete("Programme"));
        tableProgramme.addCell(celluleEntete("Budget"));
        tableProgramme.addCell(celluleEntete("Financé"));
        tableProgramme.addCell(celluleEntete("Couverture"));
        for (SuiviProgrammeDTO item : f.getParProgramme()) {
            tableProgramme.addCell(celluleNormale(item.getTitreProgramme()));
            tableProgramme.addCell(celluleNormale(String.format("%,.0f Ar", item.getBudget())));
            tableProgramme.addCell(celluleNormale(String.format("%,.0f Ar", item.getMontantFinance())));
            tableProgramme.addCell(celluleNormale(String.format("%.1f%%", item.getTauxCouverture())));
        }
        document.add(tableProgramme);
    }

    private void ajouterAnalyseRegionale(Document document, AnalyseRegionaleDTO r) throws DocumentException {
        document.add(titreSection("4. Analyse Régionale"));

        Paragraph resume = new Paragraph(
                "Région la plus active : " + (r.getRegionPlusActifs() != null ? r.getRegionPlusActifs() : "—") +
                        "  |  Région avec le plus de distributions : " +
                        (r.getRegionPlusDistributions() != null ? r.getRegionPlusDistributions() : "—"),
                CELLULE);
        resume.setSpacingAfter(8);
        document.add(resume);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.addCell(celluleEntete("Région"));
        table.addCell(celluleEntete("Actifs"));
        table.addCell(celluleEntete("Distributions"));
        table.addCell(celluleEntete("Montant distribué"));
        table.addCell(celluleEntete("Moy. / actif"));

        for (RegionAnalyseDTO region : r.getRegions()) {
            table.addCell(celluleNormale(region.getNomRegion()));
            table.addCell(celluleNormale(String.valueOf(region.getTotalAgriculteursActifs())));
            table.addCell(celluleNormale(String.valueOf(region.getNombreDistributions())));
            table.addCell(celluleNormale(String.format("%,.0f Ar", region.getMontantDistribue())));
            table.addCell(celluleNormale(String.format("%,.0f Ar", region.getMontantMoyenParActif())));
        }
        document.add(table);
    }
}