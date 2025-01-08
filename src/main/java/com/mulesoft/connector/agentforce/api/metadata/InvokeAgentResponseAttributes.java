package com.mulesoft.connector.agentforce.api.metadata;

import java.util.List;

public class InvokeAgentResponseAttributes {

  private String sessionId;
  private String botVersion;
  private List<Message> messages;
  private List<Integer> processedSequenceIds;

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getBotVersion() {
    return botVersion;
  }

  public void setBotVersion(String botVersion) {
    this.botVersion = botVersion;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  public List<Integer> getProcessedSequenceIds() {
    return processedSequenceIds;
  }

  public void setProcessedSequenceIds(List<Integer> processedSequenceIds) {
    this.processedSequenceIds = processedSequenceIds;
  }

  public static class Message {

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

  public static class Schedule {

    private int responseDelayMilliseconds;

    public int getResponseDelayMilliseconds() {
      return responseDelayMilliseconds;
    }

    public void setResponseDelayMilliseconds(int responseDelayMilliseconds) {
      this.responseDelayMilliseconds = responseDelayMilliseconds;
    }
  }
}
