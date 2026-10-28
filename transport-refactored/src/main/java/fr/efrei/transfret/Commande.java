package fr.efrei.transfret;

/**
 * Ce que le client demande : quoi, d'où, vers où, comment.
 */
public record Commande(Chargement chargement, String villeDepart, String villeArrivee, ModeTransport mode,
                       boolean urgent, boolean assurance) {
}
