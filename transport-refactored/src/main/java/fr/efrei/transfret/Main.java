package fr.efrei.transfret;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TransFret transfret = new TransFret();
        ServiceClient serviceClient = transfret.serviceClient();
        ServiceExpedition serviceExpedition = transfret.serviceExpedition();
        GenerateurFacture factures = transfret.generateurFacture();

        System.out.println("\n----- Clients -----");
        serviceClient.creerClient("Jean Dupont", "jean.dupont@mail.fr", "0601020304", "12 rue de la Paix, 75002 Paris", TypeClient.PARTICULIER, null);
        serviceClient.creerClient("Transco SA", "contact@transco.fr", "0144556677", "ZI des Chanteraines, 92390 Villeneuve", TypeClient.ENTREPRISE, "12345678900012");
        serviceClient.creerClient("LogiPlus", "ops@logiplus.com", "0788990011", "3 quai du Port, 13002 Marseille", TypeClient.PREMIUM, null);

        System.out.println("\n----- Expéditions -----");
        Expedition exp1 = serviceExpedition.creerExpedition("jean.dupont@mail.fr", List.of(
                        new Colis("Cartons livres", 35, 60, 40, 40, TypeMarchandise.STANDARD, 300),
                        new Colis("Vaisselle", 12, 50, 50, 30, TypeMarchandise.FRAGILE, 800)),
                "Paris", "Lyon", ModeTransport.ROUTE, false, true).orElseThrow();
        Expedition exp2 = serviceExpedition.creerExpedition("contact@transco.fr", List.of(
                        new Colis("Fûts de solvant", 5000, 240, 120, 150, TypeMarchandise.DANGEREUX, 20000)),
                "Paris", "Marseille", ModeTransport.ROUTE, true, false).orElseThrow();
        serviceExpedition.creerExpedition("ops@logiplus.com", List.of(
                        new Colis("Conteneur machines-outils", 15000, 1200, 235, 239, TypeMarchandise.STANDARD, 250000)),
                "Le Havre", "New York", ModeTransport.MER, false, true);
        serviceExpedition.creerExpedition("jean.dupont@mail.fr", List.of(
                        new Colis("Batteries lithium", 80, 80, 60, 50, TypeMarchandise.DANGEREUX, 4000)),
                "Paris", "New York", ModeTransport.AIR, false, false); // refusée

        System.out.println("\n----- Devis -----");
        List<Colis> palette = List.of(new Colis("Palette", 800, 120, 80, 150, TypeMarchandise.STANDARD, 2000));
        System.out.println("Paris -> Bordeaux ROUTE : " + serviceExpedition.calculerDevis(palette, "Paris", "Bordeaux",
                ModeTransport.ROUTE, false, false, TypeClient.PARTICULIER).getAsDouble() + " € TTC");

        System.out.println("\n----- Cycle de vie -----");
        serviceExpedition.demarrerExpedition(exp1.getId());
        serviceExpedition.livrerExpedition(exp1.getId());
        serviceExpedition.demarrerExpedition(exp2.getId());
        serviceExpedition.annulerExpedition(exp2.getId());

        System.out.println("\n----- Factures -----");
        System.out.println(factures.generer(exp1));
        System.out.println(factures.generer(exp2));
    }
}
