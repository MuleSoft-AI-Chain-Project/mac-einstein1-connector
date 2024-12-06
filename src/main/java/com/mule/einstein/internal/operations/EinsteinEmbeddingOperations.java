package com.mule.einstein.internal.operations;

import com.mule.einstein.api.metadata.EinsteinResponseAttributes;
import com.mule.einstein.internal.connection.EinsteinConnection;
import com.mule.einstein.internal.error.provider.EmbeddingErrorTypeProvider;
import com.mule.einstein.internal.helpers.PayloadHelper;
import com.mule.einstein.internal.helpers.ResponseHelper;
import com.mule.einstein.internal.models.ParamsEmbeddingDocumentDetails;
import com.mule.einstein.internal.models.ParamsEmbeddingModelDetails;
import com.mule.einstein.internal.models.ParamsModelDetails;
import com.mule.einstein.internal.models.RAGParamsModelDetails;
import org.apache.tika.exception.TikaException;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.mule.einstein.internal.error.EinsteinErrorType.*;
import static com.mule.einstein.internal.helpers.ConstantUtil.MODELAPI_OPENAI_ADA_002;
import static java.lang.String.format;
import static org.apache.commons.io.IOUtils.toInputStream;
import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;


/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class EinsteinEmbeddingOperations {

  private static final Logger log = LoggerFactory.getLogger(EinsteinEmbeddingOperations.class);

  /**
   * Create an embedding vector representing the input text.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("EMBEDDING-generate-from-text")
  public InputStream generateEmbedding(String text, @Connection EinsteinConnection connection,
                                       @ParameterGroup(name = "Additional properties") ParamsEmbeddingModelDetails paramDetails) {
    return toInputStream(PayloadHelper.executeGenerateEmbedding(text, connection, paramDetails), StandardCharsets.UTF_8);
  }

  /**
   * Create an embedding vector representing the input file .
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("EMBEDDING-generate-from-file")
  @Throws(EmbeddingErrorTypeProvider.class)
  public InputStream embeddingFromFiles(String filePath, @Connection EinsteinConnection connection,
                                        @ParameterGroup(
                                            name = "Additional properties") ParamsEmbeddingDocumentDetails paramDetails) {
    try {
      return toInputStream(PayloadHelper.embeddingFromFile(filePath, connection, paramDetails), StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new ModuleException("Error while executing embedding generate from file operation",
                                EMBEDDING_OPERATIONS_FAILURE, e);
    }
  }

  /**
   * Generate a response based on a file embedding.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("EMBEDDING-adhoc-file-query")
  @Throws(EmbeddingErrorTypeProvider.class)
  public Result<InputStream, Void> queryEmbeddingOnFiles(@Content String prompt, String filePath,
                                                         @Connection EinsteinConnection connection,
                                                         @ParameterGroup(
                                                             name = "Additional properties") ParamsEmbeddingDocumentDetails paramDetails) {
    log.info("Executing embedding adhoc file query operation.");
    try {

      String response = PayloadHelper.embeddingFileQuery(prompt, filePath, connection, paramDetails.getModelApiName(),
                                                         paramDetails.getFileType(), paramDetails.getOptionType());

      return ResponseHelper.createEinsteinDefaultResponse(response);
    } catch (Exception e) {

      log.error(format("Exception occurred while executing embedding adhoc file query operation %s", e.getMessage()), e);
      throw new ModuleException("Error while generating the chat from filePath " + filePath + ", for prompt " + prompt,
                                EMBEDDING_OPERATIONS_FAILURE, e);
    }
  }

  /**
   * Generate a response based on a plain text prompt and file from embedding and LLM.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("RAG-adhoc-load-document")
  @Throws(EmbeddingErrorTypeProvider.class)
  public Result<InputStream, EinsteinResponseAttributes> ragOnFiles(@Content String prompt, String filePath,
                                                                    @Connection EinsteinConnection connection,
                                                                    @ParameterGroup(
                                                                        name = "Additional properties") RAGParamsModelDetails paramDetails) {
    log.info("Executing rag adhoc load document.");
    try {

      String content = PayloadHelper.embeddingFileQuery(prompt, filePath, connection, paramDetails.getEmbeddingName(),
                                                        paramDetails.getFileType(), paramDetails.getOptionType());
      String response = PayloadHelper.executeRAG("data: " + content + ", question: " + prompt, connection,
                                                 paramDetails);

      return ResponseHelper.createEinsteinFormattedResponse(response);
    } catch (Exception e) {

      log.error(format("Exception occurred while executing rag adhoc load document operation %s", e.getMessage()), e);
      throw new ModuleException("Error while doing rag adhoc load document from filePath " + filePath + ", for prompt "
          + prompt, RAG_FAILURE, e);
    }
  }

  /**
   * Generate a response based on a plain text prompt and tools config.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("Tools-use-ai-service")
  @Throws(EmbeddingErrorTypeProvider.class)
  public Result<InputStream, EinsteinResponseAttributes> executeTools(@Content String prompt, String toolsConfig,
                                                                      @Connection EinsteinConnection connection,
                                                                      @ParameterGroup(
                                                                          name = "Additional properties") ParamsModelDetails paramDetails) {
    log.info("Executing AI service tools operation.");
    try {

      String content =
          PayloadHelper.embeddingFileQuery(prompt, toolsConfig, connection, MODELAPI_OPENAI_ADA_002, "text", "FULL");
      String response = PayloadHelper.executeTools(prompt, "data: " + content + ", question: " + prompt,
                                                   toolsConfig, connection, paramDetails);

      return ResponseHelper.createEinsteinFormattedResponse(response);
    } catch (Exception e) {

      log.error(format("Exception occurred while executing AI service tools operation %s", e.getMessage()), e);
      throw new ModuleException("Error while executing einstein tools with provided config " + toolsConfig + ", for prompt "
          + prompt, TOOLS_OPERATION_FAILURE, e);
    }
  }
}
