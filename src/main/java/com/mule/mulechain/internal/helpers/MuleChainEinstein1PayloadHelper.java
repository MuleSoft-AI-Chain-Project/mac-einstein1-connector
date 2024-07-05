package com.mule.mulechain.internal.helpers;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mule.mulechain.internal.MuleChainEinstein1Configuration;
import com.mule.mulechain.internal.models.MuleChainEinstein1ParamsEmbeddingDetails;
import com.mule.mulechain.internal.models.MuleChainEinstein1ParamsModelDetails;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class MuleChainEinstein1PayloadHelper {

    private static final Map<String, String> modelMapping = new HashMap<>();
    private static final String URL_BASE = "https://api.salesforce.com/einstein/platform/v1/models/";

    static {
        modelMapping.put("Azure OpenAI Ada 002", "sfdc_ai__DefaultAzureOpenAITextEmbeddingAda_002");
        modelMapping.put("Azure OpenAI GPT 3.5 Turbo", "sfdc_ai__DefaultAzureOpenAIGPT35Turbo");
        modelMapping.put("Azure OpenAI GPT 3.5 Turbo 16k", "sfdc_ai__DefaultAzureOpenAIGPT35Turbo_16k");
        modelMapping.put("Azure OpenAI GPT 4 Turbo", "sfdc_ai__DefaultAzureOpenAIGPT4Turbo");
        modelMapping.put("OpenAI Ada 002", "sfdc_ai__DefaultOpenAITextEmbeddingAda_002");
        modelMapping.put("OpenAI GPT 3.5 Turbo", "sfdc_ai__DefaultOpenAIGPT35Turbo");
        modelMapping.put("OpenAI GPT 3.5 Turbo 16k", "sfdc_ai__DefaultOpenAIGPT35Turbo_16k");
        modelMapping.put("OpenAI GPT 4", "sfdc_ai__DefaultOpenAIGPT4");
        modelMapping.put("OpenAI GPT 4 32k", "sfdc_ai__DefaultOpenAIGPT4_32k");
        modelMapping.put("OpenAI GPT 4o (Omni)", "sfdc_ai__DefaultOpenAIGPT4Omni");
        modelMapping.put("OpenAI GPT 4 Turbo", "sfdc_ai__DefaultOpenAIGPT4Turbo");
    }

    private static String getMappedValue(String input) {
        return modelMapping.getOrDefault(input, "Mapping not found");
    }


    private static String getAccessToken(String org, String consumerKey, String consumerSecret) {
    String urlString = "https://" + org + ".my.salesforce.com/services/oauth2/token";
    String params = "grant_type=client_credentials&client_id=" + consumerKey + "&client_secret=" + consumerSecret;

    try {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = params.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                // Parse JSON response and extract access_token
                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getString("access_token");
            }
        } else {
            return "Error: " + responseCode;
        }
    } catch (Exception e) {
        e.printStackTrace();
        return "Exception occurred: " + e.getMessage();
    }
    }


    private static HttpURLConnection getConnectionObject(URL url, String accessToken) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("x-sfdc-app-context", "EinsteinGPT");
        conn.setRequestProperty("x-client-feature-id", "ai-platform-models-connected-app");
        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        return conn;
    }

    private static String generateText(String accessToken, String payload, MuleChainEinstein1ParamsModelDetails paramDetails, String resource) {
        String urlString = URL_BASE + getMappedValue(paramDetails.getModelName()) + resource;
        

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = getConnectionObject(url, accessToken);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (java.io.BufferedReader br = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString();
                }
            } else {
                return "Error: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception occurred: " + e.getMessage();
        }
    }


    private static String generateEmbedding(String accessToken, String payload, MuleChainEinstein1ParamsEmbeddingDetails paramDetails, String resource) {
        String urlString = URL_BASE + getMappedValue(paramDetails.getModelName()) + resource;
        

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = getConnectionObject(url, accessToken);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (java.io.BufferedReader br = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString();
                }
            } else {
                return "Error: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception occurred: " + e.getMessage();
        }
    }

    private static String constructJsonPayload(String prompt, MuleChainEinstein1ParamsModelDetails paramDetails) {
        JSONObject localization = new JSONObject();
        localization.put("defaultLocale", paramDetails.getLocale());

        JSONArray inputLocales = new JSONArray();
        JSONObject inputLocale = new JSONObject();
        inputLocale.put("locale", paramDetails.getLocale());
        inputLocale.put("probability", paramDetails.getProbability());
        inputLocales.put(inputLocale);
        localization.put("inputLocales", inputLocales);

        JSONArray expectedLocales = new JSONArray();
        expectedLocales.put(paramDetails.getLocale());
        localization.put("expectedLocales", expectedLocales);

        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("prompt", prompt);
        jsonPayload.put("localization", localization);
        jsonPayload.put("tags", new JSONObject());

        return jsonPayload.toString();
    }


    private static String constrcutJsonMessages(String message, MuleChainEinstein1ParamsModelDetails paramsModelDetails){        
        JSONArray messages = new JSONArray(message);
        
        JSONObject locale = new JSONObject();
        locale.put("locale", paramsModelDetails.getLocale());
        locale.put("probability", paramsModelDetails.getProbability());
        
        JSONArray inputLocales = new JSONArray();
        inputLocales.put(locale);
        
        JSONArray expectedLocales = new JSONArray();
        expectedLocales.put(paramsModelDetails.getLocale());
        
        JSONObject localization = new JSONObject();
        localization.put("defaultLocale", paramsModelDetails.getLocale());
        localization.put("inputLocales", inputLocales);
        localization.put("expectedLocales", expectedLocales);
        
        JSONObject tags = new JSONObject();
        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messages", messages);
        jsonObject.put("localization", localization);
        jsonObject.put("tags", tags);
        
        return jsonObject.toString();        
    }

    private static String constructEmbeddingJSON(String text) {
        JSONArray input = new JSONArray();
        input.put("Every day, once a day, give yourself a present");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("input", input);

        return jsonObject.toString();

    }

    public static String executeGenerateText(String prompt, MuleChainEinstein1Configuration configuration, MuleChainEinstein1ParamsModelDetails paramDetails){
        String access_token = getAccessToken(configuration.getSalesforceOrg(), configuration.getClientId(), configuration.getClientSecret());
        String payload = constructJsonPayload(prompt, paramDetails);
        String response = generateText(access_token, payload, paramDetails, "/generations");
        return response;
    }

    public static String executeGenerateChat(String messages, MuleChainEinstein1Configuration configuration, MuleChainEinstein1ParamsModelDetails paramDetails){
        String access_token = getAccessToken(configuration.getSalesforceOrg(), configuration.getClientId(), configuration.getClientSecret());
        String payload = constrcutJsonMessages(messages, paramDetails);
        String response = generateText(access_token, payload, paramDetails, "/chat-generations");
        return response;
    }


    public static String executeGenerateEmbedding(String text, MuleChainEinstein1Configuration configuration, MuleChainEinstein1ParamsEmbeddingDetails paramDetails){
        String access_token = getAccessToken(configuration.getSalesforceOrg(), configuration.getClientId(), configuration.getClientSecret());
        String payload = constructEmbeddingJSON(text);
        String response = generateEmbedding(access_token, payload, paramDetails, "/embeddings");
        return response;
    }


}