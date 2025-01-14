package com.mulesoft.connector.agentforce.internal.connection;

import com.mulesoft.connector.agentforce.internal.botapi.helpers.BotRequestHelper;
import com.mulesoft.connector.agentforce.internal.botapi.helpers.BotRequestHelperImpl;
import com.mulesoft.connector.agentforce.internal.dto.OAuthResponseDTO;
import com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper;
import com.mulesoft.connector.agentforce.internal.modelsapi.helpers.RequestHelper;
import com.mulesoft.connector.agentforce.internal.modelsapi.helpers.RequestHelperImpl;
import com.mulesoft.connector.agentforce.internal.modelsapi.helpers.chatmemory.ChatMemoryHelper;
import com.mulesoft.connector.agentforce.internal.modelsapi.helpers.chatmemory.ChatMemoryHelperImpl;
import org.mule.runtime.extension.api.annotation.connectivity.oauth.ClientCredentials;
import org.mule.runtime.extension.api.connectivity.oauth.ClientCredentialsState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomOAuthClientCredentialsConnection implements AgentforceConnection {

  private final static Logger logger = LoggerFactory.getLogger(AgentforceConnection.class);

  private ClientCredentialsState clientCredentialsState;

  private String salesforceOrg;
  private String clientId;
  private String clientSecret;
  private OAuthResponseDTO oAuthResponseDTO;
  private RequestHelper requestHelper;
  private ChatMemoryHelper chatMemoryHelper;

  private BotRequestHelper botRequestHelper;

  public CustomOAuthClientCredentialsConnection(String salesforceOrg, OAuthResponseDTO oAuthResponseDTO,
                                                ClientCredentialsState clientCredentialsState) {
    this.salesforceOrg = salesforceOrg;
    this.oAuthResponseDTO = oAuthResponseDTO;
    this.clientCredentialsState = clientCredentialsState;
    this.requestHelper = new RequestHelperImpl(this);
    this.chatMemoryHelper = new ChatMemoryHelperImpl(requestHelper);
    this.botRequestHelper = new BotRequestHelperImpl(this);
    System.out.println("CustomOAuthClientCredentialsConnection constructor");
  }

 /* public CustomOAuthClientCredentialsConnection(String salesforceOrg, String clientId, String clientSecret,
                                                OAuthResponseDTO oAuthResponseDTO) {
    this.salesforceOrg = salesforceOrg;
    this.oAuthResponseDTO = oAuthResponseDTO;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.requestHelper = new RequestHelperImpl(this);
    this.chatMemoryHelper = new ChatMemoryHelperImpl(requestHelper);
    this.botRequestHelper = new BotRequestHelperImpl(this);
    System.out.println("CustomOAuthClientCredentialsConnection constructor");
  }*/

  @Override
  public void disconnect() {
    // Nothing to dispose
    System.out.println("Inside disconnect");
  }

  @Override
  public void validate() {
//
  }

  public OAuthResponseDTO getoAuthResponseDTO() {
    return oAuthResponseDTO;
  }

  public String getSalesforceOrg() {
    return salesforceOrg;
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
}
