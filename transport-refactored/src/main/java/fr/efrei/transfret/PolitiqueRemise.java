package fr.efrei.transfret;

/**
 * Strategy : la remise accordée selon le type de client.
 */
public interface PolitiqueRemise {

    double appliquer(double prix);
}
