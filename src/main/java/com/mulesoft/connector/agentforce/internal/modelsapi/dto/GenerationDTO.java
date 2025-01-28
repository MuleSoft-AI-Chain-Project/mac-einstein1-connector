package com.mulesoft.connector.agentforce.internal.modelsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mulesoft.connector.agentforce.api.metadata.GenerationParameters;
import com.mulesoft.connector.agentforce.api.metadata.quality.ContentQuality;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerationDTO {

  private String id;
  private String generatedText;
  private ContentQuality contentQuality;
  private GenerationParameters parameters;

  public String getId() {
    return id;
  }

  public String getGeneratedText() {
    return generatedText;
  }

  public ContentQuality getContentQuality() {
    return contentQuality;
  }

  public GenerationParameters getParameters() {
    return parameters;
  }
}
