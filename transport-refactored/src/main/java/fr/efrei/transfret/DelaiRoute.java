package fr.efrei.transfret;

/**
 * Un conducteur roule 9 h par jour, deux conducteurs se relaient 18 h par jour en urgence.
 */
public class DelaiRoute implements DelaiTransport {

    private static final double VITESSE_KMH = 70.0;
    private static final int HEURES_CONDUITE_PAR_JOUR = 9;
    private static final int HEURES_CONDUITE_PAR_JOUR_URGENT = 18;

    @Override
    public int jours(int distanceKm, boolean urgent) {
        return (int) Math.ceil(distanceKm / VITESSE_KMH / (urgent ? HEURES_CONDUITE_PAR_JOUR_URGENT : HEURES_CONDUITE_PAR_JOUR));
    }
}
