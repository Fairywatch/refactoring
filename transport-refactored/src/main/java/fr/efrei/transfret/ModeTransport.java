package fr.efrei.transfret;

import java.util.Set;

/**
 * Les modes de transport et leurs restrictions. Le tarif et le délai de chaque mode sont des stratégies
 * injectées par {@link TransFret}.
 */
public enum ModeTransport {
    ROUTE(Set.of(), true),
    RAIL(Set.of(TypeMarchandise.REFRIGERE), true),
    MER(Set.of(), false),
    AIR(Set.of(TypeMarchandise.DANGEREUX, TypeMarchandise.REFRIGERE), true);

    private final Set<TypeMarchandise> marchandisesInterdites;
    private final boolean urgenceAutorisee;

    ModeTransport(Set<TypeMarchandise> marchandisesInterdites, boolean urgenceAutorisee) {
        this.marchandisesInterdites = marchandisesInterdites;
        this.urgenceAutorisee = urgenceAutorisee;
    }

    public Set<TypeMarchandise> marchandisesInterdites() {
        return marchandisesInterdites;
    }

    public boolean urgenceAutorisee() {
        return urgenceAutorisee;
    }
}
