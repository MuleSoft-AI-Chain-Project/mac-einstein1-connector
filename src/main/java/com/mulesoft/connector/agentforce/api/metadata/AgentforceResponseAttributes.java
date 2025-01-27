package com.mulesoft.connector.agentforce.api.metadata;

import com.mulesoft.connector.agentforce.api.metadata.quality.ContentQuality;

import java.io.Serializable;
import java.util.Objects;

public class AgentforceResponseAttributes implements Serializable {

  private final String responseId;
  private final String generationId;
  private final ContentQuality contentQuality;
  private final GenerationParameters generationParameters;
  private final ResponseParameters responseParameters;

  public AgentforceResponseAttributes(String responseId, String generationId, ContentQuality contentQuality,
                                      GenerationParameters generationParameters, ResponseParameters responseParameters) {
    this.responseId = responseId;
    this.generationId = generationId;
    this.contentQuality = contentQuality;
    this.generationParameters = generationParameters;
    this.responseParameters = responseParameters;
  }

  public String getResponseId() {
    return responseId;
  }

  public String getGenerationId() {
    return generationId;
  }

  public ContentQuality getContentQuality() {
    return contentQuality;
  }

  public GenerationParameters getGenerationParameters() {
    return generationParameters;
  }

  public ResponseParameters getResponseParameters() {
    return responseParameters;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof AgentforceResponseAttributes))
      return false;
    AgentforceResponseAttributes that = (AgentforceResponseAttributes) o;
    return Objects.equals(getResponseId(), that.getResponseId()) && Objects.equals(getGenerationId(), that.getGenerationId())
        && Objects.equals(getContentQuality(), that.getContentQuality())
        && Objects.equals(getGenerationParameters(), that.getGenerationParameters())
        && Objects.equals(getResponseParameters(), that.getResponseParameters());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getResponseId(), getGenerationId(), getContentQuality(), getGenerationParameters(),
                        getResponseParameters());
  }
}
