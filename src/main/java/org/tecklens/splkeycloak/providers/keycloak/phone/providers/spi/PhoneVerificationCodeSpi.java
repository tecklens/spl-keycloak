package org.tecklens.splkeycloak.providers.keycloak.phone.providers.spi;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class PhoneVerificationCodeSpi implements Spi {

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return "phoneVerificationCode";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return PhoneVerificationCodeProvider.class;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return PhoneVerificationCodeProviderFactory.class;
    }
}