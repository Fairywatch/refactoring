package fr.efrei.transfret;

public class TarifRoute implements TarifTransport {

    private static final double PRIX_KM = 1.2;
    private static final double PRIX_KG = 0.10;

    @Override
    public double prixDeBase(Chargement chargement, int distanceKm) {
        return distanceKm * PRIX_KM + chargement.poids() * PRIX_KG;
    }
}
