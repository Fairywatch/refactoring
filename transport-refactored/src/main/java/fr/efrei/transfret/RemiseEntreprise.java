package fr.efrei.transfret;

/**
 * 10 % de remise, 15 % au-delà de 5 000 €.
 */
public class RemiseEntreprise implements PolitiqueRemise {

    private static final double SEUIL_REMISE_MAJOREE = 5000;
    private static final double COEF = 0.90;
    private static final double COEF_MAJORE = 0.85;

    @Override
    public double appliquer(double prix) {
        return prix * (prix > SEUIL_REMISE_MAJOREE ? COEF_MAJORE : COEF);
    }
}
