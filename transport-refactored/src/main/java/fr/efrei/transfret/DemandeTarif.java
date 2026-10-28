package fr.efrei.transfret;

/**
 * Tout ce qui influe sur le prix d'un transport.
 */
public record DemandeTarif(Commande commande, int distanceKm, TypeClient typeClient) {

    public Chargement chargement() {
        return commande.chargement();
    }

    public ModeTransport mode() {
        return commande.mode();
    }

    public boolean urgent() {
        return commande.urgent();
    }

    public boolean assurance() {
        return commande.assurance();
    }
}
