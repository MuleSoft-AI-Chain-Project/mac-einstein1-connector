package com.mule.mulechain.internal.operations;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.xml.sax.SAXException;

import com.mule.mulechain.internal.MuleChainEinstein1Configuration;
import com.mule.mulechain.internal.helpers.MuleChainEinstein1PayloadHelper;
import com.mule.mulechain.internal.helpers.MuleChainEinstein1PromptTemplateHelper;
import com.mule.mulechain.internal.helpers.chatmemory.MuleChainEinstein1ChatMemoryHelper;
import com.mule.mulechain.internal.helpers.documents.MuleChainEinstein1ParametersEmbeddingDocument;
import com.mule.mulechain.internal.models.MuleChainEinstein1ParamsEmbeddingDetails;
import com.mule.mulechain.internal.models.MuleChainEinstein1ParamsModelDetails;

import org.mule.runtime.extension.api.annotation.param.Config;


/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class MuleChainEinstein1Operations {

  /**
   * Generate a response based on a list of messages representing a chat conversation.
   */
  @MediaType(value = ANY, strict = false)
  @Alias("CHAT-generate-from-messages")
  public String generateChat(String messages,@Config MuleChainEinstein1Configuration configuration, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParamsModelDetails paramDetails){
    return MuleChainEinstein1PayloadHelper.executeGenerateChat(messages,configuration,paramDetails);
  }

  /**
   * Create an embedding vector representing the input text.
   */
  @MediaType(value = ANY, strict = false)
  @Alias("EMBEDDING-generate-from-text")
  public String generateEmbedding(String text,@Config MuleChainEinstein1Configuration configuration, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParamsEmbeddingDetails paramDetails){
    return MuleChainEinstein1PayloadHelper.executeGenerateEmbedding(text,configuration,paramDetails);
  }

  /**
   * Performs .
   * @throws TikaException 
   * @throws SAXException 
   * @throws IOException 
   */
  @MediaType(value = ANY, strict = false)
  @Alias("EMBEDDING-adhoc-file-query")
  public String queryEmbeddingOnFiles(String prompt, String filePath,@Config MuleChainEinstein1Configuration configuration, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParametersEmbeddingDocument paramDetails) throws IOException, SAXException, TikaException{
    return MuleChainEinstein1PayloadHelper.EmbeddingFileQuery(prompt,filePath,configuration,paramDetails);
  }


    /**
   * Performs .
   * @throws TikaException 
   * @throws SAXException 
   * @throws IOException 
   */
  @MediaType(value = ANY, strict = false)
  @Alias("EMBEDDING-generate-from-file")
  public String EmbeddingFromFiles(String filePath,@Config MuleChainEinstein1Configuration configuration, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParametersEmbeddingDocument paramDetails) throws IOException, SAXException, TikaException{
    return MuleChainEinstein1PayloadHelper.EmbeddingFromFile(filePath,configuration,paramDetails);
  }


  /**
   * Generate a response based on the prompt provided.
   */
  @MediaType(value = ANY, strict = false)
  @Alias("CHAT-answer-prompt")
  public String generateText(String prompt, @Config MuleChainEinstein1Configuration configuration, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParamsModelDetails paramDetails){
    return MuleChainEinstein1PayloadHelper.executeGenerateText(prompt,configuration,paramDetails);
  }

  /**
   * Generate a response based on the prompt using chat memory.
   */
  @MediaType(value = ANY, strict = false)
  @Alias("CHAT-answer-prompt-with-memory")
  public String generateTextMemeory(String prompt, String memoryPath, String memoryName, Integer keepLastMessages, @Config MuleChainEinstein1Configuration configuration, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParamsModelDetails paramDetails){
    return MuleChainEinstein1ChatMemoryHelper.chatWithMemory(prompt,memoryPath,memoryName,keepLastMessages,configuration,paramDetails);
  }


  /**
   * Helps defining an AI Agent with a prompt template
   */
  @MediaType(value = ANY, strict = false)
  @Alias("AGENT-define-prompt-template")  
  public String definePromptTemplate(String template, String instructions, String dataset, @Config MuleChainEinstein1Configuration configuration, @ParameterGroup(name= "Additional properties") MuleChainEinstein1ParamsModelDetails paramDetails) {


          String finalPromptTemplate = MuleChainEinstein1PromptTemplateHelper.definePromptTemplate(template, instructions, dataset);
          System.out.println(finalPromptTemplate);

          String response = MuleChainEinstein1PayloadHelper.executeGenerateText(finalPromptTemplate, configuration, paramDetails);

          System.out.println(response);
      	return response;
      }


}
