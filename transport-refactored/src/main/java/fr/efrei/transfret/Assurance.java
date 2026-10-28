package fr.efrei.transfret;

/**
 * 2 % de la valeur déclarée, 15 € minimum, ajoutés au prix.
 */
public class Assurance extends DecorateurPrix {

    private static final double TAUX = 0.02;
    private static final double MINIMUM = 15;

    public Assurance(CalculPrix enveloppe) {
        super(enveloppe);
    }

    @Override
    protected double ajuster(double prix, DemandeTarif demande) {
        if (!demande.assurance()) {
            return prix;
        }
        return prix + Math.max(demande.chargement().valeurDeclaree() * TAUX, MINIMUM);
    }
}
