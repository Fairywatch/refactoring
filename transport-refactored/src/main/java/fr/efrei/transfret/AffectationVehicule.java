package fr.efrei.transfret;

public class AffectationVehicule {

    // retourne le premier véhicule disponible et compatible, null si aucun
    public Vehicule trouverVehicule(Chargement chargement, ModeTransport mode) {
        double poids = chargement.poids();
        boolean dangereux = chargement.contient(TypeMarchandise.DANGEREUX);
        boolean refrigere = chargement.contient(TypeMarchandise.REFRIGERE);
        for (Vehicule v : Flotte.vehicules) {
            if (!v.estDisponible()) continue;
            if (v.getCapaciteMax() - v.getChargeActuelle() < poids) continue;
            if (mode == ModeTransport.ROUTE) {
                if (v instanceof Camion) {
                    if (refrigere) {
                        if (v instanceof CamionFrigorifique) {
                            return v;
                        }
                    } else if (v instanceof CamionFrigorifique) {
                        // ne pas prendre le frigo pour autre chose, sinon ça plante au chargement
                        continue;
                    } else if (dangereux) {
                        if (v.peutTransporterMatieresDangereuses()) {
                            return v;
                        }
                    } else {
                        return v;
                    }
                }
            } else if (mode == ModeTransport.RAIL) {
                if (v instanceof Train) {
                    return v;
                }
            } else if (mode == ModeTransport.MER) {
                if (v instanceof Bateau) {
                    if (refrigere && !((Bateau) v).aConteneursRefrigeres()) {
                        continue;
                    }
                    return v;
                }
            } else if (mode == ModeTransport.AIR) {
                if (v instanceof Avion) {
                    return v;
                }
            }
        }
        return null;
    }
}
