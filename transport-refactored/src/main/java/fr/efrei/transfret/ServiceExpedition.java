package fr.efrei.transfret;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;

public class ServiceExpedition {

    private static final double KM_PAR_MILLE_NAUTIQUE = 1.852;
    private static final int ROULAGE_PISTE_KM = 2;
    private static final int ALTITUDE_CROISIERE_PIEDS = 35000;
    private static final double TEMPERATURE_REFRIGERATION = 4;

    public static int compteur = 0;

    private Database db = Database.getInstance();
    private CalculateurPrix calculateur = new CalculateurPrix();
    private AffectationVehicule affectation = new AffectationVehicule();
    private ValidateurExpedition validateur = new ValidateurExpedition();
    private EstimateurDelai estimateurDelai = new EstimateurDelai();
    private Notificateur notificateur = new Notificateur();
    private TableDistances distances = TableDistances.reseauTransFret();

    public Expedition creerExpedition(Client client, List<Colis> colis, String villeDepart, String villeArrivee, ModeTransport mode, boolean urgent, boolean assurance) {
        Chargement chargement = new Chargement(colis);
        Optional<String> refus = validateur.verifier(client, chargement, mode, urgent);
        if (refus.isPresent()) {
            return refuser(refus.get());
        }
        OptionalInt distance = distances.distance(villeDepart, villeArrivee);
        if (distance.isEmpty()) {
            return refuser("trajet inconnu " + villeDepart + " -> " + villeArrivee);
        }
        Vehicule v = affectation.trouverVehicule(chargement, mode);
        if (v == null) {
            return refuser("aucun véhicule disponible pour cette expédition");
        }
        for (Colis c : chargement.colis()) {
            v.charger(c);
        }

        int km = distance.getAsInt();
        LocalDate creation = LocalDate.now();
        compteur++;
        Expedition e = new Expedition("EXP-" + compteur + "-" + (1000 + new Random().nextInt(9000)), client, chargement,
                villeDepart, villeArrivee, km, mode, urgent, v,
                calculateur.calculerPrixHT(chargement, km, mode, urgent, assurance, client.getType()),
                creation, creation.plusDays(estimateurDelai.jours(km, mode, urgent)));
        db.sauvegarderExpedition(e);
        notificateur.confirmerCreation(e);
        return e;
    }

    // retourne le prix TTC, -1 si le trajet est inconnu
    public double calculerDevis(List<Colis> colis, String villeDepart, String villeArrivee, ModeTransport mode, boolean urgent, boolean assurance, TypeClient typeClient) {
        OptionalInt distance = distances.distance(villeDepart, villeArrivee);
        if (distance.isEmpty()) {
            return -1;
        }
        return Montants.ttc(calculateur.calculerPrixHT(new Chargement(colis), distance.getAsInt(), mode, urgent, assurance, typeClient));
    }

    public boolean demarrerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || !e.demarrer()) {
            System.out.println("ERREUR : impossible de démarrer l'expédition " + id);
            return false;
        }
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
        if (e.getChargement().contient(TypeMarchandise.REFRIGERE)) {
            v.activerRefrigeration(TEMPERATURE_REFRIGERATION);
        }
        db.mettreAJourStatut(e);
        notificateur.signalerDepart(e);
        return true;
    }

    public boolean livrerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || !e.livrer()) {
            System.out.println("ERREUR : impossible de livrer l'expédition " + id);
            return false;
        }
        libererVehicule(e);
        db.mettreAJourStatut(e);
        notificateur.signalerLivraison(e);
        return true;
    }

    public boolean annulerExpedition(String id) {
        Expedition e = db.trouverExpedition(id);
        if (e == null || !e.annuler()) {
            System.out.println("ERREUR : impossible d'annuler l'expédition " + id);
            return false;
        }
        libererVehicule(e);
        db.mettreAJourStatut(e);
        notificateur.signalerAnnulation(e);
        return true;
    }

    private void libererVehicule(Expedition e) {
        for (Colis c : e.getChargement().colis()) {
            e.getVehicule().decharger(c);
        }
        e.getVehicule().setDisponible(true);
    }

    private static Expedition refuser(String motif) {
        System.out.println("ERREUR : " + motif);
        return null;
    }
}
