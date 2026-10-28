package fr.efrei.transfret;

/**
 * Majoration appliquée une fois si le chargement contient au moins un colis du type donné.
 */
public abstract class SupplementMarchandise extends DecorateurPrix {

    private final TypeMarchandise type;
    private final double coefficient;

    protected SupplementMarchandise(CalculPrix enveloppe, TypeMarchandise type, double coefficient) {
        super(enveloppe);
        this.type = type;
        this.coefficient = coefficient;
    }

    @Override
    protected double ajuster(double prix, DemandeTarif demande) {
        return demande.chargement().contient(type) ? prix * coefficient : prix;
    }
}
