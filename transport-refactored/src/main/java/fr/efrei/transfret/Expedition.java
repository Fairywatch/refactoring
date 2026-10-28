package fr.efrei.transfret;

import java.time.LocalDate;

public class Expedition {

    private static final double TAUX_FRAIS_ANNULATION_EN_COURS = 0.5;

    private final String id;
    private final Client client;
    private final Commande commande;
    private final int distance;
    private final Vehicule vehicule;
    private final double prixHT;
    private final double prixTTC;
    private final LocalDate dateCreation;
    private final LocalDate dateLivraisonPrevue;
    private StatutExpedition statut = StatutExpedition.CREEE;
    private double fraisAnnulation;

    public Expedition(String id, Client client, Commande commande, int distance, Vehicule vehicule, double prixHT,
                      LocalDate dateCreation, LocalDate dateLivraisonPrevue) {
        this.id = id;
        this.client = client;
        this.commande = commande;
        this.distance = distance;
        this.vehicule = vehicule;
        this.prixHT = prixHT;
        this.prixTTC = Montants.ttc(prixHT);
        this.dateCreation = dateCreation;
        this.dateLivraisonPrevue = dateLivraisonPrevue;
    }

    /** @return {@code false} si l'expédition n'est pas en attente de départ */
    public boolean demarrer() {
        if (statut != StatutExpedition.CREEE) {
            return false;
        }
        statut = StatutExpedition.EN_COURS;
        return true;
    }

    /** @return {@code false} si l'expédition n'est pas en route */
    public boolean livrer() {
        if (statut != StatutExpedition.EN_COURS) {
            return false;
        }
        statut = StatutExpedition.LIVREE;
        return true;
    }

    /** Gratuit avant le départ, la moitié du TTC une fois parti. @return {@code false} si déjà livrée ou annulée */
    public boolean annuler() {
        if (statut == StatutExpedition.LIVREE || statut == StatutExpedition.ANNULEE) {
            return false;
        }
        if (statut == StatutExpedition.EN_COURS) {
            fraisAnnulation = Montants.arrondir(prixTTC * TAUX_FRAIS_ANNULATION_EN_COURS);
        }
        statut = StatutExpedition.ANNULEE;
        return true;
    }

    public String getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Chargement getChargement() {
        return commande.chargement();
    }

    public String getVilleDepart() {
        return commande.villeDepart();
    }

    public String getVilleArrivee() {
        return commande.villeArrivee();
    }

    public ModeTransport getMode() {
        return commande.mode();
    }

    public boolean isUrgent() {
        return commande.urgent();
    }

    public int getDistance() {
        return distance;
    }

    public StatutExpedition getStatut() {
        return statut;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public LocalDate getDateLivraisonPrevue() {
        return dateLivraisonPrevue;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public double getPrixHT() {
        return prixHT;
    }

    public double getPrixTTC() {
        return prixTTC;
    }

    public double getFraisAnnulation() {
        return fraisAnnulation;
    }
}
