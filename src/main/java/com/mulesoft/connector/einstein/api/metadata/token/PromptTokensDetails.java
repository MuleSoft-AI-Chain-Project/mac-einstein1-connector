package com.mulesoft.connector.einstein.api.metadata.token;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    PromptTokensDetails that = (PromptTokensDetails) o;
    return cachedTokens == that.cachedTokens && audioTokens == that.audioTokens;
  }

  @Override
  public int hashCode() {
    return Objects.hash(cachedTokens, audioTokens);
  }
}
