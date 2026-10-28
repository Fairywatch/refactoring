package fr.efrei.transfret;

import java.util.Optional;

public class ServiceClient {

    private final ValidateurClient validateur;
    private final ClientRepository clients;
    private final Notificateur notificateur;

    public ServiceClient(ValidateurClient validateur, ClientRepository clients, Notificateur notificateur) {
        this.validateur = validateur;
        this.clients = clients;
        this.notificateur = notificateur;
    }

    /** @return le client inscrit, ou vide s'il est refusé */
    public Optional<Client> inscrire(Client client) {
        Optional<String> refus = validateur.verifier(client);
        if (refus.isPresent()) {
            System.out.println("ERREUR : " + refus.get());
            return Optional.empty();
        }
        clients.sauvegarder(client);
        notificateur.souhaiterBienvenue(client);
        return Optional.of(client);
    }
}
