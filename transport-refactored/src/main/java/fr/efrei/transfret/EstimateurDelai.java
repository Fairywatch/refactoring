package fr.efrei.transfret;

/**
 * Délai de livraison, en jours, selon la distance et le mode.
 */
public class EstimateurDelai {

    private static final double VITESSE_ROUTE_KMH = 70.0;
    private static final int HEURES_CONDUITE_PAR_JOUR = 9;
    private static final int HEURES_CONDUITE_PAR_JOUR_URGENT = 18;
    private static final double RAIL_KM_PAR_JOUR = 1440.0;
    private static final int RAIL_JOURS_MANUTENTION = 1;
    private static final double MER_KM_PAR_JOUR = 720.0;
    private static final int MER_JOURS_PORTUAIRES = 2;
    private static final int AIR_SEUIL_LONG_COURRIER_KM = 3000;
    private static final int AIR_JOURS_LONG_COURRIER = 2;
    private static final int DELAI_MINIMUM_JOURS = 1;

    public int jours(int distance, ModeTransport mode, boolean urgent) {
        int jours = switch (mode) {
            case ROUTE -> (int) Math.ceil(distance / VITESSE_ROUTE_KMH
                    / (urgent ? HEURES_CONDUITE_PAR_JOUR_URGENT : HEURES_CONDUITE_PAR_JOUR));
            case RAIL -> (int) Math.ceil(distance / RAIL_KM_PAR_JOUR) + (urgent ? 0 : RAIL_JOURS_MANUTENTION);
            case MER -> (int) Math.ceil(distance / MER_KM_PAR_JOUR) + MER_JOURS_PORTUAIRES;
            case AIR -> !urgent && distance > AIR_SEUIL_LONG_COURRIER_KM ? AIR_JOURS_LONG_COURRIER : DELAI_MINIMUM_JOURS;
        };
        return Math.max(jours, DELAI_MINIMUM_JOURS);
    }
}
