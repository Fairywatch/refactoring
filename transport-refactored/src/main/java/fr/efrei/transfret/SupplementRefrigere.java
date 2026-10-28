package fr.efrei.transfret;

public class SupplementRefrigere extends SupplementMarchandise {

    private static final double COEFFICIENT = 1.20;

    public SupplementRefrigere(CalculPrix enveloppe) {
        super(enveloppe, TypeMarchandise.REFRIGERE, COEFFICIENT);
    }
}
