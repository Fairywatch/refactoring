package fr.efrei.transfret;

import java.util.EnumSet;

/**
 * Ne transporte que des produits réfrigérés. Ce n'est plus un {@link Camion} : il refuserait des chargements
 * qu'un camion accepte, et ne pourrait donc pas le remplacer partout (Liskov).
 */
public class CamionFrigorifique extends Vehicule {

    public CamionFrigorifique(String immatriculation, double capaciteMax) {
        super(immatriculation, capaciteMax, ModeTransport.ROUTE, EnumSet.complementOf(EnumSet.of(TypeMarchandise.REFRIGERE)));
    }

    @Override
    protected void seDeplacer(int distanceKm) {
        faireLePlein();
        System.out.println("Réfrigération activée à " + TEMPERATURE_FROID + "°C sur " + getImmatriculation());
        System.out.println("Le camion frigorifique " + getImmatriculation() + " roule sur " + distanceKm + " km");
    }
}
