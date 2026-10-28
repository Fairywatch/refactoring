package fr.efrei.transfret;

import java.util.EnumMap;
import java.util.Map;

final class Strategies {

    private Strategies() {
    }

    /**
     * Copie la table et vérifie dès l'assemblage qu'elle a une stratégie pour chaque valeur de l'enum :
     * un oubli échoue au démarrage plutôt qu'au premier calcul.
     */
    static <E extends Enum<E>, S> Map<E, S> pourChaque(Class<E> type, Map<E, S> strategies) {
        Map<E, S> copie = new EnumMap<>(type);
        copie.putAll(strategies);
        for (E valeur : type.getEnumConstants()) {
            if (copie.get(valeur) == null) {
                throw new IllegalArgumentException("aucune stratégie pour " + valeur);
            }
        }
        return copie;
    }
}
