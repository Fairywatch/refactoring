package fr.efrei.transfret;

import java.util.Optional;

public interface ClientRepository {

    void sauvegarder(Client client);

    /** Les clients sont identifiés par leur email. */
    Optional<Client> trouver(String email);
}
