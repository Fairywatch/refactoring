package fr.efrei.transfret;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Met en forme la facture texte d'une expédition.
 */
public class GenerateurFacture {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String DOUBLE_TRAIT = "==============================================\n";
    private static final String TRAIT = "----------------------------------------------\n";

    private final Clock horloge;

    public GenerateurFacture(Clock horloge) {
        this.horloge = horloge;
    }

    public String generer(Expedition e) {
        Client client = e.getClient();
        StringBuilder sb = new StringBuilder();
        sb.append(DOUBLE_TRAIT);
        sb.append("              FACTURE TRANSFRET\n");
        sb.append(DOUBLE_TRAIT);
        sb.append("Facture n° F-").append(e.getId()).append("\n");
        sb.append("Date : ").append(LocalDate.now(horloge).format(FORMAT_DATE)).append("\n");
        sb.append("Client : ").append(client.getNom()).append("\n");
        if (client.getType().siretObligatoire()) {
            sb.append("SIRET : ").append(client.getSiret()).append("\n");
        }
        sb.append("Adresse : ").append(client.getAdresse()).append("\n");
        sb.append(TRAIT);
        sb.append("Trajet : ").append(e.getVilleDepart()).append(" -> ").append(e.getVilleArrivee())
                .append(" (").append(e.getDistance()).append(" km)\n");
        sb.append("Mode : ").append(e.getMode()).append(e.isUrgent() ? " (URGENT)" : "").append("\n");
        sb.append("Colis :\n");
        for (Colis c : e.getChargement().colis()) {
            sb.append("  - ").append(c.getDescription()).append(" : ").append(c.getPoids())
                    .append(" kg [").append(c.getLibelleType()).append("]\n");
        }
        sb.append(TRAIT);
        if (e.getStatut() == StatutExpedition.ANNULEE) {
            sb.append("EXPEDITION ANNULEE\n");
            sb.append("Frais d'annulation TTC : ").append(montant(e.getFraisAnnulation())).append(" €\n");
        } else {
            sb.append("Total HT  : ").append(montant(e.getPrixHT())).append(" €\n");
            sb.append("TVA 20%   : ").append(montant(e.getPrixTTC() - e.getPrixHT())).append(" €\n");
            sb.append("Total TTC : ").append(montant(e.getPrixTTC())).append(" €\n");
        }
        sb.append(DOUBLE_TRAIT);
        return sb.toString();
    }

    private static String montant(double euros) {
        return String.format("%.2f", euros);
    }
}
