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
import java.util.stream.Collectors;

import static com.mulesoft.connector.agentforce.internal.helpers.CommonConstantUtil.*;
import static com.mulesoft.connector.agentforce.internal.modelsapi.helpers.ConstantUtil.*;


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

  public static String executeRESTForInputStream(String accessToken, InputStream inputStream, String urlString)
      throws IOException {

    //String text =
    //  new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
    //System.out.println("inputStream = " + text);
    HttpURLConnection httpConnection = creteURLConnection(urlString);
    populateConnectionObject(httpConnection, accessToken);
    httpConnection.setDoOutput(true);
    httpConnection.setDoInput(true);
    // httpConnection.setRequestProperty("Content-Type", "application/octet-stream");

    /*
    try (OutputStream outputStream = httpConnection.getOutputStream()) {
      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      outputStream.flush();
    
    }
    */
    try (OutputStream outputStream = httpConnection.getOutputStream();
        InputStream inputStream1 = inputStream) { // Ensure inputStream is closed after use
      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = inputStream1.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      outputStream.flush();
    } catch (IOException e) {
      // Handle exception
      throw new IOException("Error writing to output stream", e);
    }

    log.info("Executing rest {} ", urlString);
    int responseCode = httpConnection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      if (httpConnection.getInputStream() == null) {
        return "Error: No response received from Agentforce";
      }
      return readResponseStream(httpConnection.getInputStream());
    } else {
      String errorMessage = readErrorStream(httpConnection.getErrorStream());
      log.debug("Error response code: {}, message: {}", responseCode, errorMessage);
      System.out.println("Error response code: " + responseCode + ", message:  " + errorMessage);
      return String.format("Error: %d", responseCode);
    }
  }

 // private static String readResponse(InputStream inputStream) throws IOException {

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

  private static HttpURLConnection creteURLConnection(String urlString) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod(HTTP_METHOD_POST);
    conn.setConnectTimeout(CONNECTION_TIMEOUT);
    conn.setReadTimeout(READ_TIMEOUT);
    conn.setDoOutput(true);
    return conn;
  }

  private static HttpURLConnection creteURLConnection2(String urlString) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setConnectTimeout(CONNECTION_TIMEOUT);
    conn.setReadTimeout(READ_TIMEOUT);
    conn.setDoOutput(true);
    return conn;
  }

  private static HttpURLConnection creteURLConnectionForDelete(String urlString) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("DELETE");
    conn.setConnectTimeout(CONNECTION_TIMEOUT);
    conn.setReadTimeout(READ_TIMEOUT);
    conn.setDoOutput(true);
    return conn;
  }

  private static void populateConnectionObject(HttpURLConnection conn, String accessToken) throws IOException {
    conn.setRequestProperty(AUTHORIZATION, "Bearer " + accessToken);
    conn.setRequestProperty(X_SFDC_APP_CONTEXT, EINSTEIN_GPT);
    conn.setRequestProperty(X_CLIENT_FEATURE_ID, AI_PLATFORM_MODELS_CONNECTED_APP);
    conn.setRequestProperty(CONTENT_TYPE_STRING, CONTENT_TYPE_APPLICATION_JSON);
  }


  private static void populateConnectionObject2(HttpURLConnection conn, String accessToken, String xorgId)
      throws IOException {
    conn.setRequestProperty(AUTHORIZATION, "Bearer " + accessToken);
    conn.setRequestProperty("X-Org-Id", xorgId);
    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
    conn.setRequestProperty("Accept", "application/json");
  }

  private static void populateConnectionObjectForEndSession(HttpURLConnection conn, String accessToken, String xorgId)
      throws IOException {
    conn.setRequestProperty(AUTHORIZATION, "Bearer " + accessToken);
    conn.setRequestProperty("X-Org-Id", xorgId);
    conn.setRequestProperty("Accept", "application/json");
    conn.setRequestProperty("X-Session-End-Reason", "UserRequest");
  }

  private static void populateConnectionObject3(HttpURLConnection conn, String accessToken) throws IOException {
    conn.setRequestProperty(AUTHORIZATION, "Bearer " + accessToken);
    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
  }

  //private static String readErrorStream(InputStream errorStream) {
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

  public static void writePayloadToConnStream(HttpURLConnection httpConnection, String payload) throws IOException {
    try (OutputStream os = httpConnection.getOutputStream()) {
      byte[] input = payload.getBytes(StandardCharsets.UTF_8);
      os.write(input, 0, input.length);
      os.flush();
    }
  }
}
