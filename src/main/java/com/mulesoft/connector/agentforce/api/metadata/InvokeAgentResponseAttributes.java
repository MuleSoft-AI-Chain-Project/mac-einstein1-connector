package com.mulesoft.connector.agentforce.api.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InvokeAgentResponseAttributes implements Serializable {

  private String botVersion;
  private List<Message> messages;
  private List<Integer> processedSequenceIds;

  public String getBotVersion() {
    return botVersion;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public List<Integer> getProcessedSequenceIds() {
    return processedSequenceIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    InvokeAgentResponseAttributes that = (InvokeAgentResponseAttributes) o;
    return Objects.equals(botVersion, that.botVersion) && Objects.equals(messages, that.messages)
        && Objects.equals(processedSequenceIds, that.processedSequenceIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(botVersion, messages, processedSequenceIds);
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Message implements Serializable {

    private String id;
    private Schedule schedule;
    private String type;

    public String getId() {
      return id;
    }

    public Schedule getSchedule() {
      return schedule;
    }

    public String getType() {
      return type;
    }

  }

  public static class Schedule implements Serializable {

    private int responseDelayMilliseconds;

    public int getResponseDelayMilliseconds() {
      return responseDelayMilliseconds;
    }

  }
}
