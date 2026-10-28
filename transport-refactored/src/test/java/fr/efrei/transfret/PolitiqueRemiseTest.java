package fr.efrei.transfret;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PolitiqueRemiseTest {

    private static final double PRECISION = 1e-9;

    @Test
    void particulier_sans_remise() {
        assertEquals(1000, new AucuneRemise().appliquer(1000), PRECISION);
    }

    @Test
    void entreprise_10_pourcent() {
        assertEquals(900, new RemiseEntreprise().appliquer(1000), PRECISION);
    }

    @Test
    void entreprise_15_pourcent_strictement_au_dela_de_5000() {
        assertEquals(4500, new RemiseEntreprise().appliquer(5000), PRECISION);
        assertEquals(5100, new RemiseEntreprise().appliquer(6000), PRECISION);
    }

    @Test
    void premium_20_pourcent() {
        assertEquals(800, new RemisePremium().appliquer(1000), PRECISION);
    }
}
