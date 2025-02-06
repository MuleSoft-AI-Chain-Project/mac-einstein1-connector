package com.mulesoft.connector.einstein.internal.connection;

import com.mulesoft.connector.einstein.internal.modelsapi.helpers.RequestHelper;
import com.mulesoft.connector.einstein.internal.modelsapi.helpers.chatmemory.ChatMemoryHelper;
import org.mule.runtime.extension.api.connectivity.oauth.ClientCredentialsState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomOAuthClientCredentialsConnection implements EinsteinConnection {

  private static final Logger logger = LoggerFactory.getLogger(CustomOAuthClientCredentialsConnection.class);

  private final ClientCredentialsState clientCredentialsState;
  private final String salesforceOrgUrl;
  private final String apiInstanceUrl;
  private final String orgId;
  private final RequestHelper requestHelper;
  private final ChatMemoryHelper chatMemoryHelper;

  public CustomOAuthClientCredentialsConnection(String salesforceOrgUrl, ClientCredentialsState clientCredentialsState,
                                                String apiInstanceUrl, String orgId) {
    this.salesforceOrgUrl = salesforceOrgUrl;
    this.clientCredentialsState = clientCredentialsState;
    this.apiInstanceUrl = apiInstanceUrl;
    this.orgId = parseOrgId(orgId);
    this.requestHelper = new RequestHelper(this);
    this.chatMemoryHelper = new ChatMemoryHelper(requestHelper);
  }

  @Override
  public void disconnect() {
    // Nothing to dispose
    logger.info("Inside CustomOAuthClientCredentialsConnection disconnect");
  }

  @Override
  public void validate() {
    /*
     * try { logger.info("Inside CustomOAuthClientCredentialsConnection validate, salesforceOrg {}", salesforceOrgUrl);
     * botRequestHelper.getAgentList(); } catch (IOException e) { throw new ModuleException("Unable to validate credentials",
     * AgentforceErrorType.INVALID_CONNECTION, e); }
     */
  }

  public String getApiInstanceUrl() {
    return apiInstanceUrl;
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
  public String getAccessToken() {
    return clientCredentialsState.getAccessToken();
  }

  private String parseOrgId(String id) {
    String[] idArr = id.split("/");
    return idArr[idArr.length - 2];
  }
}
