package fr.efrei.transfret;

/**
 * Strategy : le délai de livraison d'un mode, en jours.
 */
public interface DelaiTransport {

    int jours(int distanceKm, boolean urgent);
}
