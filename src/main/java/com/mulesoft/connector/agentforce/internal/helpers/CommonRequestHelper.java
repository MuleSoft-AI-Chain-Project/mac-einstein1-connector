package com.mulesoft.connector.agentforce.internal.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulesoft.connector.agentforce.internal.dto.OAuthResponseDTO;
import com.mulesoft.connector.agentforce.internal.error.AgentforceErrorType;
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

import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.CONNECTION_TIMEOUT;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.GRANT_TYPE_CLIENT_CREDENTIALS;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.HTTP_METHOD_POST;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.QUERY_PARAM_CLIENT_ID;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.QUERY_PARAM_CLIENT_SECRET;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.QUERY_PARAM_GRANT_TYPE;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.READ_TIMEOUT;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.URI_HTTPS_PREFIX;
import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.URI_OAUTH_TOKEN;


public class CommonRequestHelper {

  private static final Logger log = LoggerFactory.getLogger(CommonRequestHelper.class);

  public static String getOAuthURL(String salesforceOrg) {
    return URI_HTTPS_PREFIX + salesforceOrg + URI_OAUTH_TOKEN;
  }

  public static String getOAuthParams(String clientId, String clientSecret) {
    return QUERY_PARAM_GRANT_TYPE + "=" + GRANT_TYPE_CLIENT_CREDENTIALS
        + "&" + QUERY_PARAM_CLIENT_ID + "=" + clientId
        + "&" + QUERY_PARAM_CLIENT_SECRET + "=" + clientSecret;
  }

  public static OAuthResponseDTO getOAuthResponseDTO(String salesforceOrg, String clientId, String clientSecret)
      throws IOException {

    log.debug("Preparing request for connection for salesforce org:{}", salesforceOrg);

    String urlString = CommonRequestHelper.getOAuthURL(salesforceOrg);
    String urlParameters = CommonRequestHelper.getOAuthParams(clientId, clientSecret);
    HttpURLConnection httpConnection = createURLConnection(urlString, HTTP_METHOD_POST);

    writePayloadToConnStream(httpConnection, urlParameters);

    log.info("Executing rest {} ", urlString);
    int responseCode = httpConnection.getResponseCode();
    log.debug("Response code for connection request:{}", responseCode);
    if (responseCode == HttpURLConnection.HTTP_OK) {
      if (httpConnection.getInputStream() == null) {
        return null;
      }
      String response = readResponseStream(httpConnection.getInputStream());
      return new ObjectMapper().readValue(response, OAuthResponseDTO.class);
    }
    return null;
  }

  public static HttpURLConnection createURLConnection(String urlString, String httpMethod) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod(httpMethod);
    conn.setConnectTimeout(CONNECTION_TIMEOUT);
    conn.setReadTimeout(READ_TIMEOUT);
    conn.setDoOutput(true);
    return conn;
  }

  public static String readResponseStream(InputStream inputStream) throws IOException {
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

  public static String readErrorStream(InputStream errorStream) {
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

  public static String handleHttpResponse(HttpURLConnection httpConnection, AgentforceErrorType errorType) throws IOException {
    int responseCode = httpConnection.getResponseCode();

    if (responseCode == HttpURLConnection.HTTP_OK) {
      if (httpConnection.getInputStream() == null) {
        throw new ModuleException(
                                  "Error: No response received from Agentforce", errorType);
      }
      return readResponseStream(httpConnection.getInputStream());
    } else {
      String errorMessage = readErrorStream(httpConnection.getErrorStream());
      log.info("Error in HTTP request. Response code: {}, message: {}", responseCode, errorMessage);
      throw new ModuleException(
                                String.format("Error in HTTP request. ErrorCode: %d ," +
                                    " ErrorMessage: %s", responseCode, errorMessage),
                                errorType);
    }
  }

  public static void writePayloadToConnStream(HttpURLConnection httpConnection, String payload) throws IOException {
    try (OutputStream os = httpConnection.getOutputStream()) {
      byte[] input = payload.getBytes(StandardCharsets.UTF_8);
      os.write(input, 0, input.length);
      os.flush();
    }
  }
}
