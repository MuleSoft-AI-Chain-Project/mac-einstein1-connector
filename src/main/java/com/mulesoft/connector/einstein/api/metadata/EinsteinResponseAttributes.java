package com.mulesoft.connector.einstein.api.metadata;

import com.mulesoft.connector.einstein.api.metadata.quality.ContentQuality;

import java.io.Serializable;

public class EinsteinResponseAttributes implements Serializable {

  private final String responseId;
  private final String generationId;
  private final ContentQuality contentQuality;
  private final GenerationParameters generationParameters;
  private final ResponseParameters responseParameters;

  public EinsteinResponseAttributes(String responseId, String generationId, ContentQuality contentQuality,
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

}
