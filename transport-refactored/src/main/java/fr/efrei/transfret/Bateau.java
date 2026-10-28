package fr.efrei.transfret;

import java.util.Set;

/**
 * Porte-conteneurs, éventuellement équipé de conteneurs frigorifiques.
 */
public class Bateau extends Vehicule {

    private static final double KM_PAR_MILLE_NAUTIQUE = 1.852;

    private final boolean conteneursRefrigeres;

    public Bateau(String immatriculation, double capaciteMax, boolean conteneursRefrigeres) {
        super(immatriculation, capaciteMax, ModeTransport.MER,
                conteneursRefrigeres ? Set.of() : Set.of(TypeMarchandise.REFRIGERE));
        this.conteneursRefrigeres = conteneursRefrigeres;
    }

    @Override
    protected void seDeplacer(int distanceKm) {
        faireLePlein();
        if (conteneursRefrigeres) {
            System.out.println("Conteneurs frigorifiques réglés à " + TEMPERATURE_FROID + "°C");
        }
        System.out.println("Le navire " + getImmatriculation() + " navigue sur " + (int) (distanceKm / KM_PAR_MILLE_NAUTIQUE) + " milles nautiques");
    }
}
