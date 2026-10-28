package fr.efrei.transfret;

import java.util.Map;

/**
 * Délai de livraison, en jours : celui du mode, jamais moins d'un jour.
 */
public class EstimateurDelai {

    private static final int DELAI_MINIMUM_JOURS = 1;

    private final Map<ModeTransport, DelaiTransport> delais;

    public EstimateurDelai(Map<ModeTransport, DelaiTransport> delais) {
        this.delais = Strategies.pourChaque(ModeTransport.class, delais);
    }

    public int jours(int distance, ModeTransport mode, boolean urgent) {
        return Math.max(delais.get(mode).jours(distance, urgent), DELAI_MINIMUM_JOURS);
    }
}
