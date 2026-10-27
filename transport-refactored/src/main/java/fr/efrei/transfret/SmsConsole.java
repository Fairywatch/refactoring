package fr.efrei.transfret;

import java.util.ArrayList;
import java.util.List;

/**
 * Affiche les SMS sur la console et garde la trace de ceux envoyés (facturés par l'opérateur).
 */
public class SmsConsole implements CanalSms {

    private static final int LONGUEUR_MAX = 160;
    private static final String POINTS_DE_SUSPENSION = "...";

    private final List<MessageEnvoye> envoyes = new ArrayList<>();

    @Override
    public void envoyer(String numero, String texte) {
        if (texte.length() > LONGUEUR_MAX) {
            texte = texte.substring(0, LONGUEUR_MAX - POINTS_DE_SUSPENSION.length()) + POINTS_DE_SUSPENSION;
        }
        System.out.println("[SMS] -> " + numero + " : " + texte);
        envoyes.add(new MessageEnvoye(numero, texte));
    }

    public List<MessageEnvoye> envoyes() {
        return List.copyOf(envoyes);
    }
}
