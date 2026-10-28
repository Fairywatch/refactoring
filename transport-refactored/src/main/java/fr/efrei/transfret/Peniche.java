package fr.efrei.transfret;

import java.util.Set;

/**
 * Péniche fluviale. Le réfrigéré est refusé dès la validation (voir {@link ModeTransport#FLUVIAL}).
 */
public class Peniche extends Vehicule {

    public Peniche(String immatriculation, double capaciteMax) {
        super(immatriculation, capaciteMax, ModeTransport.FLUVIAL, Set.of());
    }

    @Override
    protected void seDeplacer(int distanceKm) {
        faireLePlein();
        System.out.println("La péniche " + getImmatriculation() + " descend le fleuve sur " + distanceKm + " km");
    }
}
