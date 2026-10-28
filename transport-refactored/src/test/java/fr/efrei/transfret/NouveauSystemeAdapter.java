package fr.efrei.transfret;

import fr.efrei.transport.contrat.Canal;
import fr.efrei.transport.contrat.ColisDto;
import fr.efrei.transport.contrat.DemandeClient;
import fr.efrei.transport.contrat.DemandeDevis;
import fr.efrei.transport.contrat.DemandeExpedition;
import fr.efrei.transport.contrat.EtatExpedition;
import fr.efrei.transport.contrat.Notification;
import fr.efrei.transport.contrat.Statut;
import fr.efrei.transport.contrat.SystemeTransport;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Stream;

/**
 * Branche le nouveau code sur les tests de contrat. Ne fait que traduire les types du contrat vers ceux de TransFret.
 */
public class NouveauSystemeAdapter implements SystemeTransport {

    private TransFret transfret;

    @Override
    public void reinitialiser() {
        transfret = new TransFret();
    }

    @Override
    public Optional<String> creerClient(DemandeClient d) {
        return transfret.serviceClient()
                .inscrire(new Client(d.nom(), d.email(), d.telephone(), d.adresse(), TypeClient.valueOf(d.type().name()), d.siret()))
                .map(Client::getEmail);
    }

    @Override
    public Optional<String> creerExpedition(DemandeExpedition d) {
        return transfret.serviceExpedition()
                .creerExpedition(d.refClient(), versCommande(d))
                .map(Expedition::getId);
    }

    @Override
    public Optional<EtatExpedition> consulter(String id) {
        return transfret.serviceExpedition().trouver(id).map(e -> new EtatExpedition(e.getId(),
                Statut.valueOf(e.getStatut().name()), e.getPrixHT(), e.getPrixTTC(),
                (int) ChronoUnit.DAYS.between(e.getDateCreation(), e.getDateLivraisonPrevue()),
                e.getVehicule().getImmatriculation(), e.getFraisAnnulation()));
    }

    @Override
    public OptionalDouble devisTTC(DemandeDevis d) {
        return transfret.serviceExpedition().calculerDevis(versCommande(d), TypeClient.valueOf(d.typeClient().name()));
    }

    @Override
    public boolean demarrer(String id) {
        return transfret.serviceExpedition().demarrerExpedition(id);
    }

    @Override
    public boolean livrer(String id) {
        return transfret.serviceExpedition().livrerExpedition(id);
    }

    @Override
    public boolean annuler(String id) {
        return transfret.serviceExpedition().annulerExpedition(id);
    }

    @Override
    public Optional<String> facture(String id) {
        return transfret.serviceExpedition().trouver(id).map(transfret.generateurFacture()::generer);
    }

    @Override
    public List<Notification> notifications() {
        return Stream.concat(
                transfret.emails().envoyes().stream().map(m -> new Notification(Canal.EMAIL, m.destinataire(), m.objet())),
                transfret.sms().envoyes().stream().map(m -> new Notification(Canal.SMS, m.destinataire(), m.objet()))
        ).toList();
    }

    private static Commande versCommande(DemandeExpedition d) {
        return new Commande(versChargement(d.colis()), d.villeDepart(), d.villeArrivee(),
                ModeTransport.valueOf(d.mode().name()), d.urgent(), d.assurance());
    }

    private static Commande versCommande(DemandeDevis d) {
        return new Commande(versChargement(d.colis()), d.villeDepart(), d.villeArrivee(),
                ModeTransport.valueOf(d.mode().name()), d.urgent(), d.assurance());
    }

    private static Chargement versChargement(List<ColisDto> colis) {
        return new Chargement(colis.stream().map(c -> new Colis(c.description(), c.poidsKg(), c.longueurCm(),
                c.largeurCm(), c.hauteurCm(), TypeMarchandise.valueOf(c.type().name()), c.valeurDeclaree())).toList());
    }
}
