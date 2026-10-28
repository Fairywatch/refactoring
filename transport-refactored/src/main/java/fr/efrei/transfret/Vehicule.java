package fr.efrei.transfret;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Un véhicule de la flotte. Chaque sous-classe dit ce qu'elle refuse et comment elle se déplace :
 * aucune méthode n'est imposée à un véhicule qui ne saurait pas l'exécuter.
 */
public abstract class Vehicule {

    protected static final double TEMPERATURE_FROID = 4;

    private final String immatriculation;
    private final double capaciteMax;
    private final ModeTransport mode;
    private final Set<TypeMarchandise> marchandisesRefusees;
    private final List<Colis> chargement = new ArrayList<>();
    private double chargeActuelle;
    private boolean disponible = true;

    protected Vehicule(String immatriculation, double capaciteMax, ModeTransport mode, Set<TypeMarchandise> marchandisesRefusees) {
        this.immatriculation = immatriculation;
        this.capaciteMax = capaciteMax;
        this.mode = mode;
        this.marchandisesRefusees = Set.copyOf(marchandisesRefusees);
    }

    /** Disponible, du bon mode, assez de place, et aucune marchandise refusée. */
    public boolean peutPrendreEnCharge(Chargement colis, ModeTransport modeDemande) {
        return disponible && mode == modeDemande && capaciteMax - chargeActuelle >= colis.poids()
                && marchandisesRefusees.stream().noneMatch(colis::contient);
    }

    /** @throws IllegalStateException si le chargement n'a pas été vérifié par {@link #peutPrendreEnCharge} */
    public void charger(Chargement colis) {
        if (capaciteMax - chargeActuelle < colis.poids()) {
            throw new IllegalStateException("capacité dépassée pour " + immatriculation);
        }
        for (Colis c : colis.colis()) {
            chargement.add(c);
            chargeActuelle = chargeActuelle + c.getPoids();
        }
    }

    /** Le véhicule n'est plus disponible jusqu'à ce qu'il soit libéré. */
    public void partir(int distanceKm) {
        disponible = false;
        seDeplacer(distanceKm);
    }

    /** Décharge ces colis (livrés ou annulés) et rend le véhicule disponible. */
    public void liberer(Chargement colis) {
        for (Colis c : colis.colis()) {
            if (chargement.remove(c)) {
                chargeActuelle = chargeActuelle - c.getPoids();
            }
        }
        disponible = true;
    }

    protected abstract void seDeplacer(int distanceKm);

    protected void faireLePlein() {
        System.out.println("Plein effectué pour " + immatriculation);
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public double getChargeActuelle() {
        return chargeActuelle;
    }
}
