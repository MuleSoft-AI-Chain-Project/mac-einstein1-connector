package com.mulesoft.connector.agentforce.internal.botapi.dto;

import com.mulesoft.connector.agentforce.api.metadata.InvokeAgentResponseAttributes;

public class AgentConversationResponseDTO {

  private InvokeAgentResponseAttributes responseAttributes;
  private String text;
  private String sessionId;

  public InvokeAgentResponseAttributes getResponseAttributes() {
    return responseAttributes;
  }

  public void setResponseAttributes(InvokeAgentResponseAttributes responseAttributes) {
    this.responseAttributes = responseAttributes;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
}
