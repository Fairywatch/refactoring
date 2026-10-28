package fr.efrei.transfret;

import java.util.Optional;

/**
 * Règles d'acceptation d'une commande.
 */
public class ValidateurExpedition {

    /** @return le motif du refus, ou vide si la commande est acceptable */
    public Optional<String> verifier(Commande commande) {
        Chargement chargement = commande.chargement();
        ModeTransport mode = commande.mode();
        if (chargement.estVide()) {
            return Optional.of("au moins un colis est nécessaire");
        }
        for (Colis c : chargement.colis()) {
            if (c.getPoids() <= 0) {
                return Optional.of("poids invalide pour " + c.getDescription());
            }
        }
        for (TypeMarchandise interdite : mode.marchandisesInterdites()) {
            if (chargement.contient(interdite)) {
                return Optional.of("marchandise « " + interdite.getLibelle() + " » interdite en " + mode);
            }
        }
        if (commande.urgent() && !mode.urgenceAutorisee()) {
            return Optional.of("pas d'option urgente en " + mode);
        }
        return Optional.empty();
    }
}
