package fr.efrei.transfret;

public class TarifMer extends TarifAuKmEtAuKg {

    private static final double PRIX_KM = 0.5;
    private static final double PRIX_KG = 0.03;
    private static final double MINIMUM = 500;

    public TarifMer() {
        super(PRIX_KM, PRIX_KG, MINIMUM);
    }
}
