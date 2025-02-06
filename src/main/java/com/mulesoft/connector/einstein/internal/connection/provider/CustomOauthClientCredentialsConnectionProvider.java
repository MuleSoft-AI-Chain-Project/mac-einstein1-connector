package com.mulesoft.connector.einstein.internal.connection.provider;

import com.mulesoft.connector.einstein.internal.connection.EinsteinConnection;
import com.mulesoft.connector.einstein.internal.connection.CustomOAuthClientCredentialsConnection;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.extension.api.connectivity.oauth.ClientCredentialsState;
import org.mule.runtime.extension.api.annotation.connectivity.oauth.ClientCredentials;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.connectivity.oauth.OAuthCallbackValue;
import org.mule.runtime.extension.api.annotation.Alias;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Alias("oauth-client-credentials")
@DisplayName("OAuth Client Credentials")
@ClientCredentials(tokenUrl = "https://{salesforceorg}/services/oauth2/token")
public class CustomOauthClientCredentialsConnectionProvider implements EinsteinConnectionProvider,
    CachedConnectionProvider<EinsteinConnection> {

  private static final Logger log = LoggerFactory.getLogger(CustomOauthClientCredentialsConnectionProvider.class);
  private ClientCredentialsState clientCredentialsState;

  @OAuthCallbackValue(expression = "#[payload.instance_url]")
  private String salesforceOrgUrl;

  @OAuthCallbackValue(expression = "#[payload.api_instance_url]")
  private String apiInstanceUrl;

  @OAuthCallbackValue(expression = "#[payload.id]")
  private String id;

  @Override
  public EinsteinConnection connect() {
    log.info("Inside CustomOauthClientCredentialsConnectionProvider connect, salesforceOrg {}, apiInstanceUrl = {}," +
        " id = {} ", salesforceOrgUrl, apiInstanceUrl, id);
    return new CustomOAuthClientCredentialsConnection(salesforceOrgUrl, clientCredentialsState, apiInstanceUrl, id);
  }

  public void setClientCredentialsState(ClientCredentialsState clientCredentialsState) {
    this.clientCredentialsState = clientCredentialsState;
  }
}
