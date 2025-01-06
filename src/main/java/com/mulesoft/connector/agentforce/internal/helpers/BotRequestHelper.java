package com.mulesoft.connector.agentforce.internal.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulesoft.connector.agentforce.internal.connection.AgentforceConnection;
import com.mulesoft.connector.agentforce.internal.dto.AgentMetadataResponse;
import com.mulesoft.connector.agentforce.internal.dto.BotRecord;
import com.mulesoft.connector.agentforce.internal.dto.OAuthResponseDTO;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mulesoft.connector.agentforce.internal.helpers.ConstantUtil.*;
import static com.mulesoft.connector.agentforce.internal.helpers.RequestHelper.*;

public class BotRequestHelper {

  private static final Logger log = LoggerFactory.getLogger(BotRequestHelper.class);

  public String getAgentList(AgentforceConnection connection) throws IOException {

    String metadataUrl = URI_HTTPS_PREFIX + connection.getSalesforceOrg()
        + URI_BOT_API_METADATA;

    HttpURLConnection httpConnection = createURLConnectionForGET(metadataUrl);
    populateConnectionObjectForOAuth(httpConnection, connection.getoAuthResponseDTO().getAccessToken());

    log.debug("Executing getAgentList request with URL: {} ", metadataUrl);

    int responseCode = httpConnection.getResponseCode();

    if (responseCode == HttpURLConnection.HTTP_OK) {
      if (httpConnection.getInputStream() == null) {
        return "Error: No response received from Agentforce";
      }
      return readResponse(httpConnection.getInputStream());
    } else {
      String errorMessage = readErrorStream(httpConnection.getErrorStream());
      log.info("Error in getAgentList request. Response code: {}, message: {}", responseCode, errorMessage);
      return String.format("Error: %d", responseCode);
    }
  }

  private static HttpURLConnection createURLConnectionForGET(String urlString) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setConnectTimeout(CONNECTION_TIMEOUT);
    conn.setReadTimeout(READ_TIMEOUT);
    conn.setDoOutput(true);
    return conn;
  }

  private static void populateConnectionObjectForOAuth(HttpURLConnection conn, String accessToken) {
    conn.setRequestProperty(AUTHORIZATION, "Bearer " + accessToken);
    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
  }

  private static String readResponse(InputStream inputStream) throws IOException {
    try (java.io.BufferedReader br = new java.io.BufferedReader(
                                                                new java.io.InputStreamReader(inputStream,
                                                                                              StandardCharsets.UTF_8))) {
      StringBuilder response = new StringBuilder();
      String responseLine;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
      return response.toString();
    }
  }

  private static String readErrorStream(InputStream errorStream) {
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

  public Map<String, String> getAgentMetadata(AgentforceConnection agentforceConnection) throws IOException {

    String metadataUrl = URI_HTTPS_PREFIX + agentforceConnection.getSalesforceOrg()
        + URI_BOT_API_METADATA;

    log.debug("Final Metadata URL:{}", metadataUrl);
    String jsonResponse = null;//getAgentList(agentforceConnection.getoAuthResponseDTO().getAccessToken(), metadataUrl);
    System.out.println("jsonResponse= " + jsonResponse);
    AgentMetadataResponse agentMetadataResponse = new ObjectMapper().readValue(jsonResponse, AgentMetadataResponse.class);
    Map<String, String> map = new HashMap<>();
    for (BotRecord botRecord : agentMetadataResponse.getRecords()) {
      if (botRecord.getStatus().equals("Active")) {
        /*
        String botUrl = botRecord.getBotDefinition().getAttributes().getUrl();
        System.out.println("botUrl = " + botUrl);
        String[] parts = botUrl.split("/");
        String botId = parts[parts.length - 1];
        System.out.println("Extracted ID: " + botId);
        String botName = botRecord.getBotDefinition().getMasterLabel();
         */
        String botId = botRecord.getBotDefinitionId();
        System.out.println("botId = " + botId);
        String botName = botRecord.getBotDefinition().getMasterLabel();
        map.put(botName, botId);
      }
    }
    return map;
  }

  public String startSession(String agentId, AgentforceConnection agentforceConnection) throws IOException {

    String externalSessionKey = UUID.randomUUID().toString();
    String forceConfigEndpoint = URI_HTTPS_PREFIX + agentforceConnection.getSalesforceOrg();
    String payload = constructJsonPayloadForAgentStartSession(externalSessionKey, forceConfigEndpoint);

    String response = executeAgentforceCopilotStartSession(agentforceConnection.getoAuthResponseDTO(),
                                                           payload, agentId, "sessions");
    System.out.println("response = " + response);
    return response;
  }

  public String continueSession(String body, String sessionId, AgentforceConnection agentforceConnection) throws IOException {
    String url = constructUrlPayloadForAgentContinueSession(body, sessionId);
    String xorgId = agentforceConnection.getoAuthResponseDTO().getOrgId();
    xorgId = "00DdL00000DEu66UAD";
    String response = executeContinueSession(agentforceConnection.getoAuthResponseDTO().getAccessToken(),
                                             body, xorgId, url);
    System.out.println("response = " + response);
    return response;
  }

  public String endSession(String sessionId, AgentforceConnection agentforceConnection) throws IOException {
    String url = "https://runtime-api-na-west.prod.chatbots.sfdc.sh/v5.1.0/sessions/" + sessionId;
    System.out.println("url = " + url);
    String xorgId = agentforceConnection.getoAuthResponseDTO().getOrgId();
    xorgId = "00DdL00000DEu66UAD";
    String response =
        executeEndSession(agentforceConnection.getoAuthResponseDTO().getAccessToken(), xorgId, url);
    System.out.println("response = " + response);
    return response;
  }

  private String constructUrlPayloadForAgentContinueSession(String body, String sessionId) {
    String urlString = "https://runtime-api-na-west.prod.chatbots.sfdc.sh/v5.1.0/sessions/" + sessionId + "/messages";
    log.debug("Agentforce Request URL: {}", urlString);
    System.out.println("urlString = " + urlString);
    return urlString;
  }

  private String executeAgentforceCopilotStartSession(OAuthResponseDTO accessTokenDTO, String payload, String agentId,
                                                      String sessions)
      throws IOException {

    String urlString = "https://runtime-api-na-west.prod.chatbots.sfdc.sh/v5.1.0/bots/" + agentId + "/" + sessions;
    log.debug("Agentforce Request URL: {}", urlString);
    String xorgId = accessTokenDTO.getOrgId();
    // xorgId = "00DdL00000DEu66UAD";
    return executeStartSession(accessTokenDTO.getAccessToken(), payload, xorgId, urlString);
  }

  private String constructJsonPayloadForAgentStartSession(String externalSessionKey, String forceConfigEndpoint) {
    JSONObject body = new JSONObject();
    body.put("externalSessionKey", externalSessionKey);
    JSONObject forceConfig = new JSONObject();
    forceConfig.put("endpoint", forceConfigEndpoint);
    body.put("forceConfig", forceConfig);
    System.out.println("Json = " + body.toString());
    return body.toString();
  }
}
