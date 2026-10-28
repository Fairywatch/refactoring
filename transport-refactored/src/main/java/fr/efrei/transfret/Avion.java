package fr.efrei.transfret;

/**
 * Avion cargo. Les marchandises interdites en aérien sont refusées dès la validation (voir {@link ModeTransport#AIR}).
 */
public class Avion extends Vehicule {

    private static final int ROULAGE_PISTE_KM = 2;
    private static final int ALTITUDE_CROISIERE_PIEDS = 35000;

    public Avion(String immatriculation, double capaciteMax) {
        super(immatriculation, capaciteMax, ModeTransport.AIR);
    }

    @Override
    protected boolean accepte(Chargement colis) {
        return true;
    }

    @Override
    protected void seDeplacer(int distanceKm) {
        faireLePlein();
        System.out.println("L'avion " + getImmatriculation() + " roule sur " + ROULAGE_PISTE_KM + " km de piste");
        System.out.println("L'avion " + getImmatriculation() + " vole à " + ALTITUDE_CROISIERE_PIEDS + " pieds sur " + distanceKm + " km");
    }
}
