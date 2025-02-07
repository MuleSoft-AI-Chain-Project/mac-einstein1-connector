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
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.connectivity.oauth.AccessTokenExpiredException;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
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
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.ACCEPT_TYPE_STRING;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.AUTHORIZATION;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.CONNECTION_TIME_OUT;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.CONTENT_TYPE_APPLICATION_JSON;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.CONTENT_TYPE_STRING;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.HTTP_METHOD_DELETE;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.HTTP_METHOD_GET;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.HTTP_METHOD_POST;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.READ_TIME_OUT;

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
    // System.out.println("payload = " + objectMapper.writeValueAsString(payload));
    HttpURLConnection httpConnection = createURLConnection(startSessionUrl, HTTP_METHOD_POST);
    addConnectionHeaders(httpConnection, agentforceConnection.getAccessToken());
    writePayloadToConnStream(httpConnection, objectMapper.writeValueAsString(payload));

    return parseResponse(httpConnection);
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

  public AgentConversationResponseDTO endSession(String sessionId) throws IOException {

    String endSessionUrl = agentforceConnection.getApiInstanceUrl() + V6_URI_BOT_API_BOTS + URI_BOT_API_SESSIONS + sessionId;

    log.debug("Agentforce end session details. Request URL: {}, Session ID:{}", endSessionUrl, sessionId);

    HttpURLConnection httpConnection = createURLConnection(endSessionUrl, HTTP_METHOD_DELETE);
    addConnectionHeadersForEndSession(httpConnection, agentforceConnection.getAccessToken());

    return parseResponse(httpConnection);
  }

  private void addConnectionHeaders(HttpURLConnection conn, String accessToken) {
    conn.setRequestProperty(AUTHORIZATION, "Bearer " + accessToken);
    conn.setRequestProperty(CONTENT_TYPE_STRING, CONTENT_TYPE_APPLICATION_JSON);
    conn.setRequestProperty(ACCEPT_TYPE_STRING, CONTENT_TYPE_APPLICATION_JSON);
  }

  private void addConnectionHeadersForEndSession(HttpURLConnection conn, String accessToken) {
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
    // System.out.println("responseDTO " + responseDTO);
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

  private String getTextValue(JsonNode node, String keyName) {
    return node != null && node.get(keyName) != null ? node.get(keyName).asText() : null;
  }

  private HttpURLConnection createURLConnection(String urlString, String httpMethod) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod(httpMethod);
    conn.setConnectTimeout(CONNECTION_TIME_OUT);
    conn.setReadTimeout(READ_TIME_OUT);
    conn.setDoOutput(true);
    return conn;
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

  private InputStream handleHttpResponse(HttpURLConnection httpConnection, AgentforceErrorType errorType)
      throws IOException {
    int responseCode = httpConnection.getResponseCode();

    if (responseCode == HttpURLConnection.HTTP_OK) {
      if (httpConnection.getInputStream() == null) {
        throw new ModuleException(
                                  "Error: No response received from Agentforce", errorType);
      }
      return httpConnection.getInputStream();
    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
      throw new AccessTokenExpiredException();
    } else {
      String errorMessage = readErrorStream(httpConnection.getErrorStream());
      log.info("Error in HTTP request. Response code: {}, message: {}", responseCode, errorMessage);
      throw new ModuleException(
                                String.format("Error in HTTP request. ErrorCode: %d ," +
                                    " ErrorMessage: %s", responseCode, errorMessage),
                                errorType);
    }
  }

  private void writePayloadToConnStream(HttpURLConnection httpConnection, String payload) throws IOException {
    try (OutputStream os = httpConnection.getOutputStream()) {
      byte[] input = payload.getBytes(StandardCharsets.UTF_8);
      os.write(input, 0, input.length);
      os.flush();
    }
  }
}
