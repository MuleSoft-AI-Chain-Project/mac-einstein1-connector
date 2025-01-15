package com.mulesoft.connector.agentforce.internal.botapi.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulesoft.connector.agentforce.internal.botapi.dto.BotSessionRequestDTO;
import com.mulesoft.connector.agentforce.internal.botapi.dto.ForceConfigDTO;
import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.error.AgentforceErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.UUID;

import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.AUTHORIZATION;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.CONTENT_TYPE_APPLICATION_JSON;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.CONTENT_TYPE_STRING;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.HTTP_METHOD_DELETE;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.HTTP_METHOD_GET;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.HTTP_METHOD_POST;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.SESSION_END_REASON_USERREQUEST;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.URI_BOT_API_METADATA;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.URI_HTTPS_PREFIX;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.X_ORG_ID;
import static com.mulesoft.connector.agentforce.internal.botapi.helpers.BotConstantUtil.X_SESSION_END_REASON;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper.createURLConnection;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper.handleHttpResponse;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonRequestHelper.writePayloadToConnStream;

public class BotRequestHelperImpl implements BotRequestHelper {

  private static final Logger log = LoggerFactory.getLogger(BotRequestHelperImpl.class);

  private AgentforceConnection agentforceConnection;

  public BotRequestHelperImpl(AgentforceConnection agentforceConnection) {
    this.agentforceConnection = agentforceConnection;
  }

  @Override
  public String getAgentList() throws IOException {

    String metadataUrl = URI_HTTPS_PREFIX + agentforceConnection.getSalesforceOrg()
        + URI_BOT_API_METADATA;

    HttpURLConnection httpConnection = createURLConnection(metadataUrl, HTTP_METHOD_GET);
    addConnectionHeaders(httpConnection, agentforceConnection.getAccessToken());

    log.debug("Executing getAgentList request with URL: {} ", metadataUrl);

    return handleHttpResponse(httpConnection, AgentforceErrorType.AGENT_METADATA_FAILURE);
  }

  //TODO: implement API call to get the runtime-base-url for connecting to agent
  private String fetchRuntimeBaseUrl() {
    return "https://runtime-api-na-west.prod.chatbots.sfdc.sh";
  }

  @Override
  public String startSession(String agentId) throws IOException {

    String startSessionUrl = fetchRuntimeBaseUrl() + "/v5.3.0/bots/" + agentId + "/sessions";
    String externalSessionKey = UUID.randomUUID().toString();
    String forceConfigEndpoint = URI_HTTPS_PREFIX + agentforceConnection.getSalesforceOrg();
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();
    BotSessionRequestDTO payload = createStartSessionRequestPayload(externalSessionKey, forceConfigEndpoint);

    log.debug("Agentforce start session details. Request URL: {}, externnal Session Key:{}," +
        " forceConfigEndpoint: {}, OrgId: {}",
              startSessionUrl, externalSessionKey, forceConfigEndpoint, orgId);

    HttpURLConnection httpConnection = createURLConnection(startSessionUrl, HTTP_METHOD_POST);
    addConnectionHeaders(httpConnection, agentforceConnection.getAccessToken(), orgId);
    writePayloadToConnStream(httpConnection, new ObjectMapper().writeValueAsString(payload));

    return handleHttpResponse(httpConnection, AgentforceErrorType.AGENT_OPERATIONS_FAILURE);
  }

  @Override
  public String continueSession(String message, String sessionId) throws IOException {

    String continueSessionUrl = fetchRuntimeBaseUrl() + "/v5.3.0/sessions/" + sessionId + "/messages";
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();

    HttpURLConnection httpConnection = createURLConnection(continueSessionUrl, HTTP_METHOD_POST);
    addConnectionHeaders(httpConnection, agentforceConnection.getAccessToken(), orgId);
    writePayloadToConnStream(httpConnection, message);

    return handleHttpResponse(httpConnection, AgentforceErrorType.AGENT_OPERATIONS_FAILURE);
  }

  @Override
  public String endSession(String sessionId) throws IOException {

    String endSessionUrl = fetchRuntimeBaseUrl() + "/v5.3.0/sessions/" + sessionId;
    String orgId = agentforceConnection.getoAuthResponseDTO().getOrgId();

    HttpURLConnection httpConnection = createURLConnection(endSessionUrl, HTTP_METHOD_DELETE);
    addConnectionHeadersForEndSession(httpConnection, agentforceConnection.getAccessToken(), orgId);

    return handleHttpResponse(httpConnection, AgentforceErrorType.AGENT_OPERATIONS_FAILURE);
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

  private BotSessionRequestDTO createStartSessionRequestPayload(String externalSessionKey, String forceConfigEndpoint) {

    ForceConfigDTO forceConfigDTO = new ForceConfigDTO();
    forceConfigDTO.setEndpoint(forceConfigEndpoint);
    return new BotSessionRequestDTO(externalSessionKey, forceConfigDTO);
  }
}
