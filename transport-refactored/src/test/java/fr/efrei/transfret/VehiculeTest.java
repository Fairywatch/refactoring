package fr.efrei.transfret;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehiculeTest {

    static Chargement chargement(double poids, TypeMarchandise... types) {
        return new Chargement(List.of(types).stream()
                .map(t -> new Colis("colis", poids, 50, 40, 30, t, 100)).toList());
    }

    static final Chargement STANDARD = chargement(100, TypeMarchandise.STANDARD);
    static final Chargement DANGEREUX = chargement(100, TypeMarchandise.DANGEREUX);
    static final Chargement REFRIGERE = chargement(100, TypeMarchandise.REFRIGERE);

    @Test
    void un_vehicule_ne_sert_que_son_mode() {
        assertTrue(new Camion("C", 1000, false).peutPrendreEnCharge(STANDARD, ModeTransport.ROUTE));
        assertFalse(new Camion("C", 1000, false).peutPrendreEnCharge(STANDARD, ModeTransport.RAIL));
        assertTrue(new Train("T", 1000).peutPrendreEnCharge(STANDARD, ModeTransport.RAIL));
        assertTrue(new Bateau("B", 1000, false).peutPrendreEnCharge(STANDARD, ModeTransport.MER));
        assertTrue(new Avion("A", 1000).peutPrendreEnCharge(STANDARD, ModeTransport.AIR));
    }

    @Test
    void camion_dangereux_seulement_avec_adr() {
        assertFalse(new Camion("C", 1000, false).peutPrendreEnCharge(DANGEREUX, ModeTransport.ROUTE));
        assertTrue(new Camion("C", 1000, true).peutPrendreEnCharge(DANGEREUX, ModeTransport.ROUTE));
    }

    @Test
    void camion_jamais_de_refrigere() {
        assertFalse(new Camion("C", 1000, true).peutPrendreEnCharge(REFRIGERE, ModeTransport.ROUTE));
    }

    @Test
    void camion_frigorifique_uniquement_du_refrigere() {
        CamionFrigorifique frigo = new CamionFrigorifique("F", 1000);
        assertTrue(frigo.peutPrendreEnCharge(REFRIGERE, ModeTransport.ROUTE));
        assertFalse(frigo.peutPrendreEnCharge(STANDARD, ModeTransport.ROUTE));
        // un chargement mixte est refusé en amont : plus d'exception au chargement
        assertFalse(frigo.peutPrendreEnCharge(chargement(10, TypeMarchandise.REFRIGERE, TypeMarchandise.STANDARD), ModeTransport.ROUTE));
    }

    @Test
    void bateau_refrigere_seulement_avec_conteneurs_frigorifiques() {
        assertFalse(new Bateau("B", 1000, false).peutPrendreEnCharge(REFRIGERE, ModeTransport.MER));
        assertTrue(new Bateau("B", 1000, true).peutPrendreEnCharge(REFRIGERE, ModeTransport.MER));
    }

    @Test
    void capacite_restante() {
        Camion camion = new Camion("C", 250, false);
        camion.charger(STANDARD);
        camion.charger(STANDARD);
        assertEquals(200, camion.getChargeActuelle());
        assertFalse(camion.peutPrendreEnCharge(STANDARD, ModeTransport.ROUTE));
        assertTrue(camion.peutPrendreEnCharge(chargement(50, TypeMarchandise.STANDARD), ModeTransport.ROUTE));
    }

    @Test
    void indisponible_une_fois_parti_puis_libere() {
        Camion camion = new Camion("C", 1000, false);
        camion.charger(STANDARD);
        camion.partir(465);
        assertFalse(camion.peutPrendreEnCharge(STANDARD, ModeTransport.ROUTE));
        camion.liberer(STANDARD);
        assertEquals(0, camion.getChargeActuelle());
        assertTrue(camion.peutPrendreEnCharge(STANDARD, ModeTransport.ROUTE));
    }

    @Test
    void tout_vehicule_sait_partir_sans_exception() {
        for (Vehicule v : List.of(new Camion("C", 1000, false), new CamionFrigorifique("F", 1000), new Train("T", 1000),
                new Bateau("B", 1000, true), new Avion("A", 1000))) {
            assertDoesNotThrow(() -> v.partir(100));
        }
    }
}
