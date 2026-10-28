package fr.efrei.transfret;

/**
 * Prix HT au centime, calculé par la chaîne de décorateurs assemblée dans {@link TransFret}.
 */
public class CalculateurPrix {

    private final CalculPrix chaine;

    public CalculateurPrix(CalculPrix chaine) {
        this.chaine = chaine;
    }

    public double calculerPrixHT(DemandeTarif demande) {
        return Montants.arrondir(chaine.calculer(demande));
    }
}
