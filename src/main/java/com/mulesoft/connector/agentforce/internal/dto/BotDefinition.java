package com.mulesoft.connector.agentforce.internal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BotDefinition {

  @JsonProperty("MasterLabel")
  private String masterLabel;

  public String getMasterLabel() {
    return masterLabel;
  }

  public void setMasterLabel(String masterLabel) {
    this.masterLabel = masterLabel;
  }
}
