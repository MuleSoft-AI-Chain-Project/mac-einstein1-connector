package com.mule.mulechain.internal;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import com.mule.mulechain.internal.operations.MuleChainEinstein1Operations;

/**
 * This class represents an extension configuration, values set in this class are commonly used across multiple
 * operations since they represent something core from the extension.
 */
@Operations(MuleChainEinstein1Operations.class)
public class MuleChainEinstein1Configuration {

  @Parameter
  private String salesforceOrg;

  @Parameter
  private String clientId;
  
  @Parameter
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
}
