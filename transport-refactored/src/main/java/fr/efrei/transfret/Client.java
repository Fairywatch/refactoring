package fr.efrei.transfret;

public class Client {

    private final String nom;
    private final String email;
    private final String telephone;
    private final String adresse;
    private final TypeClient type;
    private final String siret; // seulement pour les entreprises (null sinon)

    public Client(String nom, String email, String telephone, String adresse, TypeClient type, String siret) {
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.type = type;
        this.siret = siret;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public TypeClient getType() {
        return type;
    }

    public String getSiret() {
        return siret;
    }

    @Override
    public String toString() {
        return nom + " (" + type + ")";
    }
}
