package com.mulesoft.connector.agentforce.internal.botapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BotSessionRequestDTO {

  private final String externalSessionKey;
  private final InstanceConfigDTO instanceConfig;

  public BotSessionRequestDTO(String externalSessionKey, InstanceConfigDTO instanceConfig) {

    this.externalSessionKey = externalSessionKey;
    this.instanceConfig = instanceConfig;
  }

  public String getExternalSessionKey() {
    return externalSessionKey;
  }

  public InstanceConfigDTO getInstanceConfig() {
    return instanceConfig;
  }
}
