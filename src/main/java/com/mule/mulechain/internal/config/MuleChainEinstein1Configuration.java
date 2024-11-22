package com.mule.mulechain.internal.config;

import com.mule.mulechain.internal.connection.MuleChainEinstein1ConnectionProvider;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import com.mule.mulechain.internal.operations.MuleChainEinstein1Operations;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

/**
 * This class represents an extension configuration, values set in this class are commonly used across multiple
 * operations since they represent something core from the extension.
 */
@Configuration(name = "config")
@Operations(MuleChainEinstein1Operations.class)
@ConnectionProviders(MuleChainEinstein1ConnectionProvider.class)
public class MuleChainEinstein1Configuration implements Initialisable, Disposable {

  @Parameter
  @Placement(order = 1, tab = Placement.DEFAULT_TAB)
  @DisplayName("Salesforce Org URL")
  private String salesforceOrg;

  @Parameter
  @Placement(order = 1, tab = Placement.DEFAULT_TAB)
  @DisplayName("Client ID")
  private String clientId;
  
  @Parameter
  @Placement(order = 1, tab = Placement.DEFAULT_TAB)
  @DisplayName("Client Secret")
  private String clientSecret;

  public String getSalesforceOrg(){
    return salesforceOrg;
  }

  public String getClientId(){
    return clientId;
  }

  public String getClientSecret(){
    return clientSecret;
  }

  @Override
  public void dispose() {

  }

  @Override
  public void initialise() throws InitialisationException {

  }
}
