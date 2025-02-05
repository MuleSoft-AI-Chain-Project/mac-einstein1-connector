package com.mulesoft.connector.agentforce.internal.botapi.dto;

import com.mulesoft.connector.agentforce.api.metadata.InvokeAgentResponseAttributes;

import java.util.Objects;

public class AgentConversationResponseDTO {

  private InvokeAgentResponseAttributes responseAttributes;
  private String text;
  private String sessionId;

  public InvokeAgentResponseAttributes getResponseAttributes() {
    return responseAttributes;
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

  public void setResponseAttributes(InvokeAgentResponseAttributes responseAttributes) {
    this.responseAttributes = responseAttributes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof AgentConversationResponseDTO))
      return false;
    AgentConversationResponseDTO that = (AgentConversationResponseDTO) o;
    return Objects.equals(getResponseAttributes(), that.getResponseAttributes()) && Objects.equals(getText(), that.getText())
        && Objects.equals(getSessionId(), that.getSessionId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getResponseAttributes(), getText(), getSessionId());
  }

  @Override
  public String toString() {
    return "AgentConversationResponseDTO{" +
        "responseAttributes=" + responseAttributes +
        ", text='" + text + '\'' +
        ", sessionId='" + sessionId + '\'' +
        '}';
  }
}
