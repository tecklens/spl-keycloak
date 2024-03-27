package org.tecklens.splkeycloak.providers.keycloak.phone.providers.constants;

public enum TokenCodeType {
    VERIFY("verification"),
    AUTH("authentication"),

    OTP("OTP"),
    RESET("reset credential"),
    REGISTRATION("registration");

    public final String label;

    TokenCodeType(String label) {
        this.label  = label;
    }
}
