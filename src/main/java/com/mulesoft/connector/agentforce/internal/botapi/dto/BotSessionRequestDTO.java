package com.mulesoft.connector.agentforce.internal.botapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BotSessionRequestDTO {

  private final String externalSessionKey;
  private final ForceConfigDTO forceConfig;

  public BotSessionRequestDTO(String externalSessionKey, ForceConfigDTO forceConfig) {

    this.externalSessionKey = externalSessionKey;
    this.forceConfig = forceConfig;
  }

  public String getExternalSessionKey() {
    return externalSessionKey;
  }

  public ForceConfigDTO getForceConfig() {
    return forceConfig;
  }

}
