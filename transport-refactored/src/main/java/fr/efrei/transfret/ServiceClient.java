package fr.efrei.transfret;

import java.util.Optional;

public class ServiceClient {

    private ValidateurClient validateur = new ValidateurClient();
    private Notificateur notificateur = new Notificateur();

    public Client creerClient(String nom, String email, String telephone, String adresse, TypeClient type, String siret) {
        Optional<String> refus = validateur.verifier(nom, email, type, siret);
        if (refus.isPresent()) {
            System.out.println("ERREUR : " + refus.get());
            return null;
        }
        Client c = new Client(nom, email, telephone, adresse, type, siret);
        Database.getInstance().sauvegarderClient(c);
        notificateur.souhaiterBienvenue(c);
        return c;
    }
}
