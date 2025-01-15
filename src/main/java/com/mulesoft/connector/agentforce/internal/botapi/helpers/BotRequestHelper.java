package com.mulesoft.connector.agentforce.internal.botapi.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulesoft.connector.agentforce.api.metadata.InvokeAgentResponseAttributes;
import com.mulesoft.connector.agentforce.internal.botapi.dto.AgentConversationResponseDTO;
import com.mulesoft.connector.agentforce.internal.botapi.dto.AgentMetadataResponseDTO;
import com.mulesoft.connector.agentforce.internal.botapi.dto.BotContinueSessionRequestDTO;
import com.mulesoft.connector.agentforce.internal.botapi.dto.BotRecord;
import com.mulesoft.connector.agentforce.internal.botapi.dto.BotSessionRequestDTO;
import com.mulesoft.connector.agentforce.internal.botapi.dto.ForceConfigDTO;
import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.error.AgentforceErrorType;
import org.mule.runtime.core.api.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.CONTINUE_SESSION_MESSAGE_TYPE_TEXT;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.END_SESSION_REASON_USERREQUEST;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.URI_BOT_API_BOTS;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.URI_BOT_API_MESSAGES;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.URI_BOT_API_METADATA_AGENTLIST;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.URI_BOT_API_METADATA_RUNTIMEURL;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.URI_BOT_API_METADATA_SERVICES_V_62;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.URI_BOT_API_SESSIONS;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.URI_BOT_API_VERSION;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.X_ORG_ID;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.X_SESSION_END_REASON;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.AUTHORIZATION;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.CONTENT_TYPE_APPLICATION_JSON;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.CONTENT_TYPE_STRING;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.HTTP_METHOD_DELETE;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.HTTP_METHOD_GET;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.HTTP_METHOD_POST;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.URI_HTTPS_PREFIX;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper.createURLConnection;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper.handleHttpResponse;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper.writePayloadToConnStream;

public class BotRequestHelper {

  private static final Logger log = LoggerFactory.getLogger(BotRequestHelper.class);

  ObjectMapper objectMapper = new ObjectMapper();
  HashMap<String, String> runTimeBaseUrlMap = new HashMap<>(); //Storing runtimebase URL for an org, to limit number of API calls

  public List<BotRecord> getAgentList(AgentforceConnection connection) throws IOException {

    String metadataUrl = URI_HTTPS_PREFIX + connection.getSalesforceOrg()
        + URI_BOT_API_METADATA_SERVICES_V_62 + URI_BOT_API_METADATA_AGENTLIST;

    HttpURLConnection httpConnection = createURLConnection(metadataUrl, HTTP_METHOD_GET);
    addConnectionHeaders(httpConnection, connection.getoAuthResponseDTO().getAccessToken());

    log.debug("Executing getAgentList request with URL: {} ", metadataUrl);
    InputStream responseStream = handleHttpResponse(httpConnection,
                                                    AgentforceErrorType.AGENT_METADATA_FAILURE);
    AgentMetadataResponseDTO agentMetadataResponse = objectMapper.readValue(responseStream, AgentMetadataResponseDTO.class);

    return agentMetadataResponse.getRecords();
  }

  public String getRuntimeBaseUrl(AgentforceConnection connection) throws IOException {

    if (!runTimeBaseUrlMap.containsKey(connection.getSalesforceOrg())) {
      runTimeBaseUrlMap.put(connection.getSalesforceOrg(), findRuntimeBaseUrl(connection));
    }
    return runTimeBaseUrlMap.get(connection.getSalesforceOrg());
  }

  public AgentConversationResponseDTO startSession(AgentforceConnection agentforceConnection, String agentId)
      throws IOException {

    String startSessionUrl =
        getRuntimeBaseUrl(agentforceConnection) + URI_BOT_API_VERSION + URI_BOT_API_BOTS + agentId + URI_BOT_API_SESSIONS;
    String externalSessionKey = UUID.randomUUID().toString();
    String forceConfigEndpoint = URI_HTTPS_PREFIX + agentforceConnection.getSalesforceOrg();
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();
    BotSessionRequestDTO payload = createStartSessionRequestPayload(
                                                                    externalSessionKey, forceConfigEndpoint);

    log.debug("Agentforce start session details. Request URL: {}, external Session Key:{}," +
        " forceConfigEndpoint: {}, OrgId: {}",
              startSessionUrl, externalSessionKey, forceConfigEndpoint, orgId);

    HttpURLConnection httpConnection = createURLConnection(startSessionUrl, HTTP_METHOD_POST);
    addConnectionHeaders(httpConnection, agentforceConnection.getoAuthResponseDTO().getAccessToken(), orgId);
    writePayloadToConnStream(httpConnection, objectMapper.writeValueAsString(payload));

    return parseResponse(httpConnection);
  }

