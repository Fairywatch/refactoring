package fr.efrei.transfret;

public class SupplementUrgence extends DecorateurPrix {

    private static final double COEFFICIENT = 1.5;

    public SupplementUrgence(CalculPrix enveloppe) {
        super(enveloppe);
    }

    @Override
    protected double ajuster(double prix, DemandeTarif demande) {
        return demande.urgent() ? prix * COEFFICIENT : prix;
    }
}
