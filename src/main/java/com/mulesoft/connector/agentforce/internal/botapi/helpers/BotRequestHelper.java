package com.mulesoft.connector.agentforce.internal.botapi.helpers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
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
import org.jetbrains.annotations.NotNull;
import org.mule.runtime.core.api.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import static com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper.createURLConnection;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper.handleHttpResponse;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper.writePayloadToConnStream;

public class BotRequestHelper {

  private static final Logger log = LoggerFactory.getLogger(BotRequestHelper.class);

  private final AgentforceConnection agentforceConnection;
  private final ObjectMapper objectMapper;
  private final Map<String, String> runTimeBaseUrlMap;

  public BotRequestHelper(AgentforceConnection agentforceConnection) {
    this.agentforceConnection = agentforceConnection;
    runTimeBaseUrlMap = new HashMap<>();
    objectMapper = new ObjectMapper();
  }

  public List<BotRecord> getAgentList() throws IOException {

    String metadataUrl = agentforceConnection.getSalesforceOrg()
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
    String startSessionUrl =
        getRuntimeBaseUrl() + URI_BOT_API_VERSION + URI_BOT_API_BOTS + agentId + URI_BOT_API_SESSIONS;
    String externalSessionKey = UUID.randomUUID().toString();
    String forceConfigEndpoint = agentforceConnection.getSalesforceOrg();
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();
    BotSessionRequestDTO payload = createStartSessionRequestPayload(
                                                                    externalSessionKey, forceConfigEndpoint);
    log.debug("Agentforce start session details. Request URL: {}, external Session Key:{}," +
        " forceConfigEndpoint: {}, OrgId: {}",
              startSessionUrl, externalSessionKey, forceConfigEndpoint, orgId);

    HttpURLConnection httpConnection = createURLConnection(startSessionUrl, HTTP_METHOD_POST);
    addConnectionHeaders(httpConnection, agentforceConnection.getAccessToken(), orgId);
    writePayloadToConnStream(httpConnection, objectMapper.writeValueAsString(payload));

    return getParsedHttpResponse(httpConnection);
  }

  public AgentConversationResponseDTO continueSession(InputStream message, String sessionId, int messageSequenceNumber,
                                                      String inReplyToMessageId)
      throws IOException {

    String continueSessionUrl =
        getRuntimeBaseUrl() + URI_BOT_API_VERSION + URI_BOT_API_SESSIONS + sessionId + URI_BOT_API_MESSAGES;
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();
    BotContinueSessionRequestDTO payload =
        createContinueSessionRequestPayload(IOUtils.toString(message), messageSequenceNumber, inReplyToMessageId);

    log.debug("Agentforce continue session details. Request URL: {}, Session ID:{}," +
        " OrgId: {}, inReplyToMessageId: {}",
              continueSessionUrl, sessionId, orgId, inReplyToMessageId);
    HttpURLConnection httpConnection = createURLConnection(continueSessionUrl, HTTP_METHOD_POST);
    addConnectionHeaders(httpConnection, agentforceConnection.getAccessToken(), orgId);
    writePayloadToConnStream(httpConnection, objectMapper.writeValueAsString(payload));

    return getParsedHttpResponse(httpConnection);
  }

  public AgentConversationResponseDTO endSession(String sessionId) throws IOException {

    String endSessionUrl = getRuntimeBaseUrl() + URI_BOT_API_VERSION + URI_BOT_API_SESSIONS + sessionId;
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();

    log.debug("Agentforce end session details. Request URL: {}, Session ID:{}," +
        " OrgId: {}", endSessionUrl, sessionId, orgId);

    HttpURLConnection httpConnection = createURLConnection(endSessionUrl, HTTP_METHOD_DELETE);
    addConnectionHeadersForEndSession(httpConnection, agentforceConnection.getAccessToken(), orgId);

    return getParsedHttpResponse(httpConnection);
  }

  public String getRuntimeBaseUrl() throws IOException {

    if (!runTimeBaseUrlMap.containsKey(agentforceConnection.getSalesforceOrg())) {
      runTimeBaseUrlMap.put(agentforceConnection.getSalesforceOrg(), findRuntimeBaseUrl());
    }
    return runTimeBaseUrlMap.get(agentforceConnection.getSalesforceOrg());
  }

