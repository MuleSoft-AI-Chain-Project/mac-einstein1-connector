package com.mulesoft.connector.agentforce.internal.modelsapi.dto;

import com.mulesoft.connector.agentforce.api.metadata.ResponseParameters;

import java.util.List;

public class AgentforceEmbeddingResponseDTO {

  private List<AgentforceEmbeddingDTO> embeddings;
  private ResponseParameters parameters;

  public List<AgentforceEmbeddingDTO> getEmbeddings() {
    return embeddings;
  }

  public ResponseParameters getParameters() {
    return parameters;
  }
}
