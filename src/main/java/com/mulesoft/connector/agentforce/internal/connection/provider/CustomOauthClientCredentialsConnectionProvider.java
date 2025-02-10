package com.mulesoft.connector.agentforce.internal.connection.provider;

import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.connection.CustomOAuthClientCredentialsConnection;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.extension.api.connectivity.oauth.ClientCredentialsState;
import org.mule.runtime.extension.api.annotation.connectivity.oauth.ClientCredentials;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.connectivity.oauth.OAuthCallbackValue;
import org.mule.runtime.extension.api.annotation.Alias;

import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.client.HttpClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@Alias("oauth-client-credentials")
@DisplayName("OAuth Client Credentials")
@ClientCredentials(tokenUrl = "https://{salesforceorg}/services/oauth2/token")
public class CustomOauthClientCredentialsConnectionProvider implements AgentforceConnectionProvider,
    CachedConnectionProvider<AgentforceConnection>, Startable, Stoppable {

  private static final Logger log = LoggerFactory.getLogger(CustomOauthClientCredentialsConnectionProvider.class);
  private ClientCredentialsState clientCredentialsState;

  @Inject
  private HttpService httpService;

  private HttpClient httpClient;

  @OAuthCallbackValue(expression = "#[payload.instance_url]")
  private String salesforceOrgUrl;

  @OAuthCallbackValue(expression = "#[payload.api_instance_url]")
  private String apiInstanceUrl;

  @Override
  public void start() {
    HttpClientConfiguration.Builder baseClientConfigBuilder = httpClientConfigBuilder();
    this.httpClient = httpService.getClientFactory().create(baseClientConfigBuilder.build());
    this.httpClient.start();

    log.debug("Mule HTTP client started.");
  }

  @Override
  public AgentforceConnection connect() {
    log.info("Inside CustomOauthClientCredentialsConnectionProvider connect, salesforceOrg {}, apiInstanceUrl = {}",
             salesforceOrgUrl, apiInstanceUrl);
    return new CustomOAuthClientCredentialsConnection(salesforceOrgUrl, clientCredentialsState, apiInstanceUrl, httpClient);
  }

  public void setClientCredentialsState(ClientCredentialsState clientCredentialsState) {
    this.clientCredentialsState = clientCredentialsState;
  }

  @Override
  public void stop() {
    if (httpClient != null) {
      httpClient.stop();
    }
  }

  private HttpClientConfiguration.Builder httpClientConfigBuilder() {
    return new HttpClientConfiguration.Builder().setName("http-client");
  }
}
