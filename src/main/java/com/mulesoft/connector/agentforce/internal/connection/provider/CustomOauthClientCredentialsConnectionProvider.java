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
@ClientCredentials(tokenUrl = "https://{domain}.auth.marketingcloudapis.com/v2/token")
public class CustomOauthClientCredentialsConnectionProvider implements AgentforceConnectionProvider,
        CachedConnectionProvider<AgentforceConnection>  {

  private final static Logger logger = LoggerFactory.getLogger(CustomOauthClientCredentialsConnectionProvider.class);
  private ClientCredentialsState clientCredentialsState;

  /*@Parameter
  @Placement(order = 1, tab = Placement.CONNECTION_TAB)
  @DisplayName("Salesforce Org URL")
  @Example("mydomain.my.salesforce.com")*/
  @OAuthCallbackValue(expression = "#[payload.instance_url]")
  private String salesforceOrg;

  /*@Parameter
  @Placement(order = 2, tab = Placement.CONNECTION_TAB)
  @DisplayName("Client ID")
  private String clientId;*/

  /*@Password
  @Parameter
  @Placement(order = 3, tab = Placement.CONNECTION_TAB)
  @DisplayName("Client Secret")
  private String clientSecret;*/

  @OAuthCallbackValue(expression = "#[payload.api_instance_url]")
  private String apiInstanceUrl;

  @OAuthCallbackValue(expression = "#[payload.id]")
  private String id;

  @Override
  public AgentforceConnection connect() throws ConnectionException {
    logger.info("Inside CustomOauthClientCredentialsConnectionProvider connect, salesforceOrg {}, apiInstanceUrl = {}," +
                    " id = {} ", salesforceOrg, apiInstanceUrl, id);
    System.out.println("Inside CustomOauthClientCredentialsConnectionProvider connect, salesforceOrg = "+salesforceOrg +
            ", apiInstanceUrl = "+apiInstanceUrl+", id= "+id+", clientCredentialsState.getAccessToken() = "+clientCredentialsState.getAccessToken());

    OAuthResponseDTO oAuthResponseDTO = new OAuthResponseDTO(clientCredentialsState.getAccessToken(), apiInstanceUrl, id, salesforceOrg);
    return new CustomOAuthClientCredentialsConnection(salesforceOrg, oAuthResponseDTO, clientCredentialsState);

   /* try {
      OAuthResponseDTO oAuthResponseDTO = CommonRequestHelper.getOAuthResponseDTO(salesforceOrg, clientId, clientSecret);
      if (oAuthResponseDTO != null) {
        return new CustomOAuthClientCredentialsConnection(salesforceOrg, clientId, clientSecret, oAuthResponseDTO);
      } else {
        throw new ConnectionException("Failed to connect to Salesforce: HTTP ");
      }
    } catch (IOException | ConnectionException e) {
      logger.debug("Failed to connect to Salesforce: ", e);
      throw new ConnectionException("Failed to connect to Salesforce");
    }*/
  }


    public ClientCredentialsState getClientCredentialsState() {
        return clientCredentialsState;
    }

    public void setClientCredentialsState(ClientCredentialsState clientCredentialsState) {
        this.clientCredentialsState = clientCredentialsState;
    }


    public String getSalesforceOrg() {
        return salesforceOrg;
    }

    public void setSalesforceOrg(String salesforceOrg) {
        this.salesforceOrg = salesforceOrg;
    }
/*
    public String getApiInstanceUrl() {
        return apiInstanceUrl;
    }

    public void setApiInstanceUrl(String apiInstanceUrl) {
        this.apiInstanceUrl = apiInstanceUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }*/
}
