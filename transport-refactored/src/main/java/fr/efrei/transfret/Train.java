package fr.efrei.transfret;

/**
 * Train électrique : pas de plein à faire.
 */
public class Train extends Vehicule {

    public Train(String immatriculation, double capaciteMax) {
        super(immatriculation, capaciteMax, ModeTransport.RAIL);
    }

    @Override
    protected boolean accepte(Chargement colis) {
        return true;
    }

    @Override
    protected void seDeplacer(int distanceKm) {
        System.out.println("Le train " + getImmatriculation() + " circule sur " + distanceKm + " km de voies ferrées");
    }
}
