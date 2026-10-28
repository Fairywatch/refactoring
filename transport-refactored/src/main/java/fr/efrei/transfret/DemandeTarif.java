package fr.efrei.transfret;

/**
 * Tout ce qui influe sur le prix d'un transport.
 */
public record DemandeTarif(Chargement chargement, int distanceKm, ModeTransport mode, boolean urgent,
                           boolean assurance, TypeClient typeClient) {
}
