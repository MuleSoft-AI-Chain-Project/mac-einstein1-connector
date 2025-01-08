package com.mulesoft.connector.agentforce.internal.botapi.helpers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulesoft.connector.agentforce.api.metadata.InvokeAgentResponseAttributes;
import com.mulesoft.connector.agentforce.internal.botapi.dto.AgentStartSessionResponseDTO;
import com.mulesoft.connector.agentforce.internal.botapi.dto.BotSessionRequestDTO;
import com.mulesoft.connector.agentforce.internal.botapi.dto.ForceConfigDTO;
import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.error.AgentforceErrorType;
import com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.SESSION_END_REASON_USERREQUEST;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.URI_BOT_API_METADATA;
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
import static com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper.readErrorStream;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper.writePayloadToConnStream;

public class BotRequestHelper {

  private static final Logger log = LoggerFactory.getLogger(BotRequestHelper.class);

  public String getAgentList(AgentforceConnection connection) throws IOException {

    String metadataUrl = URI_HTTPS_PREFIX + connection.getSalesforceOrg()
        + URI_BOT_API_METADATA;

    HttpURLConnection httpConnection = createURLConnection(metadataUrl, HTTP_METHOD_GET);
    addConnectionHeaders(httpConnection, connection.getoAuthResponseDTO().getAccessToken());

    log.debug("Executing getAgentList request with URL: {} ", metadataUrl);

    return CommonRequestHelper.handleHttpResponse(httpConnection, AgentforceErrorType.AGENT_METADATA_FAILURE);
  }

  //TODO: implement API call to get the runtime-base-url for connecting to agent
  public String fetchRuntimeBaseUrl() {

    return "https://runtime-api-na-west.prod.chatbots.sfdc.sh";
  }

  public AgentStartSessionResponseDTO startSession(AgentforceConnection agentforceConnection, String agentId,
                                                   InputStream initialPrompt)
      throws IOException {

    String startSessionUrl = fetchRuntimeBaseUrl() + "/v5.3.0/bots/" + agentId + "/sessions";
    String externalSessionKey = UUID.randomUUID().toString();
    String forceConfigEndpoint = URI_HTTPS_PREFIX + agentforceConnection.getSalesforceOrg();
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();
    BotSessionRequestDTO payload = createStartSessionRequestPayload(
                                                                    externalSessionKey, forceConfigEndpoint,
                                                                    IOUtils.toString(initialPrompt, StandardCharsets.UTF_8));

    log.debug("Agentforce start session details. Request URL: {}, externnal Session Key:{}," +
        " forceConfigEndpoint: {}, OrgId: {}",
              startSessionUrl, externalSessionKey, forceConfigEndpoint, orgId);

    HttpURLConnection httpConnection = createURLConnection(startSessionUrl, HTTP_METHOD_POST);
    addConnectionHeaders(httpConnection, agentforceConnection.getoAuthResponseDTO().getAccessToken(), orgId);
    writePayloadToConnStream(httpConnection, new ObjectMapper().writeValueAsString(payload));

    InputStream responseStream = handleHttpResponse(httpConnection, AgentforceErrorType.AGENT_OPERATIONS_FAILURE);

    return parseStartSessionStreamResposne(responseStream);
  }

  private AgentStartSessionResponseDTO parseStartSessionStreamResposne(InputStream responseStream) throws IOException {

    AgentStartSessionResponseDTO responseDTO = new AgentStartSessionResponseDTO();
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
              responseAttributes.setSessionId(jsonParser.getText());
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
              List<Integer> sequenceIds = new ArrayList<>();
              if (JsonToken.START_ARRAY.equals(token)) {
                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                  sequenceIds.add(jsonParser.getIntValue());
                }
              }
              responseAttributes.setProcessedSequenceIds(sequenceIds);
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

  public String continueSession(String message, String sessionId, AgentforceConnection agentforceConnection) throws IOException {

    String continueSessionUrl = fetchRuntimeBaseUrl() + "/v5.3.0/sessions/" + sessionId + "/messages";
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();

    HttpURLConnection httpConnection = createURLConnection(continueSessionUrl, HTTP_METHOD_POST);
    addConnectionHeaders(httpConnection, agentforceConnection.getoAuthResponseDTO().getAccessToken(), orgId);
    writePayloadToConnStream(httpConnection, message);

    return CommonRequestHelper.handleHttpResponse(httpConnection, AgentforceErrorType.AGENT_OPERATIONS_FAILURE);
  }

  public String endSession(String sessionId, AgentforceConnection agentforceConnection) throws IOException {

    String endSessionUrl = fetchRuntimeBaseUrl() + "/v5.3.0/sessions/" + sessionId;
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();

    HttpURLConnection httpConnection = createURLConnection(endSessionUrl, HTTP_METHOD_DELETE);
    addConnectionHeadersForEndSession(httpConnection, agentforceConnection.getoAuthResponseDTO().getAccessToken(), orgId);

    return CommonRequestHelper.handleHttpResponse(httpConnection, AgentforceErrorType.AGENT_OPERATIONS_FAILURE);
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
    conn.setRequestProperty(X_SESSION_END_REASON, SESSION_END_REASON_USERREQUEST);
  }

  private BotSessionRequestDTO createStartSessionRequestPayload(String externalSessionKey,
                                                                String forceConfigEndpoint,
                                                                String initialPrompt) {

    ForceConfigDTO forceConfigDTO = new ForceConfigDTO();
    forceConfigDTO.setEndpoint(forceConfigEndpoint);
    BotSessionRequestDTO.Message message = new BotSessionRequestDTO.Message(initialPrompt);
    return new BotSessionRequestDTO(externalSessionKey, forceConfigDTO, message);
  }

  public static InputStream handleHttpResponse(HttpURLConnection httpConnection, AgentforceErrorType errorType)
      throws IOException {
    int responseCode = httpConnection.getResponseCode();

    if (responseCode == HttpURLConnection.HTTP_OK) {
      if (httpConnection.getInputStream() == null) {
        throw new ModuleException(
                                  "Error: No response received from Agentforce", errorType);
      }
      return httpConnection.getInputStream();
    } else {
      String errorMessage = readErrorStream(httpConnection.getErrorStream());
      log.info("Error in HTTP request. Response code: {}, message: {}", responseCode, errorMessage);
      throw new ModuleException(
                                String.format("Error in HTTP request. ErrorCode: %d ," +
                                    " ErrorMessage: %s", responseCode, errorMessage),
                                errorType);
    }
  }

  private static InvokeAgentResponseAttributes.Message parseMessage(JsonParser parser,
                                                                    AgentStartSessionResponseDTO responseDTO)
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


}
