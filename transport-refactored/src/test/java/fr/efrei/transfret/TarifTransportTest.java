package fr.efrei.transfret;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TarifTransportTest {

    private static final double PRECISION = 1e-9;

    static Chargement chargement(double poidsKg, double longueurCm, double largeurCm, double hauteurCm) {
        return new Chargement(List.of(new Colis("colis", poidsKg, longueurCm, largeurCm, hauteurCm, TypeMarchandise.STANDARD, 100)));
    }

    static Chargement chargement(double poidsKg) {
        return chargement(poidsKg, 10, 10, 10);
    }

    @Test
    void route_au_km_et_au_kg() {
        assertEquals(100 * 1.2 + 10 * 0.10, new TarifRoute().prixDeBase(chargement(10), 100), PRECISION);
    }

    @Test
    void rail_au_km_et_au_kg() {
        assertEquals(465 * 0.8 + 800 * 0.07, new TarifRail().prixDeBase(chargement(800), 465), PRECISION);
    }

    @Test
    void rail_minimum_150() {
        assertEquals(150, new TarifRail().prixDeBase(chargement(10), 10), PRECISION);
    }

    @Test
    void mer_au_km_et_au_kg() {
        assertEquals(5850 * 0.5 + 15000 * 0.03, new TarifMer().prixDeBase(chargement(15000), 5850), PRECISION);
    }

    @Test
    void mer_minimum_500() {
        assertEquals(500, new TarifMer().prixDeBase(chargement(10), 760), PRECISION);
    }

    @Test
    void air_au_poids_reel_quand_il_depasse_le_poids_volumetrique() {
        assertEquals(5840 * 0.6 + 100 * 4.5, new TarifAir().prixDeBase(chargement(100), 5840), PRECISION);
    }

    @Test
    void air_au_poids_volumetrique_167_kg_par_m3() {
        // 100 x 80 x 60 cm = 0,48 m3, soit 80,16 kg taxables pour 50 kg réels
        assertEquals(5840 * 0.6 + 0.48 * 167 * 4.5, new TarifAir().prixDeBase(chargement(50, 100, 80, 60), 5840), PRECISION);
    }
}
