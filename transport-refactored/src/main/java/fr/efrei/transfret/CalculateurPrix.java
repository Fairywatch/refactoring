package fr.efrei.transfret;

import java.util.Map;

public class CalculateurPrix {

    private static final double COEF_FRAGILE = 1.15;
    private static final double COEF_DANGEREUX = 1.30;
    private static final double COEF_REFRIGERE = 1.20;
    private static final double COEF_URGENT = 1.5;

    private static final double TAUX_ASSURANCE = 0.02;
    private static final double ASSURANCE_MINIMUM = 15;

    private final Map<ModeTransport, TarifTransport> tarifs;
    private final Map<TypeClient, PolitiqueRemise> remises;

    public CalculateurPrix(Map<ModeTransport, TarifTransport> tarifs, Map<TypeClient, PolitiqueRemise> remises) {
        this.tarifs = Strategies.pourChaque(ModeTransport.class, tarifs);
        this.remises = Strategies.pourChaque(TypeClient.class, remises);
    }

    public double calculerPrixHT(Chargement chargement, int distance, ModeTransport mode, boolean urgent, boolean assurance, TypeClient typeClient) {
        double prix = tarifs.get(mode).prixDeBase(chargement, distance);

        // suppléments
        if (chargement.contient(TypeMarchandise.FRAGILE)) prix = prix * COEF_FRAGILE;
        if (chargement.contient(TypeMarchandise.DANGEREUX)) prix = prix * COEF_DANGEREUX;
        if (chargement.contient(TypeMarchandise.REFRIGERE)) prix = prix * COEF_REFRIGERE;
        if (urgent) prix = prix * COEF_URGENT;

        prix = remises.get(typeClient).appliquer(prix);

        // assurance
        if (assurance) {
            prix = prix + Math.max(chargement.valeurDeclaree() * TAUX_ASSURANCE, ASSURANCE_MINIMUM);
        }
        return Montants.arrondir(prix);
    }
}