  public String findRuntimeBaseUrl() throws IOException {

    String metadataUrl = agentforceConnection.getSalesforceOrg()
            + URI_BOT_API_METADATA_SERVICES_V_62 + URI_BOT_API_METADATA_RUNTIMEURL;

    HttpURLConnection httpConnection = createURLConnection(metadataUrl, HTTP_METHOD_GET);
    addConnectionHeaders(httpConnection, agentforceConnection.getAccessToken());

    log.debug("Executing API to fetch runtime base URL: {} ", metadataUrl);
    InputStream responseStream = handleHttpResponse(httpConnection,
            AgentforceErrorType.AGENT_METADATA_FAILURE);
    JsonNode rootNode = objectMapper.readTree(responseStream);
    String runtimeBaseURL = rootNode.get("runtimeBaseUrl").textValue();

    log.debug("Runtime base URL for connecting to agent: {} ", runtimeBaseURL);
    return runtimeBaseURL;
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

  private static InvokeAgentResponseAttributes.Message parseMessage(JsonParser parser,
                                                                    AgentConversationResponseDTO responseDTO)
      throws IOException {
    InvokeAgentResponseAttributes.Message message = new InvokeAgentResponseAttributes.Message();
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String fieldName = parser.currentName();
      parser.nextToken(); // Move to the value
      if ("id".equals(fieldName)) {
        message.setId(parser.getText());
      } else if ("schedule".equals(fieldName)) {
        InvokeAgentResponseAttributes.Schedule schedule = new InvokeAgentResponseAttributes.Schedule();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
          String scheduleFieldName = parser.currentName();
          parser.nextToken(); // Move to the value
          if ("responseDelayMilliseconds".equals(scheduleFieldName)) {
            schedule.setResponseDelayMilliseconds(parser.getIntValue());
          } else {
            parser.skipChildren();
          }
        }
        message.setSchedule(schedule);
      } else if ("type".equals(fieldName)) {
        message.setType(parser.getText());
      } else if ("text".equals(fieldName)) {
        responseDTO.setTextInputStream(new ByteArrayInputStream(parser.getText().getBytes(StandardCharsets.UTF_8)));
      } else {
        parser.skipChildren();
      }
    }
    return message;
  }

  private AgentConversationResponseDTO getParsedHttpResponse(HttpURLConnection httpConnection) throws IOException {

    InputStream responseStream = handleHttpResponse(httpConnection,
                                                    AgentforceErrorType.AGENT_OPERATIONS_FAILURE);

    AgentConversationResponseDTO responseDTO = new AgentConversationResponseDTO();
    JsonFactory jsonFactory = new JsonFactory();

    try (JsonParser jsonParser = jsonFactory.createParser(responseStream)) {

      InvokeAgentResponseAttributes responseAttributes = new InvokeAgentResponseAttributes();

      while (!jsonParser.isClosed()) {
        JsonToken token = jsonParser.nextToken();
        if (JsonToken.FIELD_NAME.equals(token)) {
          String fieldName = jsonParser.currentName();
          token = jsonParser.nextToken(); // Move to the value

          switch (fieldName) {
            case "sessionId":
              responseDTO.setSessionId(jsonParser.getText());
              break;
            case "botVersion":
              responseAttributes.setBotVersion(jsonParser.getText());
              break;
            case "messages":
              if (JsonToken.START_ARRAY.equals(token)) {
                List<InvokeAgentResponseAttributes.Message> messages = new ArrayList<>();
                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                  messages.add(parseMessage(jsonParser, responseDTO));
                }
                responseAttributes.setMessages(messages);
              }
              break;
            case "processedSequenceIds":
              responseAttributes.setProcessedSequenceIds(parseProcessedSeqIds(token, jsonParser));
              break;
            default:
              // Skip unknown fields
              jsonParser.skipChildren();
              break;
          }
        }
      }
      responseDTO.setResponseAttributes(responseAttributes);
    }
    return responseDTO;
  }

  @NotNull
  private List<Integer> parseProcessedSeqIds(JsonToken token, JsonParser jsonParser) throws IOException {
    List<Integer> sequenceIds = new ArrayList<>();
    if (JsonToken.START_ARRAY.equals(token)) {
      while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
        sequenceIds.add(jsonParser.getIntValue());
      }
    }
    return sequenceIds;
  }
}

