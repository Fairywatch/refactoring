package fr.efrei.transfret;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ServiceExpedition {

    private static final double VITESSE_ROUTE_KMH = 70.0;
    private static final int HEURES_CONDUITE_PAR_JOUR = 9;
    private static final int HEURES_CONDUITE_PAR_JOUR_URGENT = 18;
    private static final double RAIL_KM_PAR_JOUR = 1440.0;
    private static final int RAIL_JOURS_MANUTENTION = 1;
    private static final double MER_KM_PAR_JOUR = 720.0;
    private static final int MER_JOURS_PORTUAIRES = 2;
    private static final int AIR_SEUIL_LONG_COURRIER_KM = 3000;
    private static final int AIR_JOURS_LONG_COURRIER = 2;
    private static final int DELAI_MINIMUM_JOURS = 1;

    private static final double TAUX_FRAIS_ANNULATION_EN_COURS = 0.5;
    private static final double KM_PAR_MILLE_NAUTIQUE = 1.852;
    private static final int ROULAGE_PISTE_KM = 2;
    private static final int ALTITUDE_CROISIERE_PIEDS = 35000;
    private static final double TEMPERATURE_REFRIGERATION = 4;

    public static int compteur = 0;

    private Database db = Database.getInstance();
    private CalculateurPrix calculateur = new CalculateurPrix();
    private AffectationVehicule affectation = new AffectationVehicule();

    public Expedition creerExpedition(Client client, List<Colis> colis, String villeDepart, String villeArrivee, ModeTransport mode, boolean urgent, boolean assurance) {
        if (client == null) {
            System.out.println("ERREUR : client obligatoire");
            return null;
        }
        if (colis == null || colis.isEmpty()) {
            System.out.println("ERREUR : au moins un colis est nécessaire");
            return null;
        }
        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis c : colis) {
            if (c.getPoids() <= 0) {
                System.out.println("ERREUR : poids invalide pour " + c.getDescription());
                return null;
            }
            if (c.getType() == TypeMarchandise.DANGEREUX) dangereux = true;
            if (c.getType() == TypeMarchandise.REFRIGERE) refrigere = true;
        }
        if (dangereux && mode == ModeTransport.AIR) {
            System.out.println("ERREUR : les matières dangereuses sont interdites en transport aérien");
            return null;
        }
        if (refrigere && !(mode == ModeTransport.ROUTE || mode == ModeTransport.MER)) {
            System.out.println("ERREUR : le transport réfrigéré n'est possible que par route ou par mer");
            return null;
        }
        if (urgent && mode == ModeTransport.MER) {
            System.out.println("ERREUR : pas d'option urgente pour le transport maritime");
            return null;
        }
        int distance = Utils.getDistance(villeDepart, villeArrivee);
        if (distance == -1) {
            System.out.println("ERREUR : trajet inconnu " + villeDepart + " -> " + villeArrivee);
            return null;
        }

        Vehicule v = affectation.trouverVehicule(colis, mode);
        if (v == null) {
            System.out.println("ERREUR : aucun véhicule disponible pour cette expédition");
            return null;
        }
        for (Colis c : colis) {
            v.charger(c);
        }

        Expedition e = new Expedition();
        compteur++;
        e.setId("EXP-" + compteur + "-" + (1000 + new Random().nextInt(9000)));
        e.setClient(client);
        e.setColis(colis);
        e.setVilleDepart(villeDepart);
        e.setVilleArrivee(villeArrivee);
        e.setDistance(distance);
        e.setMode(mode);
        e.setUrgent(urgent);
        e.setAssurance(assurance);
        e.setStatut(StatutExpedition.CREEE);
        e.setVehicule(v);
        e.setPrixHT(calculateur.calculerPrixHT(colis, distance, mode, urgent, assurance, client.getType()));
        e.setPrixTTC(Montants.ttc(e.getPrixHT()));
        e.setDateCreation(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(e.getDateCreation());
        cal.add(Calendar.DAY_OF_MONTH, calculerDelai(distance, mode, urgent));
        e.setDateLivraisonPrevue(cal.getTime());

        db.sauvegarderExpedition(e);
        notifier(client, "Confirmation de votre expédition " + e.getId(),
                "Votre expédition " + villeDepart + " -> " + villeArrivee + " (" + mode + ") a été enregistrée. Montant : " + e.getPrixTTC() + " € TTC.",
                "TransFret : expédition " + e.getId() + " créée, livraison prévue le " + Utils.formaterDate(e.getDateLivraisonPrevue()));
        return e;
    }

    // retourne le prix TTC, -1 si le trajet est inconnu
    public double calculerDevis(List<Colis> colis, String villeDepart, String villeArrivee, ModeTransport mode, boolean urgent, boolean assurance, TypeClient typeClient) {
        int distance = Utils.getDistance(villeDepart, villeArrivee);
        if (distance == -1) {
            return -1;
        }
        double ht = calculateur.calculerPrixHT(colis, distance, mode, urgent, assurance, typeClient);
        return Montants.ttc(ht);
    }

    // retourne le délai de livraison en jours
    public int calculerDelai(int distance, ModeTransport mode, boolean urgent) {
        int jours = switch (mode) {
            case ROUTE -> (int) Math.ceil(distance / VITESSE_ROUTE_KMH
                    / (urgent ? HEURES_CONDUITE_PAR_JOUR_URGENT : HEURES_CONDUITE_PAR_JOUR));
            case RAIL -> (int) Math.ceil(distance / RAIL_KM_PAR_JOUR) + (urgent ? 0 : RAIL_JOURS_MANUTENTION);
            case MER -> (int) Math.ceil(distance / MER_KM_PAR_JOUR) + MER_JOURS_PORTUAIRES;
            case AIR -> !urgent && distance > AIR_SEUIL_LONG_COURRIER_KM ? AIR_JOURS_LONG_COURRIER : DELAI_MINIMUM_JOURS;
        };
        return Math.max(jours, DELAI_MINIMUM_JOURS);
    }

    public boolean demarrerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || e.getStatut() != StatutExpedition.CREEE) {
            System.out.println("ERREUR : impossible de démarrer l'expédition " + id);
            return false;
        }
        e.setStatut(StatutExpedition.EN_COURS);
        Vehicule v = e.getVehicule();
        v.setDisponible(false);
        try {
            v.faireLePlein();
        } catch (Exception ex) {
        }
        switch (e.getMode()) {
            case ROUTE, RAIL -> v.rouler(e.getDistance());
            case MER -> v.naviguer((int) (e.getDistance() / KM_PAR_MILLE_NAUTIQUE));
            case AIR -> {
                v.rouler(ROULAGE_PISTE_KM);
                v.voler(ALTITUDE_CROISIERE_PIEDS);
            }
        }
        for (Colis c : e.getColis()) {
            if (c.getType() == TypeMarchandise.REFRIGERE) {
                v.activerRefrigeration(TEMPERATURE_REFRIGERATION);
                break;
            }
        }
        db.mettreAJourStatut(e);
        notifier(e.getClient(), "Votre expédition " + e.getId() + " est en route",
                "Votre expédition a quitté " + e.getVilleDepart() + ".",
                "TransFret : expédition " + e.getId() + " partie de " + e.getVilleDepart());
        return true;
    }

    public boolean livrerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || e.getStatut() != StatutExpedition.EN_COURS) {
            System.out.println("ERREUR : impossible de livrer l'expédition " + id);
            return false;
        }
        e.setStatut(StatutExpedition.LIVREE);
        libererVehicule(e);
        db.mettreAJourStatut(e);
        notifier(e.getClient(), "Votre expédition " + e.getId() + " est livrée",
                "Votre expédition est arrivée à " + e.getVilleArrivee() + ". Merci de votre confiance.",
                "TransFret : expédition " + e.getId() + " livrée à " + e.getVilleArrivee());
        return true;
    }

    public boolean annulerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || e.getStatut() == StatutExpedition.LIVREE || e.getStatut() == StatutExpedition.ANNULEE) {
            System.out.println("ERREUR : impossible d'annuler l'expédition " + id);
            return false;
        }
        if (e.getStatut() == StatutExpedition.EN_COURS) {
            // 50% de frais si déjà partie
            e.setFraisAnnulation(Montants.arrondir(e.getPrixTTC() * TAUX_FRAIS_ANNULATION_EN_COURS));
        }
        e.setStatut(StatutExpedition.ANNULEE);
        libererVehicule(e);
        db.mettreAJourStatut(e);
        notifier(e.getClient(), "Annulation de votre expédition " + e.getId(),
                "Votre expédition a été annulée. Frais d'annulation : " + e.getFraisAnnulation() + " €",
                "TransFret : expédition " + e.getId() + " annulée");
        return true;
    }

    private void libererVehicule(Expedition e) {
        for (Colis c : e.getColis()) {
            e.getVehicule().decharger(c);
        }
        e.getVehicule().setDisponible(true);
    }

    private void notifier(Client client, String sujet, String corps, String sms) {
        EmailService.envoyer(client.getEmail(), sujet, corps);
        if (client.getType() == TypeClient.PREMIUM) {
            new SmsService().envoyerSms(client.getTelephone(), sms);
        }
    }
}
