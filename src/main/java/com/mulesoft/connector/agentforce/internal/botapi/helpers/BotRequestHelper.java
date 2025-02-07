package com.mulesoft.connector.agentforce.internal.botapi.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulesoft.connector.agentforce.api.metadata.InvokeAgentResponseAttributes;
import com.mulesoft.connector.agentforce.internal.botapi.dto.AgentConversationResponseDTO;
import com.mulesoft.connector.agentforce.internal.botapi.dto.AgentMetadataResponseDTO;
import com.mulesoft.connector.agentforce.internal.botapi.dto.BotContinueSessionRequestDTO;
import com.mulesoft.connector.agentforce.internal.botapi.dto.BotRecord;
import com.mulesoft.connector.agentforce.internal.botapi.dto.BotSessionRequestDTO;
import com.mulesoft.connector.agentforce.internal.botapi.dto.InstanceConfigDTO;
import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.error.AgentforceErrorType;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.entity.InputStreamHttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.CONTINUE_SESSION_MESSAGE_TYPE_TEXT;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.END_SESSION_REASON_USERREQUEST;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.MESSAGE;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.MESSAGES;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.SESSION_ID;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.URI_BOT_API_AGENTS;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.URI_BOT_API_MESSAGES;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.URI_BOT_API_METADATA_AGENTLIST;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.URI_BOT_API_METADATA_SERVICES_V_62;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.URI_BOT_API_SESSIONS;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.V6_URI_BOT_API_BOTS;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.X_SESSION_END_REASON;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.ACCEPT_TYPE_STRING;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.AUTHORIZATION;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.CONTENT_TYPE_APPLICATION_JSON;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.CONTENT_TYPE_STRING;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.HTTP_METHOD_DELETE;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.HTTP_METHOD_GET;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.HTTP_METHOD_POST;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper.createURLConnection;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper.handleHttpResponse;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper.writePayloadToConnStream;
import static org.apache.commons.io.IOUtils.toInputStream;

public class BotRequestHelper {

  private static final Logger log = LoggerFactory.getLogger(BotRequestHelper.class);

  private final AgentforceConnection agentforceConnection;
  private final ObjectMapper objectMapper;

  public BotRequestHelper(AgentforceConnection agentforceConnection) {
    this.agentforceConnection = agentforceConnection;
    objectMapper = new ObjectMapper();
  }

  public List<BotRecord> getAgentList() throws IOException {

    String metadataUrl = agentforceConnection.getSalesforceOrgUrl()
        + URI_BOT_API_METADATA_SERVICES_V_62 + URI_BOT_API_METADATA_AGENTLIST;

    HttpURLConnection httpConnection = createURLConnection(metadataUrl, HTTP_METHOD_GET);
    addConnectionHeaders(httpConnection, agentforceConnection.getAccessToken());

    log.debug("Executing getAgentList request with URL: {} ", metadataUrl);
    InputStream responseStream = handleHttpResponse(httpConnection,
                                                    AgentforceErrorType.AGENT_METADATA_FAILURE);
    AgentMetadataResponseDTO agentMetadataResponse = objectMapper.readValue(responseStream, AgentMetadataResponseDTO.class);

    return agentMetadataResponse.getRecords();
  }

  public AgentConversationResponseDTO startSession(String agentId)
      throws IOException {

    String startSessionUrl = agentforceConnection.getApiInstanceUrl() + V6_URI_BOT_API_BOTS + URI_BOT_API_AGENTS
        + agentId + URI_BOT_API_SESSIONS;
    String externalSessionKey = UUID.randomUUID().toString();
    String endpoint = agentforceConnection.getSalesforceOrgUrl();
    BotSessionRequestDTO payload = createStartSessionRequestPayload(externalSessionKey, endpoint);

    log.debug("Agentforce start session details. Request URL: {}, external Session Key:{}," +
        " endpoint: {}", startSessionUrl, externalSessionKey, endpoint);

    HttpURLConnection httpConnection = createURLConnection(startSessionUrl, HTTP_METHOD_POST);
    addConnectionHeaders(httpConnection, agentforceConnection.getAccessToken());
    writePayloadToConnStream(httpConnection, objectMapper.writeValueAsString(payload));

    return parseResponse(httpConnection);
  }

