package fr.efrei.transfret;

/**
 * +10 % dès qu'un colis dépasse 200 cm sur une de ses dimensions.
 */
public class SupplementHorsGabarit extends DecorateurPrix {

    private static final double DIMENSION_MAX_CM = 200;
    private static final double COEFFICIENT = 1.10;

    public SupplementHorsGabarit(CalculPrix enveloppe) {
        super(enveloppe);
    }

    @Override
    protected double ajuster(double prix, DemandeTarif demande) {
        boolean horsGabarit = demande.chargement().colis().stream()
                .anyMatch(c -> c.getPlusGrandeDimension() > DIMENSION_MAX_CM);
        return horsGabarit ? prix * COEFFICIENT : prix;
    }
}
