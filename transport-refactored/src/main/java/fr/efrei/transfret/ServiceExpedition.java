package fr.efrei.transfret;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class ServiceExpedition {

    private static final double KM_PAR_MILLE_NAUTIQUE = 1.852;
    private static final int ROULAGE_PISTE_KM = 2;
    private static final int ALTITUDE_CROISIERE_PIEDS = 35000;
    private static final double TEMPERATURE_REFRIGERATION = 4;

    private final ExpeditionRepository expeditions;
    private final ClientRepository clients;
    private final ValidateurExpedition validateur;
    private final TableDistances distances;
    private final AffectationVehicule affectation;
    private final CalculateurPrix calculateur;
    private final EstimateurDelai estimateurDelai;
    private final Notificateur notificateur;
    private final GenerateurIdentifiant identifiants;

    public ServiceExpedition(ExpeditionRepository expeditions, ClientRepository clients, ValidateurExpedition validateur,
                             TableDistances distances, AffectationVehicule affectation, CalculateurPrix calculateur,
                             EstimateurDelai estimateurDelai, Notificateur notificateur, GenerateurIdentifiant identifiants) {
        this.expeditions = expeditions;
        this.clients = clients;
        this.validateur = validateur;
        this.distances = distances;
        this.affectation = affectation;
        this.calculateur = calculateur;
        this.estimateurDelai = estimateurDelai;
        this.notificateur = notificateur;
        this.identifiants = identifiants;
    }

    public Optional<Expedition> creerExpedition(String emailClient, List<Colis> colis, String villeDepart, String villeArrivee,
                                                ModeTransport mode, boolean urgent, boolean assurance) {
        Client client = clients.trouver(emailClient).orElse(null);
        Chargement chargement = new Chargement(colis);
        Optional<String> refus = validateur.verifier(client, chargement, mode, urgent);
        if (refus.isPresent()) {
            return refuser(refus.get());
        }
        OptionalInt distance = distances.distance(villeDepart, villeArrivee);
        if (distance.isEmpty()) {
            return refuser("trajet inconnu " + villeDepart + " -> " + villeArrivee);
        }
        Optional<Vehicule> vehicule = affectation.trouverVehicule(chargement, mode);
        if (vehicule.isEmpty()) {
            return refuser("aucun véhicule disponible pour cette expédition");
        }
        Vehicule v = vehicule.get();
        for (Colis c : chargement.colis()) {
            v.charger(c);
        }

        int km = distance.getAsInt();
        LocalDate creation = LocalDate.now();
        Expedition e = new Expedition(identifiants.suivant(), client, chargement, villeDepart, villeArrivee, km, mode,
                urgent, v, calculateur.calculerPrixHT(new DemandeTarif(chargement, km, mode, urgent, assurance, client.getType())),
                creation, creation.plusDays(estimateurDelai.jours(km, mode, urgent)));
        expeditions.sauvegarder(e);
        notificateur.confirmerCreation(e);
        return Optional.of(e);
    }

    /** @return le prix TTC, ou vide si le trajet est inconnu */
    public OptionalDouble calculerDevis(List<Colis> colis, String villeDepart, String villeArrivee, ModeTransport mode,
                                        boolean urgent, boolean assurance, TypeClient typeClient) {
        OptionalInt distance = distances.distance(villeDepart, villeArrivee);
        if (distance.isEmpty()) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(Montants.ttc(calculateur.calculerPrixHT(
                new DemandeTarif(new Chargement(colis), distance.getAsInt(), mode, urgent, assurance, typeClient))));
    }

    public Optional<Expedition> trouver(String id) {
        return expeditions.trouver(id);
    }

    public boolean demarrerExpedition(String id) {
        Expedition e = expeditions.trouver(id).orElse(null);
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
        expeditions.sauvegarder(e);
        notificateur.signalerDepart(e);
        return true;
    }

    public boolean livrerExpedition(String id) {
        Expedition e = expeditions.trouver(id).orElse(null);
        if (e == null || !e.livrer()) {
            System.out.println("ERREUR : impossible de livrer l'expédition " + id);
            return false;
        }
        libererVehicule(e);
        expeditions.sauvegarder(e);
        notificateur.signalerLivraison(e);
        return true;
    }

    public boolean annulerExpedition(String id) {
        Expedition e = expeditions.trouver(id).orElse(null);
        if (e == null || !e.annuler()) {
            System.out.println("ERREUR : impossible d'annuler l'expédition " + id);
            return false;
        }
        libererVehicule(e);
        expeditions.sauvegarder(e);
        notificateur.signalerAnnulation(e);
        return true;
    }

    private void libererVehicule(Expedition e) {
        for (Colis c : e.getChargement().colis()) {
            e.getVehicule().decharger(c);
        }
        e.getVehicule().setDisponible(true);
    }

    private static Optional<Expedition> refuser(String motif) {
        System.out.println("ERREUR : " + motif);
        return Optional.empty();
    }
}
