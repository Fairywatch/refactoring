package fr.efrei.transfret;

public final class Montants {

    public static final double MULTIPLICATEUR_TTC = 1.2;
    private static final double CENTIMES_PAR_EURO = 100.0;

    private Montants() {
    }

    public static double arrondir(double montant) {
        return Math.round(montant * CENTIMES_PAR_EURO) / CENTIMES_PAR_EURO;
    }

    public static double ttc(double montantHT) {
        return arrondir(montantHT * MULTIPLICATEUR_TTC);
    }
}
