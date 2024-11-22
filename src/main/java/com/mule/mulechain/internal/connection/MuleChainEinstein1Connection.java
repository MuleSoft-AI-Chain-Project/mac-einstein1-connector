package com.mule.mulechain.internal.connection;

/**
 * This class represents a connection to the external system.
 */
public class MuleChainEinstein1Connection {

  private final String salesforceOrg;
  private final String clientId;
  private final String clientSecret;

  public MuleChainEinstein1Connection(String salesforceOrg, String clientId, String clientSecret) {
    this.salesforceOrg = salesforceOrg;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  public String getSalesforceOrg() {
    return salesforceOrg;
  }

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void invalidate() {
    // Add logic to invalidate the connection if necessary
  }
}
