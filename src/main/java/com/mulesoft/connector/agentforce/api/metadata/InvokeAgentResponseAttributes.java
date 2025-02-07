package com.mulesoft.connector.agentforce.api.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InvokeAgentResponseAttributes implements Serializable {

  private List<Message> messages;

  public List<Message> getMessages() {
    return messages;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof InvokeAgentResponseAttributes))
      return false;
    InvokeAgentResponseAttributes that = (InvokeAgentResponseAttributes) o;
    return Objects.equals(getMessages(), that.getMessages());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getMessages());
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Message implements Serializable {

    private String type;
    private String id;
    private String feedbackId;
    private String planId;
    private boolean isContentSafe;
    private String message;
    private String reason;

    public String getType() {
      return type;
    }

    public String getId() {
      return id;
    }

    public String getFeedbackId() {
      return feedbackId;
    }

    public String getPlanId() {
      return planId;
    }

    public boolean isContentSafe() {
      return isContentSafe;
    }

    public String getMessage() {
      return message;
    }

    public String getReason() {
      return reason;
    }
  }
}
