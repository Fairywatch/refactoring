package fr.efrei.transfret;

import java.util.List;
import java.util.Optional;

public class AffectationVehicule {

    private final List<Vehicule> flotte;

    public AffectationVehicule(List<Vehicule> flotte) {
        this.flotte = List.copyOf(flotte);
    }

    /** @return le premier véhicule disponible et compatible */
    public Optional<Vehicule> trouverVehicule(Chargement chargement, ModeTransport mode) {
        double poids = chargement.poids();
        boolean dangereux = chargement.contient(TypeMarchandise.DANGEREUX);
        boolean refrigere = chargement.contient(TypeMarchandise.REFRIGERE);
        for (Vehicule v : flotte) {
            if (!v.estDisponible()) continue;
            if (v.getCapaciteMax() - v.getChargeActuelle() < poids) continue;
            if (mode == ModeTransport.ROUTE) {
                if (v instanceof Camion) {
                    if (refrigere) {
                        if (v instanceof CamionFrigorifique) {
                            return Optional.of(v);
                        }
                    } else if (v instanceof CamionFrigorifique) {
                        // ne pas prendre le frigo pour autre chose, sinon ça plante au chargement
                        continue;
                    } else if (dangereux) {
                        if (v.peutTransporterMatieresDangereuses()) {
                            return Optional.of(v);
                        }
                    } else {
                        return Optional.of(v);
                    }
                }
            } else if (mode == ModeTransport.RAIL) {
                if (v instanceof Train) {
                    return Optional.of(v);
                }
            } else if (mode == ModeTransport.MER) {
                if (v instanceof Bateau) {
                    if (refrigere && !((Bateau) v).aConteneursRefrigeres()) {
                        continue;
                    }
                    return Optional.of(v);
                }
            } else if (mode == ModeTransport.AIR) {
                if (v instanceof Avion) {
                    return Optional.of(v);
                }
            }
        }
        return Optional.empty();
    }
}
