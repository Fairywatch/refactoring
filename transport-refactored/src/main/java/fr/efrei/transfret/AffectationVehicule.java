package fr.efrei.transfret;

import java.util.List;
import java.util.Optional;

public class AffectationVehicule {

    private final List<Vehicule> flotte;

    public AffectationVehicule(List<Vehicule> flotte) {
        this.flotte = List.copyOf(flotte);
    }

    /** @return le premier véhicule de la flotte qui accepte ce chargement pour ce mode */
    public Optional<Vehicule> trouverVehicule(Chargement chargement, ModeTransport mode) {
        return flotte.stream().filter(v -> v.peutPrendreEnCharge(chargement, mode)).findFirst();
    }
}
