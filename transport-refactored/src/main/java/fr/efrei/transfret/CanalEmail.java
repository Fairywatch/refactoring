package fr.efrei.transfret;

public interface CanalEmail {

    void envoyer(String destinataire, String sujet, String corps);
}
