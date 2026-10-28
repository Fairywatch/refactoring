package fr.efrei.transfret;

public class TarifMer implements TarifTransport {

    private static final double PRIX_KM = 0.5;
    private static final double PRIX_KG = 0.03;
    private static final double MINIMUM = 500;

    @Override
    public double prixDeBase(Chargement chargement, int distanceKm) {
        return Math.max(distanceKm * PRIX_KM + chargement.poids() * PRIX_KG, MINIMUM);
    }
}
