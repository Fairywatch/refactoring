package fr.efrei.transfret;

import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DelaiTransportTest {

    @Test
    void route_9_heures_par_jour_18_en_urgence() {
        assertEquals(1, new DelaiRoute().jours(465, false));
        assertEquals(2, new DelaiRoute().jours(775, false));
        assertEquals(1, new DelaiRoute().jours(775, true));
    }

    @Test
    void rail_un_jour_de_manutention_sauf_urgence() {
        assertEquals(2, new DelaiRail().jours(465, false));
        assertEquals(1, new DelaiRail().jours(465, true));
    }

    @Test
    void mer_deux_jours_portuaires() {
        assertEquals(11, new DelaiMer().jours(5850, false));
        assertEquals(4, new DelaiMer().jours(760, false));
    }

    @Test
    void air_deux_jours_en_long_courrier_sauf_urgence() {
        assertEquals(2, new DelaiAir().jours(5840, false));
        assertEquals(1, new DelaiAir().jours(5840, true));
        assertEquals(1, new DelaiAir().jours(465, false));
    }

    @Test
    void l_estimateur_garantit_au_moins_un_jour() {
        Map<ModeTransport, DelaiTransport> instantane = new EnumMap<>(ModeTransport.class);
        for (ModeTransport mode : ModeTransport.values()) {
            instantane.put(mode, (km, urgent) -> 0);
        }
        assertEquals(1, new EstimateurDelai(instantane).jours(10, ModeTransport.ROUTE, false));
    }

    @Test
    void un_mode_sans_strategie_est_refuse_des_l_assemblage() {
        assertThrows(IllegalArgumentException.class, () -> new EstimateurDelai(Map.of(ModeTransport.ROUTE, new DelaiRoute())));
    }
}
