package com.mulesoft.connector.agentforce.internal.botapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BotRecord {

  @JsonProperty("Status")
  private String status;

  @JsonProperty("BotDefinition")
  private BotDefinition botDefinition;

  @JsonProperty("BotDefinitionId")
  private String botDefinitionId;

  @JsonProperty("attributes")
  private Attributes attributes;

  public String getStatus() {
    return status;
  }


  public BotDefinition getBotDefinition() {
    return botDefinition;
  }

  public Attributes getAttributes() {
    return attributes;
  }

  public String getBotDefinitionId() {
    return botDefinitionId;
  }
}
