package com.mulesoft.connector.agentforce.internal.dto;

public class OAuthResponseDTO {

  private final String apiInstanceUrl;
  private final String orgId;
  private final String salesforceOrgUrl;

  public OAuthResponseDTO(String apiInstanceUrl, String id, String salesforceOrgUrl) {
    this.apiInstanceUrl = apiInstanceUrl;
    this.orgId = parseOrgId(id);
    this.salesforceOrgUrl = salesforceOrgUrl;
  }

  public String getApiInstanceUrl() {
    return apiInstanceUrl;
  }

  public String getOrgId() {
    return orgId;
  }

  public String getSalesforceOrgUrl() {
    return salesforceOrgUrl;
  }

  private String parseOrgId(String id) {
    String[] idArr = id.split("/");
    return idArr[idArr.length - 2];
  }
}
