package fr.efrei.transfret;

import java.util.Map;

/**
 * Le cœur de la chaîne de prix : délègue à la stratégie tarifaire du mode demandé.
 */
public class TarifSelonMode implements CalculPrix {

    private final Map<ModeTransport, TarifTransport> tarifs;

    public TarifSelonMode(Map<ModeTransport, TarifTransport> tarifs) {
        this.tarifs = Strategies.pourChaque(ModeTransport.class, tarifs);
    }

    @Override
    public double calculer(DemandeTarif demande) {
        return tarifs.get(demande.mode()).prixDeBase(demande.chargement(), demande.distanceKm());
    }
}
