package org.tecklens.splkeycloak.providers.keycloak.phone.providers.spi;

import org.tecklens.splkeycloak.providers.keycloak.phone.providers.constants.TokenCodeType;
import org.tecklens.splkeycloak.providers.keycloak.phone.providers.exception.MessageSendException;

public abstract class FullSmsSenderAbstractService implements MessageSenderService{

    private final String realmDisplay;

    public FullSmsSenderAbstractService(String realmDisplay) {
        this.realmDisplay = realmDisplay;
    }

    public abstract void sendMessage(String phoneNumber, String message) throws MessageSendException;


    @Override
    public void sendSmsMessage(TokenCodeType type, String phoneNumber, String code , int expires, String kind, String message) throws MessageSendException{
        // * template from keycloak message bundle
//        final String MESSAGE = String.format("[%s] - " + type.label + " code: %s, expires: %s minute ",realmDisplay , code, expires / 60);
        final String MESSAGE = String.format(message == null ? "AIO: QK nhap ma OTP %s de dang nhap tai khoan tren ung dung AIO. Ma xac thuc co hieu luc trong vong %s phut. LH 1900989868 (1000VND/phut)." : message, code, expires / 60);
        sendMessage(phoneNumber,MESSAGE);
    }
}
