package fr.efrei.transfret;

/**
 * Strategy : le prix de base d'un transport, avant suppléments, remise et assurance.
 */
public interface TarifTransport {

    double prixDeBase(Chargement chargement, int distanceKm);
}
