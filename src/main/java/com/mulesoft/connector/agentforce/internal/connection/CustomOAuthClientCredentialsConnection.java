package com.mulesoft.connector.agentforce.internal.connection;

import com.mulesoft.connector.agentforce.internal.botapi.helpers.BotRequestHelper;
import com.mulesoft.connector.agentforce.internal.error.AgentforceErrorType;
import org.mule.runtime.extension.api.connectivity.oauth.ClientCredentialsState;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomOAuthClientCredentialsConnection implements AgentforceConnection {

  private static final Logger logger = LoggerFactory.getLogger(CustomOAuthClientCredentialsConnection.class);

  private final ClientCredentialsState clientCredentialsState;
  private final String salesforceOrgUrl;
  private final String apiInstanceUrl;
  private final String orgId;
  private final BotRequestHelper botRequestHelper;

  public CustomOAuthClientCredentialsConnection(String salesforceOrgUrl, ClientCredentialsState clientCredentialsState,
                                                String apiInstanceUrl, String orgId) {
    this.salesforceOrgUrl = salesforceOrgUrl;
    this.clientCredentialsState = clientCredentialsState;
    this.apiInstanceUrl = apiInstanceUrl;
    this.orgId = parseOrgId(orgId);
    this.botRequestHelper = new BotRequestHelper(this);
  }

  @Override
  public void disconnect() {
    // Nothing to dispose
    logger.info("Inside CustomOAuthClientCredentialsConnection disconnect");
  }

  @Override
  public void validate() {
    try {
      logger.info("Inside CustomOAuthClientCredentialsConnection validate, salesforceOrg {}", salesforceOrgUrl);
      botRequestHelper.getAgentList();
    } catch (IOException e) {
      throw new ModuleException("Unable to validate credentials", AgentforceErrorType.INVALID_CONNECTION, e);

    }
  }

  public String getSalesforceOrgUrl() {
    return salesforceOrgUrl;
  }

  public String getApiInstanceUrl() {
    return apiInstanceUrl;
  }

  public String getOrgId() {
    return orgId;
  }

  @Override
  public BotRequestHelper getBotRequestHelper() {
    return botRequestHelper;
  }

  @Override
  public String getAccessToken() {
    return clientCredentialsState.getAccessToken();
  }

  private String parseOrgId(String id) {
    String[] idArr = id.split("/");
    return idArr[idArr.length - 2];
  }
}
