package org.tecklens.splkeycloak.providers.keycloak.phone.authentication.authenticators.conditional;

import org.tecklens.splkeycloak.providers.common.OptionalUtils;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.authentication.authenticators.conditional.ConditionalUserAttributeValueFactory;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionalPhoneProvided implements  ConditionalAuthenticator {

  static final ConditionalPhoneProvided SINGLETON = new ConditionalPhoneProvided();

  Logger logger = LoggerFactory.getLogger(ConditionalPhoneProvided.class);

  @Override
  public boolean matchCondition(AuthenticationFlowContext context) {
//    AuthenticatorConfigModel configModel = context.getAuthenticatorConfig();
//    if (configModel == null) return false;
//    var config = configModel.getConfig();
//    logger.info(config.toString());
//    boolean negateOutput = Boolean.parseBoolean(config.getOrDefault(ConditionalUserAttributeValueFactory.CONF_NOT,"false"));

    boolean result = OptionalUtils.ofBlank(OptionalUtils.ofBlank(
            context.getHttpRequest().getDecodedFormParameters().getFirst("phone_number"))
        .orElse(context.getHttpRequest().getDecodedFormParameters().getFirst("phoneNumber"))).isPresent();

    boolean resultOtp = OptionalUtils.ofBlank(OptionalUtils.ofBlank(
                    context.getHttpRequest().getDecodedFormParameters().getFirst("otp"))
            .orElse(context.getHttpRequest().getDecodedFormParameters().getFirst("OTP"))).isPresent();
    logger.info("matchCondition:" + result + ":" + resultOtp);
    return result && resultOtp;
  }

  @Override
  public void action(AuthenticationFlowContext authenticationFlowContext) {
    // Not used
  }

  @Override
  public boolean requiresUser() {
    return false;
  }

  @Override
  public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
    // Not used
  }

  @Override
  public void close() {
    // Does nothing
  }
}
