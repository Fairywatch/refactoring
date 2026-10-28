package fr.efrei.transfret;

import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Étape 8 : le mode FLUVIAL et le supplément « hors gabarit », de la classe isolée jusqu'au système assemblé.
 */
class ExtensionTest {

    private static final double PRECISION = 1e-9;

    static Colis colis(double poids, TypeMarchandise type) {
        return new Colis("colis", poids, 50, 40, 30, type, 100);
    }

    static Colis colisLong(double longueurCm) {
        return new Colis("poutre", 100, longueurCm, 40, 30, TypeMarchandise.STANDARD, 100);
    }

    // ----- classes isolées

    @Test
    void tarif_fluvial_au_km_et_au_kg() {
        assertEquals(365 * 0.40 + 20000 * 0.02, new TarifFluvial().prixDeBase(new Chargement(List.of(colis(20000, TypeMarchandise.STANDARD))), 365), PRECISION);
    }

    @Test
    void tarif_fluvial_minimum_300() {
        assertEquals(300, new TarifFluvial().prixDeBase(new Chargement(List.of(colis(1000, TypeMarchandise.STANDARD))), 365), PRECISION);
    }

    @Test
    void delai_fluvial_360_km_par_jour_plus_un() {
        assertEquals(3, new DelaiFluvial().jours(365, false));
        assertEquals(2, new DelaiFluvial().jours(360, false));
    }

    @Test
    void la_peniche_ne_sert_que_le_fluvial() {
        Chargement standard = new Chargement(List.of(colis(1000, TypeMarchandise.STANDARD)));
        assertTrue(new Peniche("P", 2500000).peutPrendreEnCharge(standard, ModeTransport.FLUVIAL));
        assertTrue(!new Peniche("P", 2500000).peutPrendreEnCharge(standard, ModeTransport.ROUTE));
    }

    @Test
    void hors_gabarit_10_pourcent_strictement_au_dela_de_200_cm() {
        CalculPrix base = demande -> 100;
        assertEquals(110, new SupplementHorsGabarit(base).calculer(demande(colisLong(201))), PRECISION);
        assertEquals(100, new SupplementHorsGabarit(base).calculer(demande(colisLong(200))), PRECISION);
    }

    static DemandeTarif demande(Colis colis) {
        return new DemandeTarif(fluvial(colis, false), 100, TypeClient.PARTICULIER);
    }

    static Commande fluvial(Colis colis, boolean urgent) {
        return new Commande(new Chargement(List.of(colis)), "Paris", "Le Havre", ModeTransport.FLUVIAL, urgent, false);
    }

    // ----- système assemblé

    static ServiceExpedition expeditions(TransFret transfret) {
        transfret.serviceClient().inscrire(new Client("Jean Dupont", "jean@mail.fr", "06", "Paris", TypeClient.PARTICULIER, null));
        return transfret.serviceExpedition();
    }

    @Test
    void une_expedition_fluviale_paris_le_havre() {
        Expedition e = expeditions(new TransFret()).creerExpedition("jean@mail.fr", fluvial(colis(20000, TypeMarchandise.STANDARD), false)).orElseThrow();
        assertEquals("PEN-0001", e.getVehicule().getImmatriculation());
        assertEquals(546.00, e.getPrixHT(), PRECISION);
        assertEquals(3, ChronoUnit.DAYS.between(e.getDateCreation(), e.getDateLivraisonPrevue()));
    }

    @Test
    void fluvial_ni_refrigere_ni_urgent() {
        ServiceExpedition service = expeditions(new TransFret());
        assertTrue(service.creerExpedition("jean@mail.fr", fluvial(colis(100, TypeMarchandise.REFRIGERE), false)).isEmpty());
        assertTrue(service.creerExpedition("jean@mail.fr", fluvial(colis(100, TypeMarchandise.STANDARD), true)).isEmpty());
    }

    @Test
    void hors_gabarit_seulement_si_active_a_l_assemblage() {
        // Paris -> Lyon, 100 kg : 465 x 1,2 + 100 x 0,10 = 568 € HT
        Commande poutre = new Commande(new Chargement(List.of(colisLong(250))), "Paris", "Lyon", ModeTransport.ROUTE, false, false);
        double actuel = new TransFret().serviceExpedition().calculerDevis(poutre, TypeClient.PARTICULIER).getAsDouble();
        double avecHorsGabarit = new TransFret(true).serviceExpedition().calculerDevis(poutre, TypeClient.PARTICULIER).getAsDouble();
        assertEquals(Montants.ttc(568), actuel, PRECISION);
        assertEquals(Montants.ttc(568 * 1.10), avecHorsGabarit, PRECISION);
    }
}
