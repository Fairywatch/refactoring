package fr.efrei.transfret;

import java.util.Map;

/**
 * Applique la politique de remise du type de client (Strategy) au prix enveloppé.
 */
public class RemiseClient extends DecorateurPrix {

    private final Map<TypeClient, PolitiqueRemise> remises;

    public RemiseClient(CalculPrix enveloppe, Map<TypeClient, PolitiqueRemise> remises) {
        super(enveloppe);
        this.remises = Strategies.pourChaque(TypeClient.class, remises);
    }

    @Override
    protected double ajuster(double prix, DemandeTarif demande) {
        return remises.get(demande.typeClient()).appliquer(prix);
    }
}
