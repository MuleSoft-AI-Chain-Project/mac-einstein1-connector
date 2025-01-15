package com.mulesoft.connector.agentforce.internal.botapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BotContinueSessionRequestDTO {

  private final Message message;

  public BotContinueSessionRequestDTO(Message message) {
    this.message = message;
  }

  public Message getMessage() {
    return message;
  }

  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  public static class Message {

    private String type;
    private int sequenceId;
    private String inReplyToMessageId;
    private String text;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public int getSequenceId() {
      return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
      this.sequenceId = sequenceId;
    }

    public String getInReplyToMessageId() {
      return inReplyToMessageId;
    }

    public void setInReplyToMessageId(String inReplyToMessageId) {
      this.inReplyToMessageId = inReplyToMessageId;
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }
  }
}
