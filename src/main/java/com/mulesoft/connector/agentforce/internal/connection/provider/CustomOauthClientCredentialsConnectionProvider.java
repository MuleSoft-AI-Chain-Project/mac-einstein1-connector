package com.mulesoft.connector.agentforce.internal.connection.provider;

import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.connection.CustomOAuthClientCredentialsConnection;
import com.mulesoft.connector.agentforce.internal.dto.OAuthResponseDTO;
import com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.connectivity.oauth.ClientCredentialsState;
import org.mule.runtime.extension.api.annotation.connectivity.oauth.OAuthParameter;
import org.mule.runtime.extension.api.annotation.connectivity.oauth.ClientCredentials;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.connectivity.oauth.OAuthCallbackValue;
import org.mule.runtime.extension.api.annotation.Alias;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Alias("oauth-client-credentials")
@DisplayName("OAuth Client Credentials")
@ClientCredentials(tokenUrl = "https://{salesforceorg}/services/oauth2/token")
public class CustomOauthClientCredentialsConnectionProvider implements AgentforceConnectionProvider,
    CachedConnectionProvider<AgentforceConnection> {

  private final static Logger logger = LoggerFactory.getLogger(CustomOauthClientCredentialsConnectionProvider.class);
  private ClientCredentialsState clientCredentialsState;
  @OAuthCallbackValue(expression = "#[payload.instance_url]")
  private String salesforceOrg;
  @OAuthCallbackValue(expression = "#[payload.api_instance_url]")
  private String apiInstanceUrl;
  @OAuthCallbackValue(expression = "#[payload.id]")
  private String id;

  @Override
  public AgentforceConnection connect() {
    logger.info("Inside CustomOauthClientCredentialsConnectionProvider connect, salesforceOrg {}, apiInstanceUrl = {}," +
        " id = {} ", salesforceOrg, apiInstanceUrl, id);

    OAuthResponseDTO oAuthResponseDTO = new OAuthResponseDTO(apiInstanceUrl, id, salesforceOrg);
    return new CustomOAuthClientCredentialsConnection(salesforceOrg, oAuthResponseDTO, clientCredentialsState);
  }

  public void setClientCredentialsState(ClientCredentialsState clientCredentialsState) {
    this.clientCredentialsState = clientCredentialsState;
  }
}
