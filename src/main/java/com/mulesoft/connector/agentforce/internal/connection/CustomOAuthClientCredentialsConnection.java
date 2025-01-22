package com.mulesoft.connector.agentforce.internal.connection;

import com.mulesoft.connector.agentforce.internal.botapi.helpers.BotRequestHelper;
import com.mulesoft.connector.agentforce.internal.modelsapi.helpers.RequestHelper;
import com.mulesoft.connector.agentforce.internal.modelsapi.helpers.chatmemory.ChatMemoryHelper;
import org.mule.runtime.extension.api.connectivity.oauth.ClientCredentialsState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomOAuthClientCredentialsConnection implements AgentforceConnection {

  private static final Logger logger = LoggerFactory.getLogger(CustomOAuthClientCredentialsConnection.class);

  private final ClientCredentialsState clientCredentialsState;
  private final String salesforceOrgUrl;
  private final String apiInstanceUrl;
  private final String orgId;
  private final RequestHelper requestHelper;
  private final ChatMemoryHelper chatMemoryHelper;

  private final BotRequestHelper botRequestHelper;

  public CustomOAuthClientCredentialsConnection(String salesforceOrgUrl, ClientCredentialsState clientCredentialsState,
                                                String apiInstanceUrl, String orgId) {
    this.salesforceOrgUrl = salesforceOrgUrl;
    this.clientCredentialsState = clientCredentialsState;
    this.apiInstanceUrl = apiInstanceUrl;
    this.orgId = parseOrgId(orgId);
    this.requestHelper = new RequestHelper(this);
    this.chatMemoryHelper = new ChatMemoryHelper(requestHelper);
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
      botRequestHelper.findRuntimeBaseUrl();
    } catch (IOException e) {
      throw new RuntimeException(e);
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
  public RequestHelper getRequestHelper() {
    return requestHelper;
  }

  @Override
  public ChatMemoryHelper getChatMemoryHelper() {
    return chatMemoryHelper;
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
