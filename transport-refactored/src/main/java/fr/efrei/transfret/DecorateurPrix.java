package fr.efrei.transfret;

/**
 * Decorator : enveloppe un calcul de prix et ajuste le résultat.
 */
public abstract class DecorateurPrix implements CalculPrix {

    private final CalculPrix enveloppe;

    protected DecorateurPrix(CalculPrix enveloppe) {
        this.enveloppe = enveloppe;
    }

    @Override
    public final double calculer(DemandeTarif demande) {
        return ajuster(enveloppe.calculer(demande), demande);
    }

    /** @param prix le prix calculé par le composant enveloppé */
    protected abstract double ajuster(double prix, DemandeTarif demande);
}
