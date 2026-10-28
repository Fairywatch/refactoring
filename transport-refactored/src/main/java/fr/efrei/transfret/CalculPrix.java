package fr.efrei.transfret;

/**
 * Interface commune au tarif de base et à ses décorateurs (suppléments, remise, assurance).
 */
@FunctionalInterface
public interface CalculPrix {

    double calculer(DemandeTarif demande);
}
