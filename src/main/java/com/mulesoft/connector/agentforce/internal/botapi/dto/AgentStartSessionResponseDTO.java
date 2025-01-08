package com.mulesoft.connector.agentforce.internal.botapi.dto;

import com.mulesoft.connector.agentforce.api.metadata.InvokeAgentResponseAttributes;

import java.io.InputStream;

public class AgentStartSessionResponseDTO {

  private InvokeAgentResponseAttributes responseAttributes;
  private InputStream textInputStream;

  public InvokeAgentResponseAttributes getResponseAttributes() {
    return responseAttributes;
  }

  public void setResponseAttributes(InvokeAgentResponseAttributes responseAttributes) {
    this.responseAttributes = responseAttributes;
  }

  public InputStream getTextInputStream() {
    return textInputStream;
  }

  public void setTextInputStream(InputStream textInputStream) {
    this.textInputStream = textInputStream;
  }
}
