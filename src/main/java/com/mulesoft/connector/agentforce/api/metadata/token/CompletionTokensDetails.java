package com.mulesoft.connector.agentforce.api.metadata.token;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Objects;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletionTokensDetails implements Serializable {

  private final int reasoningTokens;
  private final int audioTokens;
  private final int acceptedPredictionTokens;
  private final int rejectedPredictionTokens;

  @ConstructorProperties({"reasoningTokens", "audioTokens", "acceptedPredictionTokens", "rejectedPredictionTokens"})
  public CompletionTokensDetails(int reasoningTokens, int audioTokens, int acceptedPredictionTokens,
                                 int rejectedPredictionTokens) {
    this.reasoningTokens = reasoningTokens;
    this.audioTokens = audioTokens;
    this.acceptedPredictionTokens = acceptedPredictionTokens;
    this.rejectedPredictionTokens = rejectedPredictionTokens;
  }

  public int getReasoningTokens() {
    return reasoningTokens;
  }

  public int getAudioTokens() {
    return audioTokens;
  }

  public int getAcceptedPredictionTokens() {
    return acceptedPredictionTokens;
  }

  public int getRejectedPredictionTokens() {
    return rejectedPredictionTokens;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    CompletionTokensDetails that = (CompletionTokensDetails) o;
    return reasoningTokens == that.reasoningTokens && audioTokens == that.audioTokens
        && acceptedPredictionTokens == that.acceptedPredictionTokens && rejectedPredictionTokens == that.rejectedPredictionTokens;
  }

  @Override
  public int hashCode() {
    return Objects.hash(reasoningTokens, audioTokens, acceptedPredictionTokens, rejectedPredictionTokens);
  }
}
