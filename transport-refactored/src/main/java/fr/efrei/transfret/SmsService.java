package fr.efrei.transfret;

import java.util.ArrayList;
import java.util.List;

public class SmsService {

    private static final int LONGUEUR_MAX = 160;
    private static final String POINTS_DE_SUSPENSION = "...";

    // garde une trace des SMS envoyés (facturés par l'opérateur)
    public static List<String[]> journal = new ArrayList<String[]>();

    private String apiKey = "sk_live_8f7d6e5c4b3a2918";

    public void envoyerSms(String numero, String message) {
        if (message.length() > LONGUEUR_MAX) {
            message = message.substring(0, LONGUEUR_MAX - POINTS_DE_SUSPENSION.length()) + POINTS_DE_SUSPENSION;
        }
        System.out.println("[SMS] -> " + numero + " : " + message);
        journal.add(new String[]{numero, message});
    }
}
