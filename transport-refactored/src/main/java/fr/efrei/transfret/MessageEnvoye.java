package fr.efrei.transfret;

/**
 * Trace d'un message parti : l'objet est le sujet d'un email ou le texte d'un SMS.
 */
public record MessageEnvoye(String destinataire, String objet) {
}
