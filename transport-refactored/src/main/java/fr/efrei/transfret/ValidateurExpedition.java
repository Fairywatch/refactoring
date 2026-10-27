package fr.efrei.transfret;

import java.util.Optional;

/**
 * Règles d'acceptation d'une demande d'expédition.
 */
public class ValidateurExpedition {

    /** @return le motif du refus, ou vide si la demande est acceptable */
    public Optional<String> verifier(Client client, Chargement chargement, ModeTransport mode, boolean urgent) {
        if (client == null) {
            return Optional.of("client obligatoire");
        }
        if (chargement.estVide()) {
            return Optional.of("au moins un colis est nécessaire");
        }
        for (Colis c : chargement.colis()) {
            if (c.getPoids() <= 0) {
                return Optional.of("poids invalide pour " + c.getDescription());
            }
        }
        if (chargement.contient(TypeMarchandise.DANGEREUX) && mode == ModeTransport.AIR) {
            return Optional.of("les matières dangereuses sont interdites en transport aérien");
        }
        if (chargement.contient(TypeMarchandise.REFRIGERE) && !(mode == ModeTransport.ROUTE || mode == ModeTransport.MER)) {
            return Optional.of("le transport réfrigéré n'est possible que par route ou par mer");
        }
        if (urgent && mode == ModeTransport.MER) {
            return Optional.of("pas d'option urgente pour le transport maritime");
        }
        return Optional.empty();
    }
}
