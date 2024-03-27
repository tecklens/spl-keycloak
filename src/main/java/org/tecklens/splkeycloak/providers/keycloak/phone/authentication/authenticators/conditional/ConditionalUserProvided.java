package org.tecklens.splkeycloak.providers.keycloak.phone.authentication.authenticators.conditional;

import org.tecklens.splkeycloak.providers.common.OptionalUtils;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionalUserProvided implements  ConditionalAuthenticator {

  static final ConditionalUserProvided SINGLETON = new ConditionalUserProvided();

  Logger logger = LoggerFactory.getLogger(ConditionalUserProvided.class);

  @Override
  public boolean matchCondition(AuthenticationFlowContext context) {
//    AuthenticatorConfigModel configModel = context.getAuthenticatorConfig();
//    if (configModel == null) return false;
//    var config = configModel.getConfig();
//    logger.info(config.toString());
//    boolean negateOutput = Boolean.parseBoolean(config.getOrDefault(ConditionalUserAttributeValueFactory.CONF_NOT,"false"));

    boolean result = OptionalUtils.ofBlank(OptionalUtils.ofBlank(
            context.getHttpRequest().getDecodedFormParameters().getFirst("username"))
        .orElse(context.getHttpRequest().getDecodedFormParameters().getFirst("userName"))).isPresent();

    boolean resultOtp = OptionalUtils.ofBlank(OptionalUtils.ofBlank(
                    context.getHttpRequest().getDecodedFormParameters().getFirst("otp"))
            .orElse(context.getHttpRequest().getDecodedFormParameters().getFirst("OTP"))).isPresent();
    logger.info("matchCondition:" + result + ":" + !resultOtp);
    return result && (!resultOtp);
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
