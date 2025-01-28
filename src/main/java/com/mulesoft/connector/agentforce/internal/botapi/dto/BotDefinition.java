package com.mulesoft.connector.agentforce.internal.botapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BotDefinition {

  @JsonProperty("MasterLabel")
  private String masterLabel;

  @JsonProperty("attributes")
  private Attributes attributes;

  public String getMasterLabel() {
    return masterLabel;
  }

  public Attributes getAttributes() {
    return attributes;
  }

}
