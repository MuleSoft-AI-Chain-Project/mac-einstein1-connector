package com.mulesoft.connector.agentforce.api.metadata.token;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.beans.ConstructorProperties;
import java.io.Serializable;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PromptTokensDetails implements Serializable {

  private final int cachedTokens;
  private final int audioTokens;

  @ConstructorProperties({"cachedTokens", "audioTokens"})
  public PromptTokensDetails(int cachedTokens, int audioTokens) {
    this.cachedTokens = cachedTokens;
    this.audioTokens = audioTokens;
  }

  public int getCachedTokens() {
    return cachedTokens;
  }

  public int getAudioTokens() {
    return audioTokens;
  }
}
