package fr.efrei.transfret;

import java.util.Optional;

/**
 * Règles d'acceptation d'un nouveau client.
 */
public class ValidateurClient {

    private static final int LONGUEUR_SIRET = 14;

    /** @return le motif du refus, ou vide si le client est acceptable */
    public Optional<String> verifier(Client client) {
        String email = client.getEmail();
        String siret = client.getSiret();
        if (client.getNom() == null || client.getNom().isBlank()) {
            return Optional.of("nom obligatoire");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            return Optional.of("email invalide (" + email + ")");
        }
        if (client.getType() == null) {
            return Optional.of("type de client inconnu");
        }
        if (client.getType().siretObligatoire() && (siret == null || siret.length() != LONGUEUR_SIRET)) {
            return Optional.of("SIRET obligatoire (14 chiffres) pour un client " + client.getType());
        }
        return Optional.empty();
    }
}
