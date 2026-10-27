package fr.efrei.transfret;

import java.time.format.DateTimeFormatter;

/**
 * Rédige les messages adressés aux clients et les envoie sur les bons canaux.
 */
public class Notificateur {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void souhaiterBienvenue(Client client) {
        EmailService.envoyer(client.getEmail(), "Bienvenue chez TransFret",
                "Bonjour " + client.getNom() + ", votre compte " + client.getType().name().toLowerCase() + " a été créé.");
    }

    public void confirmerCreation(Expedition e) {
        notifier(e.getClient(), "Confirmation de votre expédition " + e.getId(),
                "Votre expédition " + e.getVilleDepart() + " -> " + e.getVilleArrivee() + " (" + e.getMode() + ") a été enregistrée. Montant : " + e.getPrixTTC() + " € TTC.",
                "TransFret : expédition " + e.getId() + " créée, livraison prévue le " + e.getDateLivraisonPrevue().format(FORMAT_DATE));
    }

    public void signalerDepart(Expedition e) {
        notifier(e.getClient(), "Votre expédition " + e.getId() + " est en route",
                "Votre expédition a quitté " + e.getVilleDepart() + ".",
                "TransFret : expédition " + e.getId() + " partie de " + e.getVilleDepart());
    }

    public void signalerLivraison(Expedition e) {
        notifier(e.getClient(), "Votre expédition " + e.getId() + " est livrée",
                "Votre expédition est arrivée à " + e.getVilleArrivee() + ". Merci de votre confiance.",
                "TransFret : expédition " + e.getId() + " livrée à " + e.getVilleArrivee());
    }

    public void signalerAnnulation(Expedition e) {
        notifier(e.getClient(), "Annulation de votre expédition " + e.getId(),
                "Votre expédition a été annulée. Frais d'annulation : " + e.getFraisAnnulation() + " €",
                "TransFret : expédition " + e.getId() + " annulée");
    }

    private void notifier(Client client, String sujet, String corps, String sms) {
        EmailService.envoyer(client.getEmail(), sujet, corps);
        if (client.getType() == TypeClient.PREMIUM) {
            new SmsService().envoyerSms(client.getTelephone(), sms);
        }
    }
}
