package com.mule.mulechain.internal.operations;

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

import com.mule.mulechain.internal.connection.MuleChainEinstein1Connection;
import com.mule.mulechain.internal.helpers.MuleChainEinstein1PayloadHelper;
import com.mule.mulechain.internal.helpers.MuleChainEinstein1PromptTemplateHelper;
import com.mule.mulechain.internal.helpers.chatmemory.MuleChainEinstein1ChatMemoryHelper;
import com.mule.mulechain.internal.helpers.documents.MuleChainEinstein1ParametersEmbeddingDocument;
import com.mule.mulechain.internal.models.MuleChainEinstein1ParamsEmbeddingDetails;
import com.mule.mulechain.internal.models.MuleChainEinstein1ParamsModelDetails;
import com.mule.mulechain.internal.models.MuleChainEinstein1RAGParamsModelDetails;

import org.mule.runtime.extension.api.annotation.param.Connection;


/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class MuleChainEinstein1Operations {

  /**
   * Generate a response based on a list of messages representing a chat conversation.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("CHAT-generate-from-messages")
  public InputStream generateChat(String messages,@Connection MuleChainEinstein1Connection connection, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParamsModelDetails paramDetails){
    return toInputStream(MuleChainEinstein1PayloadHelper.executeGenerateChat(messages,connection,paramDetails).toString(), StandardCharsets.UTF_8);
  }

  /**
   * Create an embedding vector representing the input text.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("EMBEDDING-generate-from-text")
  public InputStream generateEmbedding(String text,@Connection MuleChainEinstein1Connection connection, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParamsEmbeddingDetails paramDetails){
    return toInputStream(MuleChainEinstein1PayloadHelper.executeGenerateEmbedding(text,connection,paramDetails), StandardCharsets.UTF_8);
  }

  /**
   * Performs .
   * @throws TikaException 
   * @throws SAXException 
   * @throws IOException 
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("EMBEDDING-adhoc-file-query")
  public InputStream queryEmbeddingOnFiles(String prompt, String filePath,@Connection MuleChainEinstein1Connection connection, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParametersEmbeddingDocument paramDetails) throws IOException, SAXException, TikaException{
    return toInputStream(MuleChainEinstein1PayloadHelper.EmbeddingFileQuery(prompt,filePath,connection,paramDetails.getModelName(), paramDetails.getFileType(), paramDetails.getOptionType()), StandardCharsets.UTF_8);
  }


   /**
   * Performs .
   * @throws TikaException 
   * @throws SAXException 
   * @throws IOException 
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("EMBEDDING-generate-from-file")
  public InputStream EmbeddingFromFiles(String filePath,@Connection MuleChainEinstein1Connection connection, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParametersEmbeddingDocument paramDetails) throws IOException, SAXException, TikaException{
    return toInputStream(MuleChainEinstein1PayloadHelper.EmbeddingFromFile(filePath,connection,paramDetails), StandardCharsets.UTF_8);
  }


   /**
   * Performs .
   * @throws TikaException 
   * @throws SAXException 
   * @throws IOException 
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("RAG-adhoc-load-document")
  public InputStream RAGgOnFiles(String prompt, String filePath,@Connection MuleChainEinstein1Connection connection, @ParameterGroup(name= "Additional properties") MuleChainEinstein1RAGParamsModelDetails paramDetails) throws IOException, SAXException, TikaException{
    String content = MuleChainEinstein1PayloadHelper.EmbeddingFileQuery(prompt,filePath,connection,paramDetails.getEmbeddingName(), paramDetails.getFileType(), paramDetails.getOptionType());
    return toInputStream(MuleChainEinstein1PayloadHelper.executeRAG("data: " + content + ", question: " + prompt, connection, paramDetails), StandardCharsets.UTF_8);
  }

     /**
   * Performs .
   * @throws TikaException 
   * @throws SAXException 
   * @throws IOException 
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("Tools-use-ai-service")
  public InputStream ExecuteTools(String prompt, String toolsConfig, @Connection MuleChainEinstein1Connection connection, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParamsModelDetails paramDetails) throws IOException, SAXException, TikaException{
    String content = MuleChainEinstein1PayloadHelper.EmbeddingFileQuery(prompt,toolsConfig,connection,"OpenAI Ada 002", "text", "FULL");
    return toInputStream(MuleChainEinstein1PayloadHelper.executeTools(prompt, "data: " + content + ", question: " + prompt, toolsConfig, connection, paramDetails), StandardCharsets.UTF_8);
  }



  /**
   * Generate a response based on the prompt provided.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("CHAT-answer-prompt")
  public InputStream generateText(String prompt, @Connection MuleChainEinstein1Connection connection, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParamsModelDetails paramDetails){
    return toInputStream(MuleChainEinstein1PayloadHelper.executeGenerateText(prompt,connection,paramDetails), StandardCharsets.UTF_8);
  }

  /**
   * Generate a response based on the prompt using chat memory.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("CHAT-answer-prompt-with-memory")
  public InputStream generateTextMemeory(String prompt, String memoryPath, String memoryName, Integer keepLastMessages, @Connection MuleChainEinstein1Connection connection, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParamsModelDetails paramDetails){
    return toInputStream(MuleChainEinstein1ChatMemoryHelper.chatWithMemory(prompt,memoryPath,memoryName,keepLastMessages,connection,paramDetails), StandardCharsets.UTF_8);
  }


  /**
   * Helps defining an AI Agent with a prompt template
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("AGENT-define-prompt-template")  
  public InputStream definePromptTemplate(String template, String instructions, String dataset, @Connection MuleChainEinstein1Connection connection, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParamsModelDetails paramDetails) {


          String finalPromptTemplate = MuleChainEinstein1PromptTemplateHelper.definePromptTemplate(template, instructions, dataset);
          String response = MuleChainEinstein1PayloadHelper.executeGenerateText(finalPromptTemplate, connection, paramDetails);

      	return toInputStream(response, StandardCharsets.UTF_8);
      }


}
