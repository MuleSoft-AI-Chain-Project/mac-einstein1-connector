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

  public void setStatus(String status) {
    this.status = status;
  }

  public BotDefinition getBotDefinition() {
    return botDefinition;
  }

  public void setBotDefinition(BotDefinition botDefinition) {
    this.botDefinition = botDefinition;
  }

  public Attributes getAttributes() {
    return attributes;
  }

  public void setAttributes(Attributes attributes) {
    this.attributes = attributes;
  }

  public String getBotDefinitionId() {
    return botDefinitionId;
  }

  public void setBotDefinitionId(String botDefinitionId) {
    this.botDefinitionId = botDefinitionId;
  }
}
