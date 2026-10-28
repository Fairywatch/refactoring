package fr.efrei.transfret;

public class SupplementDangereux extends SupplementMarchandise {

    private static final double COEFFICIENT = 1.30;

    public SupplementDangereux(CalculPrix enveloppe) {
        super(enveloppe, TypeMarchandise.DANGEREUX, COEFFICIENT);
    }
}
