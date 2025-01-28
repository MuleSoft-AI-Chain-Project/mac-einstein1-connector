package com.mulesoft.connector.agentforce.internal.botapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentMetadataResponseDTO {

  @JsonProperty("totalSize")
  private int totalSize;

  @JsonProperty("done")
  private boolean done;

  @JsonProperty("records")
  private List<BotRecord> records;

  public int getTotalSize() {
    return totalSize;
  }

  public boolean isDone() {
    return done;
  }

  public List<BotRecord> getRecords() {
    return records;
  }
}
