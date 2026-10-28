package fr.efrei.transfret;

/**
 * 360 km par jour, plus un jour d'éclusage et de manutention. Pas d'urgence en fluvial (voir {@link ModeTransport#FLUVIAL}).
 */
public class DelaiFluvial implements DelaiTransport {

    private static final double KM_PAR_JOUR = 360.0;
    private static final int JOURS_MANUTENTION = 1;

    @Override
    public int jours(int distanceKm, boolean urgent) {
        return (int) Math.ceil(distanceKm / KM_PAR_JOUR) + JOURS_MANUTENTION;
    }
}