  public AgentConversationResponseDTO continueSession(InputStream message, String sessionId, int messageSequenceNumber,
                                                      String inReplyToMessageId, AgentforceConnection agentforceConnection)
      throws IOException {

    String continueSessionUrl =
        getRuntimeBaseUrl(agentforceConnection) + URI_BOT_API_VERSION + URI_BOT_API_SESSIONS + sessionId + URI_BOT_API_MESSAGES;
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();
    BotContinueSessionRequestDTO payload =
        createContinueSessionRequestPayload(IOUtils.toString(message), messageSequenceNumber, inReplyToMessageId);

    log.debug("Agentforce continue session details. Request URL: {}, Session ID:{}," +
        " OrgId: {}, inReplyToMessageId: {}",
              continueSessionUrl, sessionId, orgId, inReplyToMessageId);
    HttpURLConnection httpConnection = createURLConnection(continueSessionUrl, HTTP_METHOD_POST);
    addConnectionHeaders(httpConnection, agentforceConnection.getoAuthResponseDTO().getAccessToken(), orgId);
    writePayloadToConnStream(httpConnection, objectMapper.writeValueAsString(payload));

    return parseResponse(httpConnection);
  }

  public AgentConversationResponseDTO endSession(String sessionId, AgentforceConnection agentforceConnection) throws IOException {

    String endSessionUrl = getRuntimeBaseUrl(agentforceConnection) + URI_BOT_API_VERSION + URI_BOT_API_SESSIONS + sessionId;
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();

    log.debug("Agentforce end session details. Request URL: {}, Session ID:{}," +
        " OrgId: {}", endSessionUrl, sessionId, orgId);

    HttpURLConnection httpConnection = createURLConnection(endSessionUrl, HTTP_METHOD_DELETE);
    addConnectionHeadersForEndSession(httpConnection, agentforceConnection.getoAuthResponseDTO().getAccessToken(), orgId);

    return parseResponse(httpConnection);
  }

  private static void addConnectionHeaders(HttpURLConnection conn, String accessToken) {
    conn.setRequestProperty(AUTHORIZATION, "Bearer " + accessToken);
    conn.setRequestProperty(CONTENT_TYPE_STRING, CONTENT_TYPE_APPLICATION_JSON);
  }

  private static void addConnectionHeaders(HttpURLConnection conn, String accessToken, String orgId) {
    addConnectionHeaders(conn, accessToken);
    conn.setRequestProperty(X_ORG_ID, orgId);
    conn.setRequestProperty("Accept", "application/json");
  }

  private static void addConnectionHeadersForEndSession(HttpURLConnection conn, String accessToken, String orgId) {
    addConnectionHeaders(conn, accessToken, orgId);
    conn.setRequestProperty(X_SESSION_END_REASON, END_SESSION_REASON_USERREQUEST);
  }

  private BotSessionRequestDTO createStartSessionRequestPayload(String externalSessionKey,
                                                                String forceConfigEndpoint) {

    ForceConfigDTO forceConfigDTO = new ForceConfigDTO();
    forceConfigDTO.setEndpoint(forceConfigEndpoint);
    return new BotSessionRequestDTO(externalSessionKey, forceConfigDTO);
  }

  private BotContinueSessionRequestDTO createContinueSessionRequestPayload(String message,
                                                                           int messageSequenceNumber, String inReplyToMessageId) {

    BotContinueSessionRequestDTO.Message messageDTO = new BotContinueSessionRequestDTO.Message();
    messageDTO.setText(message);
    messageDTO.setSequenceId(messageSequenceNumber);
    messageDTO.setInReplyToMessageId(inReplyToMessageId);
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
    responseDTO.setSessionId(getTextValue(rootNode, "sessionId"));
    responseDTO.setText(getMessageText(rootNode));

    return responseDTO;
  }

  private String getMessageText(JsonNode rootNode) {
    JsonNode messagesNode = rootNode.get("messages");
    StringBuilder text = new StringBuilder();
    if (messagesNode != null && messagesNode.isArray()) {
      for (JsonNode messageNode : messagesNode) {
        text.append(getTextValue(messageNode, "text")).append(" ");
      }
    }
    return text.toString();
  }

  private static String getTextValue(JsonNode node, String keyName) {
    return node != null && node.get(keyName) != null ? node.get(keyName).asText() : null;
  }

  private String findRuntimeBaseUrl(AgentforceConnection connection) throws IOException {

    String metadataUrl = URI_HTTPS_PREFIX + connection.getSalesforceOrg()
        + URI_BOT_API_METADATA_SERVICES_V_62 + URI_BOT_API_METADATA_RUNTIMEURL;

    HttpURLConnection httpConnection = createURLConnection(metadataUrl, HTTP_METHOD_GET);
    addConnectionHeaders(httpConnection, connection.getoAuthResponseDTO().getAccessToken());

    log.debug("Executing API to fetch runtime base URL: {} ", metadataUrl);
    InputStream responseStream = handleHttpResponse(httpConnection,
                                                    AgentforceErrorType.AGENT_METADATA_FAILURE);
    JsonNode rootNode = objectMapper.readTree(responseStream);
    String runtimeBaseURL = rootNode.get("runtimeBaseUrl").textValue();

    log.debug("Runtime base URL for connecting to agent: {} ", runtimeBaseURL);
    return runtimeBaseURL;
  }
}
