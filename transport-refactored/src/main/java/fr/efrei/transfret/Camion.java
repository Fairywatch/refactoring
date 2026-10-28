package fr.efrei.transfret;

/**
 * Camion bâché : pas de froid, matières dangereuses seulement avec la certification ADR.
 */
public class Camion extends Vehicule {

    private final boolean certifieAdr;

    public Camion(String immatriculation, double capaciteMax, boolean certifieAdr) {
        super(immatriculation, capaciteMax, ModeTransport.ROUTE);
        this.certifieAdr = certifieAdr;
    }

    @Override
    protected boolean accepte(Chargement colis) {
        return !colis.contient(TypeMarchandise.REFRIGERE) && (certifieAdr || !colis.contient(TypeMarchandise.DANGEREUX));
    }

    @Override
    protected void seDeplacer(int distanceKm) {
        faireLePlein();
        System.out.println("Le camion " + getImmatriculation() + " roule sur " + distanceKm + " km");
    }
}
