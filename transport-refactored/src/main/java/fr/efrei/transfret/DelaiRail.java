package fr.efrei.transfret;

/**
 * Un jour de manutention, supprimé en urgence.
 */
public class DelaiRail implements DelaiTransport {

    private static final double KM_PAR_JOUR = 1440.0;
    private static final int JOURS_MANUTENTION = 1;

    @Override
    public int jours(int distanceKm, boolean urgent) {
        return (int) Math.ceil(distanceKm / KM_PAR_JOUR) + (urgent ? 0 : JOURS_MANUTENTION);
    }
}
