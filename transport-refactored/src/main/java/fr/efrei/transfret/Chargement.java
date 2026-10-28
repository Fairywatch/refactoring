package fr.efrei.transfret;

import java.util.List;

/**
 * Les colis d'une expédition, et ce qu'on en déduit : poids, volume, valeur, types de marchandise.
 */
public class Chargement {

    private final List<Colis> colis;

    public Chargement(List<Colis> colis) {
        this.colis = colis == null ? List.of() : List.copyOf(colis);
    }

    public List<Colis> colis() {
        return colis;
    }

    public boolean estVide() {
        return colis.isEmpty();
    }

    // boucles simples plutôt que DoubleStream.sum() : même ordre d'addition, donc mêmes arrondis qu'avant
    public double poids() {
        double total = 0;
        for (Colis c : colis) {
            total = total + c.getPoids();
        }
        return total;
    }

    public double volume() {
        double total = 0;
        for (Colis c : colis) {
            total = total + c.getVolume();
        }
        return total;
    }

    public double valeurDeclaree() {
        double total = 0;
        for (Colis c : colis) {
            total = total + c.getValeurDeclaree();
        }
        return total;
    }

    public boolean contient(TypeMarchandise type) {
        return colis.stream().anyMatch(c -> c.getType() == type);
    }

    /** @return {@code true} si le chargement n'est pas vide et que tous ses colis sont de ce type */
    public boolean uniquement(TypeMarchandise type) {
        return !colis.isEmpty() && colis.stream().allMatch(c -> c.getType() == type);
    }
}
