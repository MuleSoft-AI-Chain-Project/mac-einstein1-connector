package com.mulesoft.connector.agentforce.internal.botapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BotSessionRequestDTO {

  private final String externalSessionKey;
  private final ForceConfigDTO forceConfig;
  private final Message message;

  public BotSessionRequestDTO(String externalSessionKey, ForceConfigDTO forceConfig, Message message) {

    this.externalSessionKey = externalSessionKey;
    this.forceConfig = forceConfig;
    this.message = message;
  }

  public String getExternalSessionKey() {
    return externalSessionKey;
  }

  public ForceConfigDTO getForceConfig() {
    return forceConfig;
  }

  public Message getMessage() {
    return message;
  }

  public static class Message {

    private final String text;

    public Message(String text) {
      this.text = text;
    }

    public String getText() {
      return text;
    }
  }
}
