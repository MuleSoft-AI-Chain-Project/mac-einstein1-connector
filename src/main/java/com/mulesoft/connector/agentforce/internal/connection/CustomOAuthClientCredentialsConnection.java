package com.mulesoft.connector.agentforce.internal.connection;

import com.mulesoft.connector.agentforce.internal.botapi.helpers.BotRequestHelper;
import com.mulesoft.connector.agentforce.internal.dto.OAuthResponseDTO;
import com.mulesoft.connector.agentforce.internal.modelsapi.helpers.RequestHelper;
import com.mulesoft.connector.agentforce.internal.modelsapi.helpers.chatmemory.ChatMemoryHelper;
import org.mule.runtime.extension.api.connectivity.oauth.ClientCredentialsState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomOAuthClientCredentialsConnection implements AgentforceConnection {

  private final static Logger logger = LoggerFactory.getLogger(AgentforceConnection.class);

  private ClientCredentialsState clientCredentialsState;
  private String salesforceOrg;
  private OAuthResponseDTO oAuthResponseDTO;
  private RequestHelper requestHelper;
  private ChatMemoryHelper chatMemoryHelper;

  private BotRequestHelper botRequestHelper;

  public CustomOAuthClientCredentialsConnection(String salesforceOrg, OAuthResponseDTO oAuthResponseDTO,
                                                ClientCredentialsState clientCredentialsState) {
    this.salesforceOrg = salesforceOrg;
    this.oAuthResponseDTO = oAuthResponseDTO;
    this.clientCredentialsState = clientCredentialsState;
    this.requestHelper = new RequestHelper(this);
    this.chatMemoryHelper = new ChatMemoryHelper(requestHelper);
    this.botRequestHelper = new BotRequestHelper(this);
  }

  @Override
  public void disconnect() {
    // Nothing to dispose
    System.out.println("Inside disconnect");
  }

  @Override
  public void validate() {
    try {
      botRequestHelper.getAgentList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public OAuthResponseDTO getoAuthResponseDTO() {
    return oAuthResponseDTO;
  }

  public String getSalesforceOrg() {
    return salesforceOrg;
  }

  public ClientCredentialsState getClientCredentialsState() {
    return clientCredentialsState;
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
}
