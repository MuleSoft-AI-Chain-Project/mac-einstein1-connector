package com.mulesoft.connector.agentforce.internal.bot.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulesoft.connector.agentforce.internal.bot.dto.BotSessionRequestDTO;
import com.mulesoft.connector.agentforce.internal.bot.dto.ForceConfigDTO;
import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.error.AgentforceErrorType;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.UUID;

import static com.mulesoft.connector.agentforce.internal.helpers.ConstantUtil.AUTHORIZATION;
import static com.mulesoft.connector.agentforce.internal.helpers.ConstantUtil.HTTP_METHOD_DELETE;
import static com.mulesoft.connector.agentforce.internal.helpers.ConstantUtil.HTTP_METHOD_GET;
import static com.mulesoft.connector.agentforce.internal.helpers.ConstantUtil.HTTP_METHOD_POST;
import static com.mulesoft.connector.agentforce.internal.helpers.ConstantUtil.URI_BOT_API_METADATA;
import static com.mulesoft.connector.agentforce.internal.helpers.ConstantUtil.URI_HTTPS_PREFIX;
import static com.mulesoft.connector.agentforce.internal.helpers.RequestHelper.createURLConnection;
import static com.mulesoft.connector.agentforce.internal.helpers.RequestHelper.readErrorStream;
import static com.mulesoft.connector.agentforce.internal.helpers.RequestHelper.readResponse;
import static com.mulesoft.connector.agentforce.internal.helpers.RequestHelper.writePayloadToConnStream;

public class BotRequestHelper {

  private static final Logger log = LoggerFactory.getLogger(BotRequestHelper.class);

  public String getAgentList(AgentforceConnection connection) throws IOException {

    String metadataUrl = URI_HTTPS_PREFIX + connection.getSalesforceOrg()
        + URI_BOT_API_METADATA;

    HttpURLConnection httpConnection = createURLConnection(metadataUrl, HTTP_METHOD_GET);
    addConnectionHeaders(httpConnection, connection.getoAuthResponseDTO().getAccessToken());

    log.debug("Executing getAgentList request with URL: {} ", metadataUrl);

    return handleHttpResponse(httpConnection, AgentforceErrorType.AGENT_METADATA_FAILURE);
  }

  //TODO: implement API call to get the runtime-base-url for connecting to agent
  public String fetchRuntimeBaseUrl() {

    return "https://runtime-api-na-west.prod.chatbots.sfdc.sh";
  }

  public String startSession(String agentId, AgentforceConnection agentforceConnection) throws IOException {

    String startSessionUrl = fetchRuntimeBaseUrl() + "/v5.3.0/bots/" + agentId + "/sessions";
    String externalSessionKey = UUID.randomUUID().toString();
    String forceConfigEndpoint = URI_HTTPS_PREFIX + agentforceConnection.getSalesforceOrg();
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();
    BotSessionRequestDTO payload = createStartSessionRequestPayload(externalSessionKey, forceConfigEndpoint);

    log.debug("Agentforce start session details. Request URL: {}, externnal Session Key:{}," +
        " forceConfigEndpoint: {}, OrgId: {}",
              startSessionUrl, externalSessionKey, forceConfigEndpoint, orgId);

    HttpURLConnection httpConnection = createURLConnection(startSessionUrl, HTTP_METHOD_POST);
    addConnectionHeaders(httpConnection, agentforceConnection.getoAuthResponseDTO().getAccessToken(), orgId);
    writePayloadToConnStream(httpConnection, new ObjectMapper().writeValueAsString(payload));

    return handleHttpResponse(httpConnection, AgentforceErrorType.AGENT_OPERATIONS_FAILURE);
  }

  public String continueSession(String message, String sessionId, AgentforceConnection agentforceConnection) throws IOException {

    String continueSessionUrl = fetchRuntimeBaseUrl() + "/v5.3.0/sessions/" + sessionId + "/messages";
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();

    HttpURLConnection httpConnection = createURLConnection(continueSessionUrl, HTTP_METHOD_POST);
    addConnectionHeaders(httpConnection, agentforceConnection.getoAuthResponseDTO().getAccessToken(), orgId);
    writePayloadToConnStream(httpConnection, message);

    return handleHttpResponse(httpConnection, AgentforceErrorType.AGENT_OPERATIONS_FAILURE);
  }

  public String endSession(String sessionId, AgentforceConnection agentforceConnection) throws IOException {

    String endSessionUrl = fetchRuntimeBaseUrl() + "/v5.3.0/sessions/" + sessionId;
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();

    HttpURLConnection httpConnection = createURLConnection(endSessionUrl, HTTP_METHOD_DELETE);
    addConnectionHeadersForEndSession(httpConnection, agentforceConnection.getoAuthResponseDTO().getAccessToken(), orgId);

    return handleHttpResponse(httpConnection, AgentforceErrorType.AGENT_OPERATIONS_FAILURE);
  }

  private static void addConnectionHeaders(HttpURLConnection conn, String accessToken) {
    conn.setRequestProperty(AUTHORIZATION, "Bearer " + accessToken);
    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
  }

  private static void addConnectionHeaders(HttpURLConnection conn, String accessToken, String orgId) {
    addConnectionHeaders(conn, accessToken);
    conn.setRequestProperty("X-Org-Id", orgId);
    conn.setRequestProperty("Accept", "application/json");
  }

  private static void addConnectionHeadersForEndSession(HttpURLConnection conn, String accessToken, String orgId) {
    addConnectionHeaders(conn, accessToken, orgId);
    conn.setRequestProperty("X-Session-End-Reason", "UserRequest");
  }

  private String handleHttpResponse(HttpURLConnection httpConnection, AgentforceErrorType errorType) throws IOException {
    int responseCode = httpConnection.getResponseCode();

    if (responseCode == HttpURLConnection.HTTP_OK) {
      if (httpConnection.getInputStream() == null) {
        throw new ModuleException(
                                  "Error: No response received from Agentforce", errorType);
      }
      return readResponse(httpConnection.getInputStream());
    } else {
      String errorMessage = readErrorStream(httpConnection.getErrorStream());
      log.info("Error in HTTP request. Response code: {}, message: {}", responseCode, errorMessage);
      throw new ModuleException(
                                String.format("Error in HTTP request. ErrorCode: %d ," +
                                    " ErrorMessage: %s", responseCode, errorMessage),
                                errorType);
    }
  }

  private BotSessionRequestDTO createStartSessionRequestPayload(String externalSessionKey, String forceConfigEndpoint) {

    ForceConfigDTO forceConfigDTO = new ForceConfigDTO();
    forceConfigDTO.setEndpoint(forceConfigEndpoint);
    return new BotSessionRequestDTO(externalSessionKey, forceConfigDTO);
  }
}
