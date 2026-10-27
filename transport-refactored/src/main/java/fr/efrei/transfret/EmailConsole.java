package fr.efrei.transfret;

import java.util.ArrayList;
import java.util.List;

/**
 * Affiche les emails sur la console et garde la trace de ceux envoyés (demandé par le support client).
 */
public class EmailConsole implements CanalEmail {

    private static final String EXPEDITEUR = "noreply@transfret.fr";

    private final List<MessageEnvoye> envoyes = new ArrayList<>();

    @Override
    public void envoyer(String destinataire, String sujet, String corps) {
        System.out.println("[EMAIL] " + EXPEDITEUR + " -> " + destinataire + " | " + sujet);
        System.out.println("        " + corps);
        envoyes.add(new MessageEnvoye(destinataire, sujet));
    }

    public List<MessageEnvoye> envoyes() {
        return List.copyOf(envoyes);
    }
}
