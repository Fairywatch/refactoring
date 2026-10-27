package fr.efrei.transfret;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalInt;

/**
 * Distances entre villes, dans les deux sens, sans tenir compte de la casse.
 */
public class TableDistances {

    private final Map<String, Integer> distances = new HashMap<>();

    public TableDistances ajouter(String villeA, String villeB, int km) {
        distances.put(cle(villeA, villeB), km);
        distances.put(cle(villeB, villeA), km);
        return this;
    }

    public OptionalInt distance(String villeA, String villeB) {
        Integer km = distances.get(cle(villeA, villeB));
        return km == null ? OptionalInt.empty() : OptionalInt.of(km);
    }

    private static String cle(String villeA, String villeB) {
        return villeA.toUpperCase(Locale.ROOT) + "->" + villeB.toUpperCase(Locale.ROOT);
    }
}
