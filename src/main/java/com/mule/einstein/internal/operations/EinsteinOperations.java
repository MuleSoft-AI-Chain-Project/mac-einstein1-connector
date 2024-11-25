package com.mule.einstein.internal.operations;

import static org.apache.commons.io.IOUtils.toInputStream;
import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.tika.exception.TikaException;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.xml.sax.SAXException;

import com.mule.einstein.internal.connection.EinsteinConnection;
import com.mule.einstein.internal.helpers.PayloadHelper;
import com.mule.einstein.internal.helpers.PromptTemplateHelper;
import com.mule.einstein.internal.helpers.chatmemory.ChatMemoryHelper;
import com.mule.einstein.internal.helpers.documents.ParametersEmbeddingDocument;
import com.mule.einstein.internal.models.ParamsEmbeddingDetails;
import com.mule.einstein.internal.models.ParamsModelDetails;
import com.mule.einstein.internal.models.RAGParamsModelDetails;

import org.mule.runtime.extension.api.annotation.param.Connection;


/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class EinsteinOperations {

  /**
   * Generate a response based on a list of messages representing a chat conversation.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("CHAT-generate-from-messages")
  public InputStream generateChat(String messages, @Connection EinsteinConnection connection, @ParameterGroup(name= "Additional properties") ParamsModelDetails paramDetails){
    return toInputStream(PayloadHelper.executeGenerateChat(messages,connection,paramDetails), StandardCharsets.UTF_8);
  }

  /**
   * Create an embedding vector representing the input text.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("EMBEDDING-generate-from-text")
  public InputStream generateEmbedding(String text, @Connection EinsteinConnection connection, @ParameterGroup(name= "Additional properties") ParamsEmbeddingDetails paramDetails){
    return toInputStream(PayloadHelper.executeGenerateEmbedding(text,connection,paramDetails), StandardCharsets.UTF_8);
  }

  /**
   * Performs .
   * @throws TikaException 
   * @throws SAXException 
   * @throws IOException 
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("EMBEDDING-adhoc-file-query")
  public InputStream queryEmbeddingOnFiles(String prompt, String filePath, @Connection EinsteinConnection connection, @ParameterGroup(name= "Additional properties") ParametersEmbeddingDocument paramDetails) throws IOException, SAXException, TikaException{
    return toInputStream(PayloadHelper.embeddingFileQuery(prompt,filePath,connection,paramDetails.getModelName(), paramDetails.getFileType(), paramDetails.getOptionType()), StandardCharsets.UTF_8);
  }


   /**
   * Performs .
   * @throws TikaException 
   * @throws SAXException 
   * @throws IOException 
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("EMBEDDING-generate-from-file")
  public InputStream embeddingFromFiles(String filePath, @Connection EinsteinConnection connection, @ParameterGroup(name= "Additional properties") ParametersEmbeddingDocument paramDetails) throws IOException, SAXException, TikaException{
    return toInputStream(PayloadHelper.embeddingFromFile(filePath,connection,paramDetails), StandardCharsets.UTF_8);
  }


   /**
   * Performs .
   * @throws TikaException 
   * @throws SAXException 
   * @throws IOException 
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("RAG-adhoc-load-document")
  public InputStream ragOnFiles(String prompt, String filePath, @Connection EinsteinConnection connection, @ParameterGroup(name= "Additional properties") RAGParamsModelDetails paramDetails) throws IOException, SAXException, TikaException{
    String content = PayloadHelper.embeddingFileQuery(prompt,filePath,connection,paramDetails.getEmbeddingName(), paramDetails.getFileType(), paramDetails.getOptionType());
    return toInputStream(PayloadHelper.executeRAG("data: " + content + ", question: " + prompt, connection, paramDetails), StandardCharsets.UTF_8);
  }

     /**
   * Performs .
   * @throws TikaException 
   * @throws SAXException 
   * @throws IOException 
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("Tools-use-ai-service")
  public InputStream executeTools(String prompt, String toolsConfig, @Connection EinsteinConnection connection, @ParameterGroup(name= "Additional properties") ParamsModelDetails paramDetails) throws IOException, SAXException, TikaException{
    String content = PayloadHelper.embeddingFileQuery(prompt,toolsConfig,connection,"OpenAI Ada 002", "text", "FULL");
    return toInputStream(PayloadHelper.executeTools(prompt, "data: " + content + ", question: " + prompt, toolsConfig, connection, paramDetails), StandardCharsets.UTF_8);
  }



  /**
   * Generate a response based on the prompt provided.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("CHAT-answer-prompt")
  public InputStream generateText(String prompt, @Connection EinsteinConnection connection, @ParameterGroup(name= "Additional properties") ParamsModelDetails paramDetails){
    return toInputStream(PayloadHelper.executeGenerateText(prompt,connection,paramDetails), StandardCharsets.UTF_8);
  }

  /**
   * Generate a response based on the prompt using chat memory.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("CHAT-answer-prompt-with-memory")
  public InputStream generateTextMemeory(String prompt, String memoryPath, String memoryName, Integer keepLastMessages, @Connection EinsteinConnection connection, @ParameterGroup(name= "Additional properties") ParamsModelDetails paramDetails){
    return toInputStream(ChatMemoryHelper.chatWithMemory(prompt,memoryPath,memoryName,keepLastMessages,connection,paramDetails), StandardCharsets.UTF_8);
  }


  /**
   * Helps defining an AI Agent with a prompt template
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("AGENT-define-prompt-template")  
  public InputStream definePromptTemplate(String template, String instructions, String dataset, @Connection EinsteinConnection connection, @ParameterGroup(name= "Additional properties") ParamsModelDetails paramDetails) {


          String finalPromptTemplate = PromptTemplateHelper.definePromptTemplate(template, instructions, dataset);
          String response = PayloadHelper.executeGenerateText(finalPromptTemplate, connection, paramDetails);

      	return toInputStream(response, StandardCharsets.UTF_8);
      }


}
