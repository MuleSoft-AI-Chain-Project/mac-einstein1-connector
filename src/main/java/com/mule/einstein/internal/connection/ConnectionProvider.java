package com.mule.einstein.internal.connection;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ConnectionProvider implements PoolingConnectionProvider<EinsteinConnection> {

  private final Logger log = LoggerFactory.getLogger(ConnectionProvider.class);

  @Parameter
  @Placement(order = 1, tab = Placement.CONNECTION_TAB)
  @DisplayName("Salesforce Org URL")
  private String salesforceOrg;

  @Parameter
  @Placement(order = 2,tab = Placement.CONNECTION_TAB)
  @DisplayName("Client ID")
  private String clientId;

  @Parameter
  @Placement(order = 3,tab = Placement.CONNECTION_TAB)
  @DisplayName("Client Secret")
  private String clientSecret;

  @Override
  public EinsteinConnection connect() throws ConnectionException {
    try {
      String urlStr = "https://" + salesforceOrg + ".my.salesforce.com/services/oauth2/token";
      String urlParameters = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
      byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

      URL url = new URL(urlStr);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.getOutputStream().write(postData);

      int responseCode = conn.getResponseCode();
      if (responseCode == 200) {
        return new EinsteinConnection(salesforceOrg, clientId, clientSecret);
      } else {
        throw new ConnectionException("Failed to connect to Salesforce: HTTP " + responseCode);
      }
    } catch (IOException e) {
      throw new ConnectionException("Failed to connect to Salesforce", e);
    }
  }

  @Override
  public void disconnect(EinsteinConnection connection) {
    try {
      connection.invalidate();
    } catch (Exception e) {
      log.error("Error while disconnecting [" + connection.getClientId() + "]: " + e.getMessage(), e);
    }
  }

  @Override
  public ConnectionValidationResult validate(EinsteinConnection connection) {
    try {
      String urlStr = "https://" + connection.getSalesforceOrg() + ".my.salesforce.com/services/oauth2/token";
      String urlParameters = "grant_type=client_credentials&client_id=" + connection.getClientId() + "&client_secret=" + connection.getClientSecret();
      byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

      URL url = new URL(urlStr);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.getOutputStream().write(postData);

      int responseCode = conn.getResponseCode();
      if (responseCode == 200) {
        return ConnectionValidationResult.success();
      } else {
        return ConnectionValidationResult.failure("Failed to validate connection: HTTP " + responseCode, null);
      }
    } catch (IOException e) {
      return ConnectionValidationResult.failure("Failed to validate connection", e);
    }
  }
}
