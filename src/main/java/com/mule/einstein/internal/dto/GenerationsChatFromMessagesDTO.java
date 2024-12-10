package com.mule.einstein.internal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mule.einstein.api.metadata.GenerationParameters;
import com.mule.einstein.api.metadata.quality.ContentQuality;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerationsChatFromMessagesDTO {

  private String id;
  private String role;
  private String content;
  private ContentQuality contentQuality;
  private GenerationParameters parameters;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ContentQuality getContentQuality() {
    return contentQuality;
  }

  public void setContentQuality(ContentQuality contentQuality) {
    this.contentQuality = contentQuality;
  }

  public GenerationParameters getParameters() {
    return parameters;
  }

  public void setParameters(GenerationParameters parameters) {
    this.parameters = parameters;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
