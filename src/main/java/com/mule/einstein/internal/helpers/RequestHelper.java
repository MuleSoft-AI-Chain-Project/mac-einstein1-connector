package com.mule.einstein.internal.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.mule.einstein.internal.helpers.ConstantUtil.GRANT_TYPE_CLIENT_CREDENTIALS;
import static com.mule.einstein.internal.helpers.ConstantUtil.QUERY_PARAM_CLIENT_ID;
import static com.mule.einstein.internal.helpers.ConstantUtil.QUERY_PARAM_CLIENT_SECRET;
import static com.mule.einstein.internal.helpers.ConstantUtil.QUERY_PARAM_GRANT_TYPE;
import static com.mule.einstein.internal.helpers.ConstantUtil.URI_HTTPS_PREFIX;
import static com.mule.einstein.internal.helpers.ConstantUtil.URI_OAUTH_TOKEN;

public class RequestHelper {

    private static final Logger log = LoggerFactory.getLogger(RequestHelper.class);

    public static String getOAuthURL(String salesforceOrg)
    {
        return URI_HTTPS_PREFIX + salesforceOrg + URI_OAUTH_TOKEN;
    }

    public static String getOAuthParams(String clientId,String clientSecret)
    {
        return QUERY_PARAM_GRANT_TYPE +"="+GRANT_TYPE_CLIENT_CREDENTIALS
                + "&"+QUERY_PARAM_CLIENT_ID+"=" + clientId
                + "&"+QUERY_PARAM_CLIENT_SECRET+"=" + clientSecret;
    }

    public static String executeREST(String accessToken, String payload, String urlString) {

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
            log.error("Exception during REST request ",e);
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
        conn.setRequestProperty(ConstantUtil.CONTENT_TYPE_STRING, "application/json;charset=utf-8");
        return conn;
    }

}
