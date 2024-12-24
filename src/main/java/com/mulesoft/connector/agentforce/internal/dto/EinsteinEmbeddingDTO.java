package com.mulesoft.connector.agentforce.internal.dto;

import java.beans.ConstructorProperties;
import java.util.List;

public class EinsteinEmbeddingDTO {

  private final int index;
  private final List<Double> embedding;

  @ConstructorProperties({"index", "embedding"})
  public EinsteinEmbeddingDTO(int index, List<Double> embedding) {
    this.index = index;
    this.embedding = embedding;
  }

  public int getIndex() {
    return index;
  }

  public List<Double> getEmbedding() {
    return embedding;
  }
}