  public void startSession(String agentId, CompletionCallback<InputStream, InvokeAgentResponseAttributes> callback)
      throws IOException {

    String startSessionUrl = agentforceConnection.getApiInstanceUrl() + V6_URI_BOT_API_BOTS + URI_BOT_API_AGENTS
        + agentId + URI_BOT_API_SESSIONS;
    String externalSessionKey = UUID.randomUUID().toString();
    String endpoint = agentforceConnection.getSalesforceOrgUrl();
    BotSessionRequestDTO payload = createStartSessionRequestPayload(externalSessionKey, endpoint);

    log.debug("Agentforce start session details. Request URL: {}, external Session Key:{}," +
        " endpoint: {}", startSessionUrl, externalSessionKey, endpoint);

    InputStream bodyStream = new ByteArrayInputStream(objectMapper.writeValueAsString(payload)
        .getBytes(StandardCharsets.UTF_8));

    CompletableFuture<HttpResponse> completableFuture = agentforceConnection.getHttpClient().sendAsync(buildRequest(
                                                                                                                    startSessionUrl,
                                                                                                                    agentforceConnection
                                                                                                                        .getAccessToken(),
                                                                                                                    HTTP_METHOD_POST,
                                                                                                                    new InputStreamHttpEntity(bodyStream)));

    log.info("waiting");
    completableFuture.whenComplete((response, exception) -> {
      if (exception != null) {
        callback.error(exception);
      } else {
        callback.success(parseResponseForStartSession(response.getEntity().getContent()));
      }
    });
    log.info("completed");
  }

  public AgentConversationResponseDTO continueSession(InputStream message, String sessionId, int messageSequenceNumber)
      throws IOException {

    String continueSessionUrl =
        agentforceConnection.getApiInstanceUrl() + V6_URI_BOT_API_BOTS + URI_BOT_API_SESSIONS + sessionId
            + URI_BOT_API_MESSAGES;

    BotContinueSessionRequestDTO payload =
        createContinueSessionRequestPayload(IOUtils.toString(message), messageSequenceNumber);

    log.info("Agentforce continue session details. Request URL: {}, Session ID:{}", continueSessionUrl, sessionId);

    HttpURLConnection httpConnection = createURLConnection(continueSessionUrl, HTTP_METHOD_POST);
    addConnectionHeaders(httpConnection, agentforceConnection.getAccessToken());
    writePayloadToConnStream(httpConnection, objectMapper.writeValueAsString(payload));

    return parseResponse(httpConnection);
  }

  public void continueSession(InputStream message, String sessionId, int messageSequenceNumber,
                              CompletionCallback<InputStream, InvokeAgentResponseAttributes> callback)
      throws IOException {

    String continueSessionUrl =
        agentforceConnection.getApiInstanceUrl() + V6_URI_BOT_API_BOTS + URI_BOT_API_SESSIONS + sessionId
            + URI_BOT_API_MESSAGES;

    BotContinueSessionRequestDTO payload =
        createContinueSessionRequestPayload(IOUtils.toString(message), messageSequenceNumber);

    log.info("Agentforce continue session details. Request URL: {}, Session ID:{}", continueSessionUrl, sessionId);

    InputStream bodyStream = new ByteArrayInputStream(objectMapper.writeValueAsString(payload)
        .getBytes(StandardCharsets.UTF_8));

    CompletableFuture<HttpResponse> completableFuture = agentforceConnection.getHttpClient().sendAsync(buildRequest(
                                                                                                                    continueSessionUrl,
                                                                                                                    agentforceConnection
                                                                                                                        .getAccessToken(),
                                                                                                                    HTTP_METHOD_POST,
                                                                                                                    new InputStreamHttpEntity(bodyStream)));

    completableFuture.whenComplete((response, exception) -> {
      if (exception != null) {
        callback.error(exception);
      } else {
        callback.success(parseResponseForStartSession(response.getEntity().getContent()));
      }
    });
  }

  public AgentConversationResponseDTO endSession(String sessionId) throws IOException {

    String endSessionUrl = agentforceConnection.getApiInstanceUrl() + V6_URI_BOT_API_BOTS + URI_BOT_API_SESSIONS + sessionId;

    log.debug("Agentforce end session details. Request URL: {}, Session ID:{}", endSessionUrl, sessionId);

    HttpURLConnection httpConnection = createURLConnection(endSessionUrl, HTTP_METHOD_DELETE);
    addConnectionHeadersForEndSession(httpConnection, agentforceConnection.getAccessToken());

    return parseResponse(httpConnection);
  }

  private static void addConnectionHeaders(HttpURLConnection conn, String accessToken) {
    conn.setRequestProperty(AUTHORIZATION, "Bearer " + accessToken);
    conn.setRequestProperty(CONTENT_TYPE_STRING, CONTENT_TYPE_APPLICATION_JSON);
    conn.setRequestProperty(ACCEPT_TYPE_STRING, CONTENT_TYPE_APPLICATION_JSON);
  }

  /*
   * private static void addConnectionHeadersForBotAPI(HttpURLConnection conn, String accessToken) { addConnectionHeaders(conn,
   * accessToken); conn.setRequestProperty(ACCEPT_TYPE_STRING, CONTENT_TYPE_APPLICATION_JSON); }
   */
  private static void addConnectionHeadersForEndSession(HttpURLConnection conn, String accessToken) {
    addConnectionHeaders(conn, accessToken);
    conn.setRequestProperty(X_SESSION_END_REASON, END_SESSION_REASON_USERREQUEST);
  }

