package org.tecklens.splkeycloak.providers.keycloak.phone.providers.spi;

import org.tecklens.splkeycloak.providers.keycloak.phone.providers.constants.TokenCodeType;
import org.tecklens.splkeycloak.providers.keycloak.phone.providers.exception.MessageSendException;
import org.keycloak.provider.Provider;


/**
 * SMS, Voice, APP
 */
public interface MessageSenderService extends Provider {

    //void sendVoiceMessage((TokenCodeType type, String realmName, String realmDisplayName, String phoneNumber, String code , int expires) throws MessageSendException;


    void sendSmsMessage(TokenCodeType type, String phoneNumber, String code , int expires , String kind, String message) throws MessageSendException;
}
