package fr.efrei.transfret;

import java.util.Set;

/**
 * Train électrique : pas de plein à faire. Le réfrigéré est refusé dès la validation (voir {@link ModeTransport#RAIL}).
 */
public class Train extends Vehicule {

    public Train(String immatriculation, double capaciteMax) {
        super(immatriculation, capaciteMax, ModeTransport.RAIL, Set.of());
    }

    @Override
    protected void seDeplacer(int distanceKm) {
        System.out.println("Le train " + getImmatriculation() + " circule sur " + distanceKm + " km de voies ferrées");
    }
}
