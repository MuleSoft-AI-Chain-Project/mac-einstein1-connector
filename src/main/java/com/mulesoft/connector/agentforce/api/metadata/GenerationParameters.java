package com.mulesoft.connector.agentforce.api.metadata;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Objects;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GenerationParameters implements Serializable {

  private final String finishReason;
  private final String refusal;
  private final int index;
  private final String logprobs;

  @ConstructorProperties({"finshReason", "refusal", "index", "logprobs"})
  public GenerationParameters(String finishReason, String refusal, int index, String logprobs) {
    this.finishReason = finishReason;
    this.refusal = refusal;
    this.index = index;
    this.logprobs = logprobs;
  }

  public String getFinishReason() {
    return finishReason;
  }

  public String getRefusal() {
    return refusal;
  }

  public int getIndex() {
    return index;
  }

  public String getLogprobs() {
    return logprobs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof GenerationParameters))
      return false;
    GenerationParameters that = (GenerationParameters) o;
    return getIndex() == that.getIndex() && Objects.equals(getFinishReason(), that.getFinishReason())
        && Objects.equals(getRefusal(), that.getRefusal()) && Objects.equals(getLogprobs(), that.getLogprobs());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFinishReason(), getRefusal(), getIndex(), getLogprobs());
  }
}
