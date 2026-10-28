package fr.efrei.transfret;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecorateurPrixTest {

    private static final double PRECISION = 1e-9;
    private static final Map<TypeClient, PolitiqueRemise> REMISES = Map.of(
            TypeClient.PARTICULIER, new AucuneRemise(),
            TypeClient.ENTREPRISE, new RemiseEntreprise(),
            TypeClient.PREMIUM, new RemisePremium());

    static CalculPrix prixFixe(double prix) {
        return demande -> prix;
    }

    static DemandeTarif demande(TypeMarchandise type, boolean urgent, boolean assurance, TypeClient client, double valeur) {
        Colis colis = new Colis("colis", 10, 50, 40, 30, type, valeur);
        return demande(new Chargement(List.of(colis)), urgent, assurance, client);
    }

    static DemandeTarif demande(Chargement chargement, boolean urgent, boolean assurance, TypeClient client) {
        return new DemandeTarif(new Commande(chargement, "Paris", "Lille", ModeTransport.ROUTE, urgent, assurance), 100, client);
    }

    static DemandeTarif demande(TypeMarchandise type) {
        return demande(type, false, false, TypeClient.PARTICULIER, 100);
    }

    @Test
    void fragile_15_pourcent_seulement_si_un_colis_est_fragile() {
        assertEquals(115, new SupplementFragile(prixFixe(100)).calculer(demande(TypeMarchandise.FRAGILE)), PRECISION);
        assertEquals(100, new SupplementFragile(prixFixe(100)).calculer(demande(TypeMarchandise.STANDARD)), PRECISION);
    }

    @Test
    void dangereux_30_pourcent() {
        assertEquals(130, new SupplementDangereux(prixFixe(100)).calculer(demande(TypeMarchandise.DANGEREUX)), PRECISION);
        assertEquals(100, new SupplementDangereux(prixFixe(100)).calculer(demande(TypeMarchandise.FRAGILE)), PRECISION);
    }

    @Test
    void refrigere_20_pourcent() {
        assertEquals(120, new SupplementRefrigere(prixFixe(100)).calculer(demande(TypeMarchandise.REFRIGERE)), PRECISION);
        assertEquals(100, new SupplementRefrigere(prixFixe(100)).calculer(demande(TypeMarchandise.STANDARD)), PRECISION);
    }

    @Test
    void urgence_50_pourcent() {
        assertEquals(150, new SupplementUrgence(prixFixe(100)).calculer(demande(TypeMarchandise.STANDARD, true, false, TypeClient.PARTICULIER, 100)), PRECISION);
        assertEquals(100, new SupplementUrgence(prixFixe(100)).calculer(demande(TypeMarchandise.STANDARD)), PRECISION);
    }

    @Test
    void remise_selon_le_type_de_client() {
        assertEquals(900, new RemiseClient(prixFixe(1000), REMISES).calculer(demande(TypeMarchandise.STANDARD, false, false, TypeClient.ENTREPRISE, 100)), PRECISION);
        assertEquals(800, new RemiseClient(prixFixe(1000), REMISES).calculer(demande(TypeMarchandise.STANDARD, false, false, TypeClient.PREMIUM, 100)), PRECISION);
        assertEquals(1000, new RemiseClient(prixFixe(1000), REMISES).calculer(demande(TypeMarchandise.STANDARD)), PRECISION);
    }

    @Test
    void assurance_2_pourcent_de_la_valeur_minimum_15() {
        assertEquals(100 + 400, new Assurance(prixFixe(100)).calculer(demande(TypeMarchandise.STANDARD, false, true, TypeClient.PARTICULIER, 20000)), PRECISION);
        assertEquals(100 + 15, new Assurance(prixFixe(100)).calculer(demande(TypeMarchandise.STANDARD, false, true, TypeClient.PARTICULIER, 100)), PRECISION);
        assertEquals(100, new Assurance(prixFixe(100)).calculer(demande(TypeMarchandise.STANDARD, false, false, TypeClient.PARTICULIER, 20000)), PRECISION);
    }

    @Test
    void les_supplements_se_cumulent() {
        Colis fragile = new Colis("fragile", 10, 50, 40, 30, TypeMarchandise.FRAGILE, 100);
        Colis dangereux = new Colis("dangereux", 10, 50, 40, 30, TypeMarchandise.DANGEREUX, 100);
        DemandeTarif demande = demande(new Chargement(List.of(fragile, dangereux)), false, false, TypeClient.PARTICULIER);
        assertEquals(100 * 1.15 * 1.30, new SupplementDangereux(new SupplementFragile(prixFixe(100))).calculer(demande), PRECISION);
    }

    @Test
    void la_remise_entreprise_s_applique_apres_l_urgence() {
        // 4 000 € urgents = 6 000 € : on passe le seuil des 5 000 €, donc 15 % de remise
        DemandeTarif demande = demande(TypeMarchandise.STANDARD, true, false, TypeClient.ENTREPRISE, 100);
        assertEquals(6000 * 0.85, new RemiseClient(new SupplementUrgence(prixFixe(4000)), REMISES).calculer(demande), PRECISION);
    }
}
