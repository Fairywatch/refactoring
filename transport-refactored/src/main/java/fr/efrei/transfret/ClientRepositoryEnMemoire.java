package fr.efrei.transfret;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClientRepositoryEnMemoire implements ClientRepository {

    private final Map<String, Client> clients = new HashMap<>();

    @Override
    public void sauvegarder(Client client) {
        clients.put(client.getEmail(), client);
    }

    @Override
    public Optional<Client> trouver(String email) {
        return Optional.ofNullable(email == null ? null : clients.get(email));
    }
}
