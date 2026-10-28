package fr.efrei.transfret;

import java.util.Set;

/**
 * Camion bâché : pas de froid, matières dangereuses seulement avec la certification ADR.
 */
public class Camion extends Vehicule {

    private static final Set<TypeMarchandise> REFUSEES = Set.of(TypeMarchandise.REFRIGERE, TypeMarchandise.DANGEREUX);
    private static final Set<TypeMarchandise> REFUSEES_ADR = Set.of(TypeMarchandise.REFRIGERE);

    public Camion(String immatriculation, double capaciteMax, boolean certifieAdr) {
        super(immatriculation, capaciteMax, ModeTransport.ROUTE, certifieAdr ? REFUSEES_ADR : REFUSEES);
    }

    @Override
    protected void seDeplacer(int distanceKm) {
        faireLePlein();
        System.out.println("Le camion " + getImmatriculation() + " roule sur " + distanceKm + " km");
    }
}
