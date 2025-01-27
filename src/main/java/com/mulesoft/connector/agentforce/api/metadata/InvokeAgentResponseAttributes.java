package com.mulesoft.connector.agentforce.api.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

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

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Message implements Serializable {

    private String id;
    private Schedule schedule;
    private String type;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public Schedule getSchedule() {
      return schedule;
    }

    public void setSchedule(Schedule schedule) {
      this.schedule = schedule;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

  }

  public static class Schedule implements Serializable {

    private int responseDelayMilliseconds;

    public int getResponseDelayMilliseconds() {
      return responseDelayMilliseconds;
    }

    public void setResponseDelayMilliseconds(int responseDelayMilliseconds) {
      this.responseDelayMilliseconds = responseDelayMilliseconds;
    }
  }
}
