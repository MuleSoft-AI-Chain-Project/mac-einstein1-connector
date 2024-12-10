package com.mule.einstein.internal.dto;

import com.mule.einstein.api.metadata.ResponseParameters;

import java.util.List;

public class GenerationDetailsDTO {

  private ResponseParameters parameters;
  private List<GenerationsChatFromMessagesDTO> generations;

  public ResponseParameters getParameters() {
    return parameters;
  }

  public void setParameters(ResponseParameters parameters) {
    this.parameters = parameters;
  }

  public List<GenerationsChatFromMessagesDTO> getGenerations() {
    return generations;
  }

  public void setGenerations(List<GenerationsChatFromMessagesDTO> generations) {
    this.generations = generations;
  }
}
