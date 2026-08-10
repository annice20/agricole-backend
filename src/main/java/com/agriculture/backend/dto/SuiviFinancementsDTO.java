package com.agriculture.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class SuiviFinancementsDTO {
    private List<RepartitionOrganismeDTO> parOrganisme;
    private List<SuiviProgrammeDTO> parProgramme;
    private List<EvolutionMensuelleDTO> evolutionMensuelle;
    private BigDecimal montantTotalGlobal;
    private long nombreOrganismes;

    public List<RepartitionOrganismeDTO> getParOrganisme() { return parOrganisme; }
    public void setParOrganisme(List<RepartitionOrganismeDTO> parOrganisme) { this.parOrganisme = parOrganisme; }
    public List<SuiviProgrammeDTO> getParProgramme() { return parProgramme; }
    public void setParProgramme(List<SuiviProgrammeDTO> parProgramme) { this.parProgramme = parProgramme; }
    public List<EvolutionMensuelleDTO> getEvolutionMensuelle() { return evolutionMensuelle; }
    public void setEvolutionMensuelle(List<EvolutionMensuelleDTO> evolutionMensuelle) { this.evolutionMensuelle = evolutionMensuelle; }
    public BigDecimal getMontantTotalGlobal() { return montantTotalGlobal; }
    public void setMontantTotalGlobal(BigDecimal montantTotalGlobal) { this.montantTotalGlobal = montantTotalGlobal; }
    public long getNombreOrganismes() { return nombreOrganismes; }
    public void setNombreOrganismes(long nombreOrganismes) { this.nombreOrganismes = nombreOrganismes; }
}