package fr.efrei.transfret;

/**
 * Tarif à la distance et au poids, avec un minimum de facturation. Chaque mode fixe ses propres valeurs.
 */
public abstract class TarifAuKmEtAuKg implements TarifTransport {

    protected static final double SANS_MINIMUM = 0;

    private final double prixKm;
    private final double prixKg;
    private final double minimum;

    protected TarifAuKmEtAuKg(double prixKm, double prixKg, double minimum) {
        this.prixKm = prixKm;
        this.prixKg = prixKg;
        this.minimum = minimum;
    }

    @Override
    public double prixDeBase(Chargement chargement, int distanceKm) {
        return Math.max(distanceKm * prixKm + chargement.poids() * prixKg, minimum);
    }
}
