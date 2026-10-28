package fr.efrei.transfret;

/**
 * Les types de client et ce qui les distingue. La remise de chaque type est une stratégie injectée par
 * {@link TransFret}.
 */
public enum TypeClient {
    //         SIRET obligatoire, notifié par SMS
    PARTICULIER(false, false),
    ENTREPRISE(true, false),
    PREMIUM(false, true);

    private final boolean siretObligatoire;
    private final boolean notifieParSms;

    TypeClient(boolean siretObligatoire, boolean notifieParSms) {
        this.siretObligatoire = siretObligatoire;
        this.notifieParSms = notifieParSms;
    }

    public boolean siretObligatoire() {
        return siretObligatoire;
    }

    public boolean notifieParSms() {
        return notifieParSms;
    }
}
