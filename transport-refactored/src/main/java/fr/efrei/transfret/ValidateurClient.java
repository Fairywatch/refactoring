package fr.efrei.transfret;

import java.util.Optional;

/**
 * Règles d'acceptation d'un nouveau client.
 */
public class ValidateurClient {

    private static final int LONGUEUR_SIRET = 14;

    /** @return le motif du refus, ou vide si le client est acceptable */
    public Optional<String> verifier(String nom, String email, TypeClient type, String siret) {
        if (nom == null || nom.isBlank()) {
            return Optional.of("nom obligatoire");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            return Optional.of("email invalide (" + email + ")");
        }
        if (type == null) {
            return Optional.of("type de client inconnu");
        }
        if (type == TypeClient.ENTREPRISE && (siret == null || siret.length() != LONGUEUR_SIRET)) {
            return Optional.of("SIRET obligatoire (14 chiffres) pour une entreprise");
        }
        return Optional.empty();
    }
}
