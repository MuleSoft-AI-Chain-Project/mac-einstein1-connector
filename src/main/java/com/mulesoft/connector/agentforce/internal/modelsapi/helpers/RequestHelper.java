package com.mulesoft.connector.agentforce.internal.modelsapi.helpers;

import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.modelsapi.models.ParamsEmbeddingDocumentDetails;
import com.mulesoft.connector.agentforce.internal.modelsapi.models.ParamsEmbeddingModelDetails;
import com.mulesoft.connector.agentforce.internal.modelsapi.models.ParamsModelDetails;
import com.mulesoft.connector.agentforce.internal.modelsapi.models.RAGParamsModelDetails;
import org.apache.tika.exception.TikaException;
import org.json.JSONArray;
import org.xml.sax.SAXException;

import java.io.IOException;

public interface RequestHelper {

  String executeGenerateText(String prompt, ParamsModelDetails paramDetails) throws IOException;

  String executeGenerateChat(String messages, ParamsModelDetails paramDetails)
      throws IOException;

  String executeGenerateEmbedding(String text, ParamsEmbeddingModelDetails paramDetails)
      throws IOException;

  JSONArray embeddingFromFile(String filePath, ParamsEmbeddingDocumentDetails embeddingDocumentDetails)
      throws IOException, SAXException, TikaException;

  String executeRAG(String text, RAGParamsModelDetails paramDetails) throws IOException;

  String executeTools(String originalPrompt, String prompt, String filePath, ParamsModelDetails paramDetails)
      throws IOException;

  JSONArray embeddingFileQuery(String prompt, String filePath, String modelName, String fileType,
                               String optionType)
      throws IOException, SAXException, TikaException;
}
