package com.mulesoft.connector.einstein.internal.modelsapi.dto;

import com.mulesoft.connector.einstein.api.metadata.ResponseParameters;

import java.util.List;

public class EinsteinEmbeddingResponseDTO {

  private List<EinsteinEmbeddingDTO> embeddings;
  private ResponseParameters parameters;

  public List<EinsteinEmbeddingDTO> getEmbeddings() {
    return embeddings;
  }

  public ResponseParameters getParameters() {
    return parameters;
  }
}
