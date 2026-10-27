package fr.efrei.transfret;

public class GenerateurIdentifiant {

    private int dernier;

    public String suivant() {
        dernier++;
        return "EXP-" + dernier;
    }
}
