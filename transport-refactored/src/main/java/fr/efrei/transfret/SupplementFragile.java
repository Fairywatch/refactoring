package fr.efrei.transfret;

public class SupplementFragile extends SupplementMarchandise {

    private static final double COEFFICIENT = 1.15;

    public SupplementFragile(CalculPrix enveloppe) {
        super(enveloppe, TypeMarchandise.FRAGILE, COEFFICIENT);
    }
}
