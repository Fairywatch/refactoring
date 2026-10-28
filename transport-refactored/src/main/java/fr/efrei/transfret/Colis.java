package fr.efrei.transfret;

public class Colis {

    private static final double CM3_PAR_M3 = 1_000_000;

    private final String description;
    private final double poids;        // en kg
    private final double longueur;     // en cm
    private final double largeur;      // en cm
    private final double hauteur;      // en cm
    private final TypeMarchandise type;
    private final double valeurDeclaree;

    public Colis(String description, double poids, double longueur, double largeur, double hauteur, TypeMarchandise type, double valeurDeclaree) {
        this.description = description;
        this.poids = poids;
        this.longueur = longueur;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.type = type;
        this.valeurDeclaree = valeurDeclaree;
    }

    // volume en m3
    public double getVolume() {
        return (longueur * largeur * hauteur) / CM3_PAR_M3;
    }

    public double getPlusGrandeDimension() {
        return Math.max(longueur, Math.max(largeur, hauteur));
    }

    public String getLibelleType() {
        return type.getLibelle();
    }

    public String getDescription() {
        return description;
    }

    public double getPoids() {
        return poids;
    }

    public TypeMarchandise getType() {
        return type;
    }

    public double getValeurDeclaree() {
        return valeurDeclaree;
    }
}
