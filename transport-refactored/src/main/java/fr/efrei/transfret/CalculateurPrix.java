package fr.efrei.transfret;

public class CalculateurPrix {

    private static final double ROUTE_PRIX_KM = 1.2;
    private static final double ROUTE_PRIX_KG = 0.10;
    private static final double RAIL_PRIX_KM = 0.8;
    private static final double RAIL_PRIX_KG = 0.07;
    private static final double RAIL_MINIMUM = 150;
    private static final double MER_PRIX_KM = 0.5;
    private static final double MER_PRIX_KG = 0.03;
    private static final double MER_MINIMUM = 500;
    private static final double AIR_PRIX_KM = 0.6;
    private static final double AIR_PRIX_KG = 4.5;
    private static final double AIR_KG_PAR_M3 = 167;

    private static final double COEF_FRAGILE = 1.15;
    private static final double COEF_DANGEREUX = 1.30;
    private static final double COEF_REFRIGERE = 1.20;
    private static final double COEF_URGENT = 1.5;

    private static final double SEUIL_REMISE_ENTREPRISE_MAJOREE = 5000;
    private static final double COEF_ENTREPRISE = 0.90;
    private static final double COEF_ENTREPRISE_MAJOREE = 0.85;
    private static final double COEF_PREMIUM = 0.80;

    private static final double TAUX_ASSURANCE = 0.02;
    private static final double ASSURANCE_MINIMUM = 15;

    public double calculerPrixHT(Chargement chargement, int distance, ModeTransport mode, boolean urgent, boolean assurance, TypeClient typeClient) {
        double poids = chargement.poids();
        double volume = chargement.volume();
        double valeur = chargement.valeurDeclaree();

        // tarif de base selon le mode
        double prix = switch (mode) {
            case ROUTE -> distance * ROUTE_PRIX_KM + poids * ROUTE_PRIX_KG;
            case RAIL -> Math.max(distance * RAIL_PRIX_KM + poids * RAIL_PRIX_KG, RAIL_MINIMUM);
            case MER -> Math.max(distance * MER_PRIX_KM + poids * MER_PRIX_KG, MER_MINIMUM);
            case AIR -> distance * AIR_PRIX_KM + Math.max(poids, volume * AIR_KG_PAR_M3) * AIR_PRIX_KG;
        };

        // suppléments
        if (chargement.contient(TypeMarchandise.FRAGILE)) prix = prix * COEF_FRAGILE;
        if (chargement.contient(TypeMarchandise.DANGEREUX)) prix = prix * COEF_DANGEREUX;
        if (chargement.contient(TypeMarchandise.REFRIGERE)) prix = prix * COEF_REFRIGERE;
        if (urgent) prix = prix * COEF_URGENT;

        // remise client
        if (typeClient == TypeClient.ENTREPRISE) {
            prix = prix * (prix > SEUIL_REMISE_ENTREPRISE_MAJOREE ? COEF_ENTREPRISE_MAJOREE : COEF_ENTREPRISE);
        } else if (typeClient == TypeClient.PREMIUM) {
            prix = prix * COEF_PREMIUM;
        }

        // assurance
        if (assurance) {
            prix = prix + Math.max(valeur * TAUX_ASSURANCE, ASSURANCE_MINIMUM);
        }
        return Montants.arrondir(prix);
    }
}
