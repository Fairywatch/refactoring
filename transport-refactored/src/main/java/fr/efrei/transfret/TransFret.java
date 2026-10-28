package fr.efrei.transfret;

import java.util.List;
import java.util.Map;

/**
 * Point d'assemblage : le seul endroit qui construit le graphe d'objets de l'application.
 * Créer un nouveau {@code TransFret}, c'est repartir d'un système vierge.
 */
public class TransFret {

    private final EmailConsole emails = new EmailConsole();
    private final SmsConsole sms = new SmsConsole();
    private final GenerateurFacture generateurFacture = new GenerateurFacture();
    private final ServiceClient serviceClient;
    private final ServiceExpedition serviceExpedition;

    public TransFret() {
        ClientRepository clients = new ClientRepositoryEnMemoire();
        Notificateur notificateur = new Notificateur(emails, sms);

        List<Vehicule> flotte = List.of(
                new Camion("AB-123-CD", 19000, false),
                new Camion("EF-456-GH", 12000, true),
                new CamionFrigorifique("IJ-789-KL", 8000),
                new Train("TR-0001", 600000),
                new Bateau("CMA-CGM-01", 20000000, true),
                new Avion("F-GTXA", 100000));

        TableDistances distances = new TableDistances()
                .ajouter("Paris", "Lyon", 465)
                .ajouter("Paris", "Marseille", 775)
                .ajouter("Paris", "Lille", 225)
                .ajouter("Paris", "Bordeaux", 585)
                .ajouter("Lyon", "Marseille", 315)
                .ajouter("Le Havre", "New York", 5850)
                .ajouter("Marseille", "Alger", 760)
                .ajouter("Paris", "New York", 5840);

        // L'ordre compte : la remise entreprise dépend du prix après suppléments et urgence,
        // et l'assurance s'ajoute après la remise.
        CalculPrix prix = new TarifSelonMode(Map.of(
                ModeTransport.ROUTE, new TarifRoute(),
                ModeTransport.RAIL, new TarifRail(),
                ModeTransport.MER, new TarifMer(),
                ModeTransport.AIR, new TarifAir()));
        prix = new SupplementFragile(prix);
        prix = new SupplementDangereux(prix);
        prix = new SupplementRefrigere(prix);
        prix = new SupplementUrgence(prix);
        prix = new RemiseClient(prix, Map.of(
                TypeClient.PARTICULIER, new AucuneRemise(),
                TypeClient.ENTREPRISE, new RemiseEntreprise(),
                TypeClient.PREMIUM, new RemisePremium()));
        prix = new Assurance(prix);
        CalculateurPrix calculateur = new CalculateurPrix(prix);
        EstimateurDelai estimateurDelai = new EstimateurDelai(
                Map.of(ModeTransport.ROUTE, new DelaiRoute(),
                        ModeTransport.RAIL, new DelaiRail(),
                        ModeTransport.MER, new DelaiMer(),
                        ModeTransport.AIR, new DelaiAir()));

        serviceClient = new ServiceClient(new ValidateurClient(), clients, notificateur);
        serviceExpedition = new ServiceExpedition(new ExpeditionRepositoryEnMemoire(), clients, new ValidateurExpedition(),
                distances, new AffectationVehicule(flotte), calculateur, estimateurDelai, notificateur,
                new GenerateurIdentifiant());
    }

    public ServiceClient serviceClient() {
        return serviceClient;
    }

    public ServiceExpedition serviceExpedition() {
        return serviceExpedition;
    }

    public GenerateurFacture generateurFacture() {
        return generateurFacture;
    }

    public EmailConsole emails() {
        return emails;
    }

    public SmsConsole sms() {
        return sms;
    }
}
