package com.mulesoft.connector.einstein.api.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mulesoft.connector.einstein.api.metadata.token.TokenUsage;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseParameters implements Serializable {

  private final TokenUsage tokenUsage;
  private final String model;
  private final String systemFingerprint;
  private final String object;

  @ConstructorProperties({"usage", "model", "systemFingerprint", "object"})
  public ResponseParameters(TokenUsage tokenUsage, String model, String systemFingerprint, String object) {
    this.tokenUsage = tokenUsage;
    this.model = model;
    this.systemFingerprint = systemFingerprint;
    this.object = object;
  }

  public TokenUsage getTokenUsage() {
    return tokenUsage;
  }

  public String getModel() {
    return model;
  }

  public String getSystemFingerprint() {
    return systemFingerprint;
  }

  public String getObject() {
    return object;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof ResponseParameters))
      return false;
    ResponseParameters that = (ResponseParameters) o;
    return Objects.equals(getTokenUsage(), that.getTokenUsage()) && Objects.equals(getModel(), that.getModel())
        && Objects.equals(getSystemFingerprint(), that.getSystemFingerprint()) && Objects.equals(getObject(), that.getObject());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTokenUsage(), getModel(), getSystemFingerprint(), getObject());
  }
}
