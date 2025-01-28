package com.mulesoft.connector.agentforce.internal.botapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Attributes {

  @JsonProperty("type")
  private String type;

  @JsonProperty("url")
  private String url;

  public String getType() {
    return type;
  }

  public String getUrl() {
    return url;
  }

}
