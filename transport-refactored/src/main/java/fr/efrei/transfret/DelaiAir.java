package fr.efrei.transfret;

/**
 * Livré le lendemain, sauf long-courrier (deux jours) ; toujours le lendemain en urgence.
 */
public class DelaiAir implements DelaiTransport {

    private static final int SEUIL_LONG_COURRIER_KM = 3000;
    private static final int JOURS_COURT = 1;
    private static final int JOURS_LONG_COURRIER = 2;

    @Override
    public int jours(int distanceKm, boolean urgent) {
        return !urgent && distanceKm > SEUIL_LONG_COURRIER_KM ? JOURS_LONG_COURRIER : JOURS_COURT;
    }
}
