package com.mulesoft.connector.agentforce.internal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.beans.ConstructorProperties;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthResponseDTO {

  private final String accessToken;
  private final String apiInstanceUrl;
  private final String orgId;

  @ConstructorProperties({"accessToken", "apiInstanceUrl", "id"})
  public OAuthResponseDTO(String accessToken, String apiInstanceUrl, String id) {
    this.accessToken = accessToken;
    this.apiInstanceUrl = apiInstanceUrl;
    this.orgId = parseOrgId(id);
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getApiInstanceUrl() {
    return apiInstanceUrl;
  }

  public String getOrgId() {
    return orgId;
  }

  private String parseOrgId(String id) {
    int lastIndex = id.lastIndexOf("/");
    return id.substring(lastIndex + 1);
  }
}
