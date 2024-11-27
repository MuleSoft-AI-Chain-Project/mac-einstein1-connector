package com.mule.einstein.api.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mule.einstein.api.metadata.token.TokenUsage;

import java.beans.ConstructorProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseParameters {

  private final TokenUsage tokenUsage;
  private final String model;
  private final String systemFingerPrint;
  private final String object;

  @ConstructorProperties({"usage", "model", "systemFingerPrint", "object"})
  public ResponseParameters(TokenUsage tokenUsage, String model, String systemFingerPrint, String object) {
    this.tokenUsage = tokenUsage;
    this.model = model;
    this.systemFingerPrint = systemFingerPrint;
    this.object = object;
  }

  public TokenUsage getTokenUsage() {
    return tokenUsage;
  }

  public String getModel() {
    return model;
  }

  public String getSystemFingerPrint() {
    return systemFingerPrint;
  }

  public String getObject() {
    return object;
  }
}
