package com.mulesoft.connector.agentforce.internal.dto;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthResponseDTO {
  private final String accessToken;
  private final String apiInstanceUrl;
  private final String orgId;
  private final String salesforceorg;

  @ConstructorProperties({"accessToken", "apiInstanceUrl", "id", "intanceUrl"})
  public OAuthResponseDTO(String accessToken, String apiInstanceUrl, String id, String salesforceorg) {
    this.accessToken = accessToken;
    this.apiInstanceUrl = apiInstanceUrl;
    this.orgId = parseOrgId(id);
    this.salesforceorg = salesforceorg;
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

  public String getSalesforceorg() {
    return salesforceorg;
  }

  private String parseOrgId(String id) {
    String[] idArr = id.split("/");
    return idArr[idArr.length - 2];
  }

  @Override
  public String toString() {
    return "OAuthResponseDTO{" +
            "accessToken='" + accessToken + '\'' +
            ", apiInstanceUrl='" + apiInstanceUrl + '\'' +
            ", orgId='" + orgId + '\'' +
            ", salesforceorg='" + salesforceorg + '\'' +
            '}';
  }
}