  private BotSessionRequestDTO createStartSessionRequestPayload(String externalSessionKey,
                                                                String endpoint) {

    InstanceConfigDTO instanceConfigDTO = new InstanceConfigDTO();
    instanceConfigDTO.setEndpoint(endpoint);
    return new BotSessionRequestDTO(externalSessionKey, instanceConfigDTO);
  }

  private BotContinueSessionRequestDTO createContinueSessionRequestPayload(String message,
                                                                           int messageSequenceNumber) {

    BotContinueSessionRequestDTO.Message messageDTO = new BotContinueSessionRequestDTO.Message();
    messageDTO.setText(message);
    messageDTO.setSequenceId(messageSequenceNumber);
    messageDTO.setType(CONTINUE_SESSION_MESSAGE_TYPE_TEXT);

    return new BotContinueSessionRequestDTO(messageDTO);
  }

  private AgentConversationResponseDTO parseResponse(HttpURLConnection httpConnection) throws IOException {

    InputStream responseStream = handleHttpResponse(httpConnection,
                                                    AgentforceErrorType.AGENT_API_ERROR);

    AgentConversationResponseDTO responseDTO = new AgentConversationResponseDTO();

    JsonNode rootNode = objectMapper.readTree(responseStream);

    responseDTO.setResponseAttributes(
                                      objectMapper.treeToValue(
                                                               rootNode, InvokeAgentResponseAttributes.class));
    responseDTO.setSessionId(getTextValue(rootNode, SESSION_ID));
    responseDTO.setText(getMessageText(rootNode));

    return responseDTO;
  }

  private String getMessageText(JsonNode rootNode) {
    JsonNode messagesNode = rootNode.get(MESSAGES);
    if (messagesNode != null && messagesNode.isArray()) {
      return StreamSupport
          .stream(messagesNode.spliterator(), false)
          .map(x -> getTextValue(x, MESSAGE))
          .collect(Collectors.joining(" "));
    }
    throw new ModuleException(
                              "Invalid response structure. Expected 'Messages'", AgentforceErrorType.AGENT_API_ERROR);
  }

  private static String getTextValue(JsonNode node, String keyName) {
    return node != null && node.get(keyName) != null ? node.get(keyName).asText() : null;
  }

  private HttpRequest buildRequest(String uri, String accessToken, String httpMethod, HttpEntity httpEntity) {
    return HttpRequest.builder()
        .uri(uri)
        .headers(addConnectionHeaders(accessToken))
        .entity(httpEntity)
        .method(httpMethod)
        .build();
  }

  private MultiMap<String, String> addConnectionHeaders(String accessToken) {
    MultiMap<String, String> multiMap = new MultiMap<>();
    multiMap.put(AUTHORIZATION, "Bearer " + accessToken);
    multiMap.put(CONTENT_TYPE_STRING, CONTENT_TYPE_APPLICATION_JSON);
    multiMap.put(ACCEPT_TYPE_STRING, CONTENT_TYPE_APPLICATION_JSON);
    return multiMap;
  }

  private Result<InputStream, InvokeAgentResponseAttributes> parseResponseForStartSession(InputStream responseStream) {

    AgentConversationResponseDTO responseDTO = new AgentConversationResponseDTO();

    try {
      JsonNode rootNode = objectMapper.readTree(responseStream);
      responseDTO.setResponseAttributes(
                                        objectMapper.treeToValue(
                                                                 rootNode, InvokeAgentResponseAttributes.class));
      responseDTO.setSessionId(getTextValue(rootNode, SESSION_ID));
      responseDTO.setText(getMessageText(rootNode));

      System.out.println("responseDTO = " + responseDTO);

      return Result.<InputStream, InvokeAgentResponseAttributes>builder()
          .output(toInputStream(responseDTO.getSessionId(), StandardCharsets.UTF_8))
          .attributes(responseDTO.getResponseAttributes())
          .attributesMediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JAVA)
          .mediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JSON)
          .build();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Result<InputStream, InvokeAgentResponseAttributes> parseResponseForContinueSession(InputStream responseStream) {

    System.out.println("Inside parseResponseForStartSession");
    AgentConversationResponseDTO responseDTO = new AgentConversationResponseDTO();

    try {
      JsonNode rootNode = objectMapper.readTree(responseStream);
      responseDTO.setResponseAttributes(
                                        objectMapper.treeToValue(
                                                                 rootNode, InvokeAgentResponseAttributes.class));
      responseDTO.setSessionId(getTextValue(rootNode, SESSION_ID));
      responseDTO.setText(getMessageText(rootNode));

      return Result.<InputStream, InvokeAgentResponseAttributes>builder()
          .output(toInputStream(responseDTO.getText(), StandardCharsets.UTF_8))
          .attributes(responseDTO.getResponseAttributes())
          .attributesMediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JAVA)
          .mediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JSON)
          .build();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
