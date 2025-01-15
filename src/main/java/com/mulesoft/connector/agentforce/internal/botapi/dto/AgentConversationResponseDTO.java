package com.mulesoft.connector.agentforce.internal.botapi.dto;

import com.mulesoft.connector.agentforce.api.metadata.InvokeAgentResponseAttributes;

import java.io.InputStream;

public class AgentConversationResponseDTO {

  private InvokeAgentResponseAttributes responseAttributes;
  private InputStream textInputStream;
  private String sessionId;

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

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
}
