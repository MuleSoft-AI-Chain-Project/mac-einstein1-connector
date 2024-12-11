package com.mule.einstein.internal.dto;

import com.mule.einstein.api.metadata.ResponseParameters;

import java.util.List;

public class EinsteinEmbeddingResponseDTO {

  private List<EinsteinEmbeddingDTO> embeddings;
  private ResponseParameters parameters;

  public List<EinsteinEmbeddingDTO> getEmbeddings() {
    return embeddings;
  }

  public void setEmbeddings(List<EinsteinEmbeddingDTO> embeddings) {
    this.embeddings = embeddings;
  }

  public ResponseParameters getParameters() {
    return parameters;
  }

  public void setParameters(ResponseParameters parameters) {
    this.parameters = parameters;
  }
}
