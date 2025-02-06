package com.mulesoft.connector.einstein.internal.modelsapi.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulesoft.connector.einstein.api.metadata.EinsteinResponseAttributes;
import com.mulesoft.connector.einstein.api.metadata.ResponseParameters;
import com.mulesoft.connector.einstein.internal.modelsapi.dto.EinsteinChatFromMessagesResponseDTO;
import com.mulesoft.connector.einstein.internal.modelsapi.dto.EinsteinEmbeddingResponseDTO;
import com.mulesoft.connector.einstein.internal.modelsapi.dto.EinsteinGenerationResponseDTO;
import org.json.JSONObject;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.apache.commons.io.IOUtils.toInputStream;

public class ResponseHelper {

  private ResponseHelper() {
    throw new IllegalStateException("Utility class");
  }

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static Result<InputStream, Void> createAgentforceDefaultResponse(String response) {

    return Result.<InputStream, Void>builder()
        .output(toInputStream(response, StandardCharsets.UTF_8))
        .mediaType(MediaType.APPLICATION_JSON)
        .build();
  }

  public static Result<InputStream, EinsteinResponseAttributes> createAgentforceFormattedResponse(InputStream responseStream)
      throws IOException {

    EinsteinGenerationResponseDTO responseDTO = objectMapper.readValue(responseStream, EinsteinGenerationResponseDTO.class);

    String generatedText =
        responseDTO.getGeneration() != null ? responseDTO.getGeneration().getGeneratedText() : "";

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("response", generatedText);

    return Result.<InputStream, EinsteinResponseAttributes>builder()
        .output(toInputStream(jsonObject.toString(), StandardCharsets.UTF_8))
        .attributes(mapResponseAttributes(responseDTO))
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .mediaType(MediaType.APPLICATION_JSON)
        .build();
  }

  public static Result<InputStream, ResponseParameters> createAgentforceChatFromMessagesResponse(InputStream responseStream)
      throws IOException {

    EinsteinChatFromMessagesResponseDTO responseDTO =
        objectMapper.readValue(responseStream, EinsteinChatFromMessagesResponseDTO.class);

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("generations", responseDTO.getGenerationDetails().getGenerations());

    return Result.<InputStream, ResponseParameters>builder()
        .output(toInputStream(jsonObject.toString(), StandardCharsets.UTF_8))
        .attributes(responseDTO.getGenerationDetails().getParameters())
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .mediaType(MediaType.APPLICATION_JSON)
        .build();
  }

  public static Result<InputStream, ResponseParameters> createAgentforceEmbeddingResponse(InputStream response)
      throws IOException {

    EinsteinEmbeddingResponseDTO responseDTO = objectMapper.readValue(response, EinsteinEmbeddingResponseDTO.class);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("embeddings", responseDTO.getEmbeddings());

    return Result.<InputStream, ResponseParameters>builder()
        .output(toInputStream(jsonObject.toString(), StandardCharsets.UTF_8))
        .attributes(mapEmbeddingResponseAttributes(responseDTO))
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .mediaType(MediaType.APPLICATION_JSON)
        .build();
  }

  private static EinsteinResponseAttributes mapResponseAttributes(EinsteinGenerationResponseDTO responseDTO) {

    return new EinsteinResponseAttributes(
                                          responseDTO.getId(),
                                          responseDTO.getGeneration() != null ? responseDTO.getGeneration().getId() : null,
                                          responseDTO.getGeneration() != null ? responseDTO.getGeneration().getContentQuality()
                                              : null,
                                          responseDTO.getGeneration() != null ? responseDTO.getGeneration().getParameters()
                                              : null,
                                          responseDTO.getParameters());
  }

  private static ResponseParameters mapEmbeddingResponseAttributes(EinsteinEmbeddingResponseDTO responseDTO) {

    return new ResponseParameters(
                                  responseDTO.getParameters() != null ? responseDTO.getParameters().getTokenUsage() : null,
                                  responseDTO.getParameters() != null ? responseDTO.getParameters().getModel() : null,
                                  null,
                                  responseDTO.getParameters() != null ? responseDTO.getParameters().getObject() : null);
  }
}
