package com.mule.mulechain.internal.helpers;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.tika.parser.Parser;
import java.io.InputStream;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mule.mulechain.internal.MuleChainEinstein1Configuration;
import com.mule.mulechain.internal.helpers.documents.MuleChainEinstein1ParametersEmbeddingDocument;
import com.mule.mulechain.internal.models.MuleChainEinstein1ParamsEmbeddingDetails;
import com.mule.mulechain.internal.models.MuleChainEinstein1ParamsModelDetails;
import com.mule.mulechain.internal.models.MuleChainEinstein1RAGParamsModelDetails;

import java.io.OutputStream;

public class MuleChainEinstein1PayloadHelper {

    private static final Map<String, String> modelMapping = new HashMap<>();
    private static final String URL_BASE = "https://api.salesforce.com/einstein/platform/v1/models/";

    static {
        modelMapping.put("Anthropic Claude 3 Haiku on Amazon", "sfdc_ai__DefaultBedrockAnthropicClaude3Haiku");
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


    public static String getAccessToken(String org, String consumerKey, String consumerSecret) {
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


    private static String executeREST(String accessToken, String payload, String urlString) {

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


    private static String generateText(String accessToken, String payload, String modelName, String resource) {
        String urlString = URL_BASE + getMappedValue(modelName) + resource;
        System.out.println(urlString);
        return executeREST(accessToken, payload, urlString);
    }


    private static String generateEmbedding(String accessToken, String payload, String modelName, String resource) {
        String urlString = URL_BASE + getMappedValue(modelName) + resource;
        System.out.println(urlString);

        return executeREST(accessToken, payload, urlString);
    }




    public static String EmbeddingFromFile(String filePath, MuleChainEinstein1Configuration configuration, MuleChainEinstein1ParametersEmbeddingDocument MuleChainEinsteinParameters) throws IOException, SAXException, TikaException {

        String access_token = getAccessToken(configuration.getSalesforceOrg(), configuration.getClientId(), configuration.getClientSecret());
        /* 
        List<String> corpus;
        if (MuleChainEinsteinParameters.getOptionType().equals("FULL")) {
            corpus = Arrays.asList(splitFullDocument(filePath,MuleChainEinsteinParameters.getFileType()));
        } else {
            corpus = Arrays.asList(splitByType(filePath,MuleChainEinsteinParameters.getFileType(), MuleChainEinsteinParameters.getOptionType()));
        }
        */
        List<String> corpus = createCorpusList(filePath, MuleChainEinsteinParameters.getFileType(), MuleChainEinsteinParameters.getOptionType());

        try {


            //JSONObject queryResponse = generateEmbedding(modelId, body, configuration, region);
            String response = "";
            JSONObject jsonObject;
            //Generate embedding for query
            JSONArray embeddingsArray;

            String corpusBody=null;
            // Generate embeddings for the corpus
            List<JSONArray> corpusEmbeddings = new ArrayList<>();

            for (String text : corpus) {
                corpusBody = constructEmbeddingJSON(text); 
                //System.out.println(corpusBody);
                if (text != null && !text.isEmpty()) {
                    response = generateEmbedding(access_token, constructEmbeddingJSON(corpusBody), MuleChainEinsteinParameters.getModelName(), "/embeddings");
                    jsonObject = new JSONObject(response);
                    embeddingsArray = jsonObject.getJSONArray("embeddings");
                    corpusEmbeddings.add(embeddingsArray.getJSONObject(0).getJSONArray("embedding"));
                }
            }


            // Convert results list to a JSONArray
            JSONArray jsonArray = new JSONArray(corpusEmbeddings);

            return jsonArray.toString();
       } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return null;

        }
    }

    private static String constructJsonPayload(String prompt, String locale, Number probability) {
        JSONObject localization = new JSONObject();
        localization.put("defaultLocale", locale);

        JSONArray inputLocales = new JSONArray();
        JSONObject inputLocale = new JSONObject();
        inputLocale.put("locale", locale);
        inputLocale.put("probability", probability);
        inputLocales.put(inputLocale);
        localization.put("inputLocales", inputLocales);

        JSONArray expectedLocales = new JSONArray();
        expectedLocales.put(locale);
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
        input.put(text);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("input", input);

        return jsonObject.toString();

    }

    public static String executeGenerateText(String prompt, MuleChainEinstein1Configuration configuration, MuleChainEinstein1ParamsModelDetails paramDetails){
        String access_token = getAccessToken(configuration.getSalesforceOrg(), configuration.getClientId(), configuration.getClientSecret());
        String payload = constructJsonPayload(prompt, paramDetails.getLocale(), paramDetails.getProbability());
        String response = generateText(access_token, payload, paramDetails.getModelName(), "/generations");
        return response;
    }

    public static String executeGenerateChat(String messages, MuleChainEinstein1Configuration configuration, MuleChainEinstein1ParamsModelDetails paramDetails){
        String access_token = getAccessToken(configuration.getSalesforceOrg(), configuration.getClientId(), configuration.getClientSecret());
        String payload = constrcutJsonMessages(messages, paramDetails);
        String response = generateText(access_token, payload, paramDetails.getModelName(), "/chat-generations");
        return response;
    }


    public static String executeGenerateEmbedding(String text, MuleChainEinstein1Configuration configuration, MuleChainEinstein1ParamsEmbeddingDetails paramDetails){
        String access_token = getAccessToken(configuration.getSalesforceOrg(), configuration.getClientId(), configuration.getClientSecret());
        String payload = constructEmbeddingJSON(text);
        String response = generateEmbedding(access_token, payload, paramDetails.getModelName(), "/embeddings");
        return response;
    }

    public static String executeRAG(String text, MuleChainEinstein1Configuration configuration, MuleChainEinstein1RAGParamsModelDetails paramDetails){
        String access_token = getAccessToken(configuration.getSalesforceOrg(), configuration.getClientId(), configuration.getClientSecret());
        String payload = constructJsonPayload(text, paramDetails.getLocale(), paramDetails.getProbability());
        String response = generateText(access_token, payload, paramDetails.getModelName(), "/generations");
        return response;
    }



    private static List<String> createCorpusList(String filePath, String fileType, String splitOption) throws IOException, SAXException, TikaException {
        List<String> corpus;
        //System.out.println(splitOption);
        if (splitOption.equals("FULL")) {
            corpus = Arrays.asList(splitFullDocument(filePath,fileType));
        } else {
            corpus = Arrays.asList(splitByType(filePath,fileType, splitOption));
        }
        return corpus;
    }

    public static String EmbeddingFileQuery(String prompt, String filePath, MuleChainEinstein1Configuration configuration, String modelName, String fileType, String optionType) throws IOException, SAXException, TikaException {

        String access_token = getAccessToken(configuration.getSalesforceOrg(), configuration.getClientId(), configuration.getClientSecret());

        /* 
        List<String> corpus;
        if (MuleChainEinsteinParameters.getOptionType().equals("FULL")) {
            corpus = Arrays.asList(splitFullDocument(filePath,MuleChainEinsteinParameters));
        } else {
            corpus = Arrays.asList(splitByType(filePath,MuleChainEinsteinParameters));
        }
        */

        List<String> corpus = createCorpusList(filePath, fileType, optionType);

        String body = constructEmbeddingJSON(prompt);


        try {


            //JSONObject queryResponse = generateEmbedding(modelId, body, configuration, region);
            String response = generateEmbedding(access_token, body, modelName, "/embeddings");
            JSONObject jsonObject = new JSONObject(response);
            //Generate embedding for query
            JSONArray embeddingsArray = jsonObject.getJSONArray("embeddings");

            // Extract the first embedding object
            JSONObject firstEmbeddingObject = embeddingsArray.getJSONObject(0);

            // Extract the embedding array from the first embedding object
            JSONArray queryEmbedding = firstEmbeddingObject.getJSONArray("embedding");

            String corpusBody=null;
            // Generate embeddings for the corpus
            List<JSONArray> corpusEmbeddings = new ArrayList<>();

            for (String text : corpus) {
                corpusBody = constructEmbeddingJSON(text); 
                //System.out.println(corpusBody);
                if (text != null && !text.isEmpty()) {
                    response = generateEmbedding(access_token, constructEmbeddingJSON(corpusBody), modelName, "/embeddings");
                    jsonObject = new JSONObject(response);
                    embeddingsArray = jsonObject.getJSONArray("embeddings");
                    corpusEmbeddings.add(embeddingsArray.getJSONObject(0).getJSONArray("embedding"));
                }
            }

            // Compare embeddings and rank results
            List<Double> similarityScores = new ArrayList<>();
            for (JSONArray corpusEmbedding : corpusEmbeddings) {
                similarityScores.add(calculateCosineSimilarity(queryEmbedding, corpusEmbedding));
            }

            // Rank and print results
            List<String> results = rankAndPrintResults(corpus, similarityScores);

            // Convert results list to a JSONArray
            JSONArray jsonArray = new JSONArray(results);

            return jsonArray.toString();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return null;

        }
    }



    private static double calculateCosineSimilarity(JSONArray vec1, JSONArray vec2) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vec1.length(); i++) {
            double a = vec1.getDouble(i);
            double b = vec2.getDouble(i);
            dotProduct += a * b;
            normA += Math.pow(a, 2);
            normB += Math.pow(b, 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private static List<String> rankAndPrintResults(List<String> corpus, List<Double> similarityScores) {
        List<Integer> indices = new ArrayList<>();
        System.out.println(corpus.size());
        for (int i = 0; i < corpus.size(); i++) {
            indices.add(i);
        }

        indices.sort((i, j) -> Double.compare(similarityScores.get(j), similarityScores.get(i)));

        System.out.println("Ranked results:");
        List<String> results = new ArrayList<>();
        for (int index : indices) {
            System.out.println("Score: " + similarityScores.get(index) + " - Text: " + corpus.get(index));
            results.add(similarityScores.get(index) + " - " + corpus.get(index));
        }

         
        return results;
    }

    /* 
    private static String getContentFromFile(String filePath) throws IOException, SAXException, TikaException {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(new File(filePath));
        ParseContext pcontext = new ParseContext();

        PDFParser pdfparser = new PDFParser();
        pdfparser.parse(inputstream, handler, metadata, pcontext);
        return handler.toString();
    }    
    */

    private static String getContentFromUrl(String urlString) throws IOException, SAXException, TikaException {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        InputStream inputstream = new URL(urlString).openStream();
        ParseContext pcontext = new ParseContext();
    
        Parser parser = new AutoDetectParser();
        parser.parse(inputstream, handler, metadata, pcontext);
        return handler.toString();
    }

    private static String getContentFromFile(String filePath) throws IOException, SAXException, TikaException {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(new File(filePath));
        ParseContext pcontext = new ParseContext();
    
        AutoDetectParser parser = new AutoDetectParser();
        parser.parse(inputstream, handler, metadata, pcontext);
        return handler.toString();
    }

    private static String getFileTypeContextFromFile(String filePath, String fileType) throws IOException, SAXException, TikaException {
        if (fileType.equals("URL")) {
            return getContentFromUrl(filePath);
        } else {
            return getContentFromFile(filePath);
        }
    }

    private static String splitFullDocument(String filePath, String fileType) throws IOException, SAXException, TikaException {
        String content = getFileTypeContextFromFile(filePath, fileType);
        return content;
    }


    private static String[] splitByType(String filePath, String fileType, String splitOption) throws IOException, SAXException, TikaException {
        String content = getFileTypeContextFromFile(filePath, fileType);
        String[] parts = splitContent(content, splitOption);
        return parts;
    }



    private static String[] splitContent(String text, String option) {
        switch (option) {
            case "PARAGRAPH":
                return splitByParagraphs(text);
            case "SENTENCES":
                return splitBySentences(text);
            default:
                throw new IllegalArgumentException("Unknown split option: " + option);
        }
      }
      
      private static String[] splitByParagraphs(String text) {
        // Assuming paragraphs are separated by two or more newlines
       
        return removeEmptyStrings(text.split("\\r?\\n\\r?\\n"));
      }
      
      private static String[] splitBySentences(String text) {
        // Split by sentences (simple implementation using period followed by space)
        return removeEmptyStrings(text.split("(?<!Mr|Mrs|Ms|Dr|Sr|Jr|Prof)\\.\\s+"));
      }


      public static String[] removeEmptyStrings(String[] array) {
        // Convert array to list
        List<String> list = new ArrayList<>(Arrays.asList(array));

        // Remove empty strings from the list
        list.removeIf(String::isEmpty);

        // Convert list back to array
        return list.toArray(new String[0]);
    }
     
      

}