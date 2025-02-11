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
import org.mule.runtime.extension.api.connectivity.oauth.AccessTokenExpiredException;
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.CONTINUE_SESSION_MESSAGE_TYPE_TEXT;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.END_SESSION_REASON_USERREQUEST;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.MESSAGE;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.MESSAGES;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.SESSION_ENDED;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.SESSION_ID;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.SLASH;
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

    log.info("Executing getAgentList request with URL: {} ", metadataUrl);

    HttpResponse httpResponse = agentforceConnection.getHttpClient().send(buildRequest(metadataUrl, agentforceConnection
        .getAccessToken(), HTTP_METHOD_GET, null));

    InputStream inputStream = parseHttpResponse(httpResponse);

    AgentMetadataResponseDTO agentMetadataResponse =
        objectMapper.readValue(inputStream, AgentMetadataResponseDTO.class);

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
        " endpoint: {}, payload: {}", startSessionUrl, externalSessionKey, endpoint,
              objectMapper.writeValueAsString(payload));

    InputStream payloadStream = new ByteArrayInputStream(objectMapper.writeValueAsString(payload)
        .getBytes(StandardCharsets.UTF_8));

    sendRequest(startSessionUrl, HTTP_METHOD_POST, payloadStream, callback, this::parseResponseForStartSession);
  }

  public void continueSession(InputStream message, String sessionId, int messageSequenceNumber,
                              CompletionCallback<InputStream, InvokeAgentResponseAttributes> callback)
      throws IOException {

    String continueSessionUrl =
        agentforceConnection.getApiInstanceUrl() + V6_URI_BOT_API_BOTS + URI_BOT_API_SESSIONS + SLASH + sessionId
            + URI_BOT_API_MESSAGES;

    BotContinueSessionRequestDTO payload =
        createContinueSessionRequestPayload(IOUtils.toString(message), messageSequenceNumber);

    log.info("Agentforce continue session details. Request URL: {}, Session ID:{}", continueSessionUrl, sessionId);

    InputStream payloadStream = new ByteArrayInputStream(objectMapper.writeValueAsString(payload)
        .getBytes(StandardCharsets.UTF_8));

    sendRequest(continueSessionUrl, HTTP_METHOD_POST, payloadStream, callback, this::parseResponseForContinueSession);
  }

  public void endSession(String sessionId, CompletionCallback<InputStream, InvokeAgentResponseAttributes> callback) {

    String endSessionUrl =
        agentforceConnection.getApiInstanceUrl() + V6_URI_BOT_API_BOTS + URI_BOT_API_SESSIONS + SLASH + sessionId;

    log.debug("Agentforce end session details. Request URL: {}, Session ID:{}", endSessionUrl, sessionId);

    sendRequest(endSessionUrl, HTTP_METHOD_DELETE, null, callback, this::parseResponseForDeleteSession);
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

  private Result<InputStream, InvokeAgentResponseAttributes> parseResponseForDeleteSession(InputStream responseStream) {

    AgentConversationResponseDTO responseDTO = parseResponse(responseStream);

    return Result.<InputStream, InvokeAgentResponseAttributes>builder()
        .output(toInputStream(SESSION_ENDED, StandardCharsets.UTF_8))
        .attributes(responseDTO.getResponseAttributes())
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .mediaType(MediaType.TEXT)
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

  private String readErrorStream(InputStream errorStream) {
    if (errorStream == null) {
      return "No error details available.";
    }
    try (BufferedReader br = new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8))) {
      StringBuilder errorResponse = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        errorResponse.append(line.trim());
      }
      return errorResponse.toString();
    } catch (IOException e) {
      log.debug("Error reading error stream", e);
      return "Unable to get response from Agentforce. Could not read reading error details as well.";
    }
  }

  private <T> void sendRequest(String url, String httpMethod, InputStream payloadStream,
                               CompletionCallback<T, InvokeAgentResponseAttributes> callback,
                               Function<InputStream, Result<T, InvokeAgentResponseAttributes>> responseParser) {
    log.debug("Agentforce request details. Request URL: {}", url);

    CompletableFuture<HttpResponse> completableFuture = agentforceConnection.getHttpClient().sendAsync(
                                                                                                       buildRequest(url,
                                                                                                                    agentforceConnection
                                                                                                                        .getAccessToken(),
                                                                                                                    httpMethod,
                                                                                                                    payloadStream != null
                                                                                                                        ? new InputStreamHttpEntity(payloadStream)
                                                                                                                        : new EmptyHttpEntity()));

    completableFuture.whenComplete((response, exception) -> handleResponse(response, exception, callback, responseParser));
  }

  private <T> void handleResponse(HttpResponse response, Throwable exception,
                                  CompletionCallback<T, InvokeAgentResponseAttributes> callback,
                                  Function<InputStream, Result<T, InvokeAgentResponseAttributes>> responseParser) {
    if (exception != null) {
      callback.error(exception);
      return;
    }
    InputStream contentStream = parseHttpResponse(response, callback);
    if (contentStream == null) {
      return;
    }
    callback.success(responseParser.apply(contentStream));
  }

  private InputStream parseHttpResponse(HttpResponse httpResponse) {

    int statusCode = httpResponse.getStatusCode();
    log.debug("Parsing Http Response, statusCode = {}", statusCode);

    if (statusCode == HttpURLConnection.HTTP_OK) {
      if (httpResponse.getEntity().getContent() == null) {
        throw new ModuleException(
                                  "Error: No response received from Einstein", AgentforceErrorType.AGENT_METADATA_FAILURE);
      }
      return httpResponse.getEntity().getContent();
    } else if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
      throw new AccessTokenExpiredException();
    } else {
      String errorMessage = readErrorStream(httpResponse.getEntity().getContent());
      log.debug("Error in HTTP request. Response code: {}, message: {}", statusCode, errorMessage);
      throw new ModuleException(
                                String.format("Error in HTTP request. ErrorCode: %d, ErrorMessage: %s", statusCode, errorMessage),
                                AgentforceErrorType.AGENT_OPERATIONS_FAILURE);
    }
  }

  private InputStream parseHttpResponse(HttpResponse httpResponse, CompletionCallback callback) {

    int statusCode = httpResponse.getStatusCode();
    log.debug("Parsing Http Response, statusCode = {}", statusCode);

    if (statusCode == HttpURLConnection.HTTP_OK) {
      if (httpResponse.getEntity().getContent() == null) {
        callback.error(new ModuleException(
                                           "Error: No response received from Einstein", AGENT_OPERATIONS_FAILURE));
      }
      return httpResponse.getEntity().getContent();
    } else if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
      callback.error(new AccessTokenExpiredException());
    } else {
      String errorMessage = readErrorStream(httpResponse.getEntity().getContent());
      log.info("Error in HTTP request. Response code: {}, message: {}", statusCode, errorMessage);
      callback.error(new ModuleException(String.format("Error in HTTP request. ErrorCode: %d, ErrorMessage: %s", statusCode,
                                                       errorMessage),
                                         AgentforceErrorType.AGENT_OPERATIONS_FAILURE));
    }
    return null;
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

  private HttpRequest buildRequest(String url, String accessToken, String httpMethod, HttpEntity httpEntity) {
    return HttpRequest.builder()
        .uri(url)
        .headers(HTTP_METHOD_DELETE.equals(httpMethod) ? addConnectionHeadersForDelete(accessToken)
            : addConnectionHeaders(accessToken))
        .method(httpMethod)
        .entity(httpEntity)
        .build();
  }
}
