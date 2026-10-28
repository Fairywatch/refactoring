package fr.efrei.transfret;

public class TarifRail implements TarifTransport {

    private static final double PRIX_KM = 0.8;
    private static final double PRIX_KG = 0.07;
    private static final double MINIMUM = 150;

    @Override
    public double prixDeBase(Chargement chargement, int distanceKm) {
        return Math.max(distanceKm * PRIX_KM + chargement.poids() * PRIX_KG, MINIMUM);
    }
}
