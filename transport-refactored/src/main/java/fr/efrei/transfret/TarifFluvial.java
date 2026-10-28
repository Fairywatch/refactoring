package fr.efrei.transfret;

public class TarifFluvial extends TarifAuKmEtAuKg {

    private static final double PRIX_KM = 0.40;
    private static final double PRIX_KG = 0.02;
    private static final double MINIMUM = 300;

    public TarifFluvial() {
        super(PRIX_KM, PRIX_KG, MINIMUM);
    }
}
