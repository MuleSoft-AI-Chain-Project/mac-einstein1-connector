package com.mulesoft.connector.agentforce.internal.botapi.helpers;

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
import org.json.JSONObject;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.http.api.domain.entity.EmptyHttpEntity;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.entity.InputStreamHttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
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
import static com.mulesoft.connector.agentforce.internal.error.AgentforceErrorType.AGENT_OPERATIONS_FAILURE;
import static org.apache.commons.io.IOUtils.toInputStream;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.ACCEPT_TYPE_STRING;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.AUTHORIZATION;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.CONTENT_TYPE_APPLICATION_JSON;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.CONTENT_TYPE_STRING;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.HTTP_METHOD_DELETE;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.HTTP_METHOD_GET;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.HTTP_METHOD_POST;

public class BotRequestHelper {

  private static final Logger log = LoggerFactory.getLogger(BotRequestHelper.class);

  private final AgentforceConnection agentforceConnection;
  private final ObjectMapper objectMapper;

  public BotRequestHelper(AgentforceConnection agentforceConnection) {
    this.agentforceConnection = agentforceConnection;
    objectMapper = new ObjectMapper();
  }

  public List<BotRecord> getAgentList() throws IOException, TimeoutException {

    String metadataUrl = agentforceConnection.getSalesforceOrgUrl()
        + URI_BOT_API_METADATA_SERVICES_V_62 + URI_BOT_API_METADATA_AGENTLIST;

    log.debug("Executing getAgentList request with URL: {} ", metadataUrl);

    HttpResponse httpResponse = agentforceConnection.getHttpClient().send(buildRequest(metadataUrl, agentforceConnection
        .getAccessToken(), HTTP_METHOD_GET, null));

    AgentMetadataResponseDTO agentMetadataResponse =
        objectMapper.readValue(httpResponse.getEntity().getContent(), AgentMetadataResponseDTO.class);

    return agentMetadataResponse.getRecords();
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

    completableFuture.whenComplete((response, exception) -> {
      if (exception != null) {
        callback.error(exception);
      } else {
        callback.success(parseResponseForStartSession(response.getEntity().getContent()));
      }
    });
  }

  public void continueSession(InputStream message, String sessionId, int messageSequenceNumber,
                              CompletionCallback<InputStream, InvokeAgentResponseAttributes> callback)
      throws IOException {

    String continueSessionUrl =
        agentforceConnection.getApiInstanceUrl() + V6_URI_BOT_API_BOTS + URI_BOT_API_SESSIONS + sessionId
            + URI_BOT_API_MESSAGES;

    BotContinueSessionRequestDTO payload =
        createContinueSessionRequestPayload(IOUtils.toString(message), messageSequenceNumber);

    log.debug("Agentforce continue session details. Request URL: {}, Session ID:{}", continueSessionUrl, sessionId);

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
        callback.success(parseResponseForContinueSession(response.getEntity().getContent()));
      }
    });
  }

  public void endSession(String sessionId, CompletionCallback<Void, InvokeAgentResponseAttributes> callback) throws IOException {

    String endSessionUrl = agentforceConnection.getApiInstanceUrl() + V6_URI_BOT_API_BOTS + URI_BOT_API_SESSIONS + sessionId;

    log.debug("Agentforce end session details. Request URL: {}, Session ID:{}", endSessionUrl, sessionId);

    CompletableFuture<HttpResponse> completableFuture = agentforceConnection.getHttpClient().sendAsync(buildRequest(
                                                                                                                    endSessionUrl,
                                                                                                                    agentforceConnection
                                                                                                                        .getAccessToken(),
                                                                                                                    HTTP_METHOD_DELETE,
                                                                                                                    null));

    completableFuture.whenComplete((response, exception) -> {
      if (exception != null) {
        callback.error(exception);
      } else {
        callback.success(parseResponseForDeleteSession(response.getEntity().getContent()));
      }
    });
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

  private String getTextValue(JsonNode node, String keyName) {
    return node != null && node.get(keyName) != null ? node.get(keyName).asText() : null;
  }

  private HttpRequest buildRequest(String uri, String accessToken, String httpMethod, HttpEntity httpEntity) {
    return HttpRequest.builder()
        .uri(uri)
        .headers(HTTP_METHOD_DELETE.equals(httpMethod) ? addConnectionHeadersForDelete(accessToken)
            : addConnectionHeaders(accessToken))
        .method(httpMethod)
        .entity(httpEntity != null ? httpEntity : new EmptyHttpEntity())
        .build();
  }

  private MultiMap<String, String> addConnectionHeaders(String accessToken) {
    MultiMap<String, String> multiMap = new MultiMap<>();
    multiMap.put(AUTHORIZATION, "Bearer " + accessToken);
    multiMap.put(CONTENT_TYPE_STRING, CONTENT_TYPE_APPLICATION_JSON);
    multiMap.put(ACCEPT_TYPE_STRING, CONTENT_TYPE_APPLICATION_JSON);
    return multiMap;
  }

  private MultiMap<String, String> addConnectionHeadersForDelete(String accessToken) {
    MultiMap<String, String> multiMap = addConnectionHeaders(accessToken);
    multiMap.put(X_SESSION_END_REASON, END_SESSION_REASON_USERREQUEST);
    return multiMap;
  }

  private Result<InputStream, InvokeAgentResponseAttributes> parseResponseForStartSession(InputStream responseStream) {

    AgentConversationResponseDTO responseDTO = parseResponse(responseStream);

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("sessionId", responseDTO.getSessionId());
    return Result.<InputStream, InvokeAgentResponseAttributes>builder()
        .output(toInputStream(jsonObject.toString(), StandardCharsets.UTF_8))
        .attributes(responseDTO.getResponseAttributes())
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .mediaType(MediaType.APPLICATION_JSON)
        .build();
  }

  private Result<InputStream, InvokeAgentResponseAttributes> parseResponseForContinueSession(InputStream responseStream) {

    AgentConversationResponseDTO responseDTO = parseResponse(responseStream);

    return Result.<InputStream, InvokeAgentResponseAttributes>builder()
        .output(toInputStream(responseDTO.getText(), StandardCharsets.UTF_8))
        .attributes(responseDTO.getResponseAttributes())
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .mediaType(MediaType.TEXT)
        .build();
  }

  private Result<Void, InvokeAgentResponseAttributes> parseResponseForDeleteSession(InputStream responseStream) {

    AgentConversationResponseDTO responseDTO = parseResponse(responseStream);

    return Result.<Void, InvokeAgentResponseAttributes>builder()
        .attributes(responseDTO.getResponseAttributes())
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .build();
  }

  private AgentConversationResponseDTO parseResponse(InputStream responseStream) {
    AgentConversationResponseDTO responseDTO = new AgentConversationResponseDTO();

    try {
      JsonNode rootNode = objectMapper.readTree(responseStream);
      responseDTO.setResponseAttributes(
                                        objectMapper.treeToValue(
                                                                 rootNode, InvokeAgentResponseAttributes.class));
      responseDTO.setSessionId(getTextValue(rootNode, SESSION_ID));
      responseDTO.setText(getMessageText(rootNode));
    } catch (IOException e) {
      throw new ModuleException("Error in parsing response ", AGENT_OPERATIONS_FAILURE, e);
    }
    return responseDTO;
  }
}
