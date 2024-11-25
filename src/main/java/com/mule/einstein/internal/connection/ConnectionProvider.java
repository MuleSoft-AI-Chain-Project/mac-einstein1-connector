package com.mule.einstein.internal.connection;

import com.mule.einstein.internal.helpers.ConstantUtil;
import com.mule.einstein.internal.helpers.RequestHelper;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
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
  @Example("mydomain.my.salesforce.com")
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
      int responseCode = getConnectionResponseCode(salesforceOrg,clientId,clientSecret);

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
      log.error("Error while disconnecting [{}]: {}", connection.getClientId(), e.getMessage(), e);
    }
  }

  @Override
  public ConnectionValidationResult validate(EinsteinConnection connection) {
    try {
      int responseCode = getConnectionResponseCode(connection.getSalesforceOrg(),connection.getClientId(),connection.getClientSecret());

      if (responseCode == 200) {
        return ConnectionValidationResult.success();
      } else {
        return ConnectionValidationResult.failure("Failed to validate connection: HTTP " + responseCode, null);
      }
    } catch (IOException e) {
      return ConnectionValidationResult.failure("Failed to validate connection", e);
    }
  }

  private int getConnectionResponseCode(String salesforceOrg, String clientId, String clientSecret) throws IOException {

    log.debug("Preparing request for connection for salesforce org:{}",salesforceOrg);

    String urlStr = RequestHelper.getOAuthURL(salesforceOrg);
    String urlParameters = RequestHelper.getOAuthParams(clientId,clientSecret);

    byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

    URL url = new URL(urlStr);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setDoOutput(true);
    conn.setRequestMethod(ConstantUtil.HTTP_METHOD_POST);
    conn.getOutputStream().write(postData);
    int respCode = conn.getResponseCode();

    log.debug("Response code for connection request:{}",respCode);
    return respCode;
  }
}
