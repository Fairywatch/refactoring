package fr.efrei.transfret;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class ServiceExpedition {

    private final ExpeditionRepository expeditions;
    private final ClientRepository clients;
    private final ValidateurExpedition validateur;
    private final TableDistances distances;
    private final AffectationVehicule affectation;
    private final CalculateurPrix calculateur;
    private final EstimateurDelai estimateurDelai;
    private final Notificateur notificateur;
    private final GenerateurIdentifiant identifiants;
    private final Clock horloge;

    public ServiceExpedition(ExpeditionRepository expeditions, ClientRepository clients, ValidateurExpedition validateur,
                             TableDistances distances, AffectationVehicule affectation, CalculateurPrix calculateur,
                             EstimateurDelai estimateurDelai, Notificateur notificateur, GenerateurIdentifiant identifiants,
                             Clock horloge) {
        this.expeditions = expeditions;
        this.clients = clients;
        this.validateur = validateur;
        this.distances = distances;
        this.affectation = affectation;
        this.calculateur = calculateur;
        this.estimateurDelai = estimateurDelai;
        this.notificateur = notificateur;
        this.identifiants = identifiants;
        this.horloge = horloge;
    }

    public Optional<Expedition> creerExpedition(String emailClient, Commande commande) {
        Optional<Client> client = clients.trouver(emailClient);
        if (client.isEmpty()) {
            return refuser("client inconnu " + emailClient);
        }
        Optional<String> refus = validateur.verifier(commande);
        if (refus.isPresent()) {
            return refuser(refus.get());
        }
        OptionalInt distance = distances.distance(commande.villeDepart(), commande.villeArrivee());
        if (distance.isEmpty()) {
            return refuser("trajet inconnu " + commande.villeDepart() + " -> " + commande.villeArrivee());
        }
        Optional<Vehicule> vehicule = affectation.trouverVehicule(commande.chargement(), commande.mode());
        if (vehicule.isEmpty()) {
            return refuser("aucun véhicule disponible pour cette expédition");
        }
        vehicule.get().charger(commande.chargement());

        int km = distance.getAsInt();
        LocalDate creation = LocalDate.now(horloge);
        Expedition e = new Expedition(identifiants.suivant(), client.get(), commande, km, vehicule.get(),
                calculateur.calculerPrixHT(new DemandeTarif(commande, km, client.get().getType())),
                creation, creation.plusDays(estimateurDelai.jours(km, commande.mode(), commande.urgent())));
        expeditions.sauvegarder(e);
        notificateur.confirmerCreation(e);
        return Optional.of(e);
    }

    /** @return le prix TTC, ou vide si le trajet est inconnu */
    public OptionalDouble calculerDevis(Commande commande, TypeClient typeClient) {
        OptionalInt distance = distances.distance(commande.villeDepart(), commande.villeArrivee());
        if (distance.isEmpty()) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(Montants.ttc(
                calculateur.calculerPrixHT(new DemandeTarif(commande, distance.getAsInt(), typeClient))));
    }

    public Optional<Expedition> trouver(String id) {
        return expeditions.trouver(id);
    }

    public boolean demarrerExpedition(String id) {
        Optional<Expedition> expedition = expeditions.trouver(id);
        if (expedition.isEmpty() || !expedition.get().demarrer()) {
            System.out.println("ERREUR : impossible de démarrer l'expédition " + id);
            return false;
        }
        Expedition e = expedition.get();
        e.getVehicule().partir(e.getDistance());
        expeditions.sauvegarder(e);
        notificateur.signalerDepart(e);
        return true;
    }

    public boolean livrerExpedition(String id) {
        Optional<Expedition> expedition = expeditions.trouver(id);
        if (expedition.isEmpty() || !expedition.get().livrer()) {
            System.out.println("ERREUR : impossible de livrer l'expédition " + id);
            return false;
        }
        Expedition e = expedition.get();
        e.getVehicule().liberer(e.getChargement());
        expeditions.sauvegarder(e);
        notificateur.signalerLivraison(e);
        return true;
    }

    public boolean annulerExpedition(String id) {
        Optional<Expedition> expedition = expeditions.trouver(id);
        if (expedition.isEmpty() || !expedition.get().annuler()) {
            System.out.println("ERREUR : impossible d'annuler l'expédition " + id);
            return false;
        }
        Expedition e = expedition.get();
        e.getVehicule().liberer(e.getChargement());
        expeditions.sauvegarder(e);
        notificateur.signalerAnnulation(e);
        return true;
    }

    private static Optional<Expedition> refuser(String motif) {
        System.out.println("ERREUR : " + motif);
        return Optional.empty();
    }
}
