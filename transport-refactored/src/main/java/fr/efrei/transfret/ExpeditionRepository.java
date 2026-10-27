package fr.efrei.transfret;

import java.util.Optional;

public interface ExpeditionRepository {

    /** Enregistre l'expédition, ou la met à jour si elle existe déjà. */
    void sauvegarder(Expedition expedition);

    Optional<Expedition> trouver(String id);
}
