package fr.efrei.transfret;

/**
 * En aérien, on facture le plus grand du poids réel et du poids volumétrique.
 */
public class TarifAir implements TarifTransport {

    private static final double PRIX_KM = 0.6;
    private static final double PRIX_KG = 4.5;
    private static final double KG_PAR_M3 = 167;

    @Override
    public double prixDeBase(Chargement chargement, int distanceKm) {
        double poidsTaxable = Math.max(chargement.poids(), chargement.volume() * KG_PAR_M3);
        return distanceKm * PRIX_KM + poidsTaxable * PRIX_KG;
    }
}
