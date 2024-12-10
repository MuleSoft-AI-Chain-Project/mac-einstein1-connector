package com.mule.einstein.internal.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mule.einstein.internal.connection.EinsteinConnection;
import com.mule.einstein.internal.dto.EinsteinEmbeddingResponseDTO;
import com.mule.einstein.internal.dto.OAuthResponseDTO;
import com.mule.einstein.internal.models.ParamsEmbeddingDocumentDetails;
import com.mule.einstein.internal.models.ParamsEmbeddingModelDetails;
import com.mule.einstein.internal.models.ParamsModelDetails;
import com.mule.einstein.internal.models.RAGParamsModelDetails;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.BodyContentHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.mule.runtime.api.connection.ConnectionException;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mule.einstein.internal.helpers.ConstantUtil.*;
import static com.mule.einstein.internal.helpers.RequestHelper.executeREST;

public class PayloadHelper {

  public static String executeGenerateText(String prompt, EinsteinConnection connection, ParamsModelDetails paramDetails)
      throws IOException, ConnectionException {
    OAuthResponseDTO accessTokeDTO =
        RequestHelper.getAccessToken(connection.getSalesforceOrg(), connection.getClientId(), connection.getClientSecret());
    String payload = constructJsonPayload(prompt, paramDetails.getLocale(), paramDetails.getProbability());
    return executeEinsteinRequest(accessTokeDTO, payload, paramDetails.getModelApiName(), URI_MODELS_API_GENERATIONS);
  }

  public static String executeGenerateChat(String messages, EinsteinConnection connection, ParamsModelDetails paramDetails)
      throws IOException, ConnectionException {
    OAuthResponseDTO accessTokeDTO =
        RequestHelper.getAccessToken(connection.getSalesforceOrg(), connection.getClientId(), connection.getClientSecret());
    String payload = constrcutJsonMessages(messages, paramDetails);
    return executeEinsteinRequest(accessTokeDTO, payload, paramDetails.getModelApiName(), URI_MODELS_API_CHAT_GENERATIONS);
  }

  public static String executeGenerateEmbedding(String text, EinsteinConnection connection,
                                                ParamsEmbeddingModelDetails paramDetails)
      throws IOException, ConnectionException {
    OAuthResponseDTO accessTokeDTO =
        RequestHelper.getAccessToken(connection.getSalesforceOrg(), connection.getClientId(), connection.getClientSecret());
    String payload = constructEmbeddingJSON(text);
    return executeEinsteinRequest(accessTokeDTO, payload, paramDetails.getModelApiName(), URI_MODELS_API_EMBEDDINGS);
  }

  public static JSONArray embeddingFromFile(String filePath, EinsteinConnection connection,
                                            ParamsEmbeddingDocumentDetails einsteinParameters)
      throws IOException, SAXException, TikaException, ConnectionException {

    OAuthResponseDTO accessTokeDTO =
        RequestHelper.getAccessToken(connection.getSalesforceOrg(), connection.getClientId(), connection.getClientSecret());
    List<String> corpus = createCorpusList(filePath, einsteinParameters.getFileType(), einsteinParameters.getOptionType());

    return new JSONArray(
                         getCorpusEmbeddings(einsteinParameters.getModelApiName(), corpus, accessTokeDTO));
  }

  public static String executeRAG(String text, EinsteinConnection connection, RAGParamsModelDetails paramDetails)
      throws IOException, ConnectionException {
    OAuthResponseDTO accessTokeDTO =
        RequestHelper.getAccessToken(connection.getSalesforceOrg(), connection.getClientId(), connection.getClientSecret());
    String payload = constructJsonPayload(text, paramDetails.getLocale(), paramDetails.getProbability());
    return executeEinsteinRequest(accessTokeDTO, payload, paramDetails.getModelApiName(), URI_MODELS_API_GENERATIONS);
  }

  public static String executeTools(String originalPrompt, String prompt, String filePath, EinsteinConnection connection,
                                    ParamsModelDetails paramDetails)
      throws IOException, ConnectionException {
    OAuthResponseDTO accessTokeDTO =
        RequestHelper.getAccessToken(connection.getSalesforceOrg(), connection.getClientId(), connection.getClientSecret());
    String payload = constructJsonPayload(prompt, paramDetails.getLocale(), paramDetails.getProbability());
    String payloadOptional = constructJsonPayload(originalPrompt, paramDetails.getLocale(), paramDetails.getProbability());

    String intermediateAnswer =
        executeEinsteinRequest(accessTokeDTO, payload, paramDetails.getModelApiName(), URI_MODELS_API_GENERATIONS);

    String response =
        executeEinsteinRequest(accessTokeDTO, payloadOptional, paramDetails.getModelApiName(), URI_MODELS_API_GENERATIONS);
    List<String> findURL = extractUrls(intermediateAnswer);
    if (findURL != null) {
      JSONObject jsonObject = new JSONObject(intermediateAnswer);
      String generatedText = jsonObject.getJSONObject("generation").getString("generatedText");

      String ePayload = buildPayload(generatedText);

      response = getAttributes(findURL.get(0), filePath, extractPayload(ePayload));
      String finalPayload = constructJsonPayload("data: " + response + ", question: " + originalPrompt, paramDetails.getLocale(),
                                                 paramDetails.getProbability());
      response = executeEinsteinRequest(accessTokeDTO, finalPayload, paramDetails.getModelApiName(), URI_MODELS_API_GENERATIONS);

    }
    return response;
  }

  private static String executeEinsteinRequest(OAuthResponseDTO accessTokenDTO, String payload, String modelName,
                                               String resource) {
    String urlString = accessTokenDTO.getApiInstanceUrl() + URI_MODELS_API + modelName + resource;
    return executeREST(accessTokenDTO.getAccessToken(), payload, urlString);
  }

  public static JSONArray embeddingFileQuery(String prompt, String filePath, EinsteinConnection connection, String modelName,
                                             String fileType, String optionType)
      throws IOException, SAXException, TikaException, ConnectionException {

    OAuthResponseDTO accessTokeDTO =
        RequestHelper.getAccessToken(connection.getSalesforceOrg(), connection.getClientId(), connection.getClientSecret());

    List<String> corpus = createCorpusList(filePath, fileType, optionType);
    String body = constructEmbeddingJSON(prompt);

    List<BigDecimal> embeddingList = getQueryEmbedding(accessTokeDTO, body, modelName);

    List<List<BigDecimal>> corpusEmbeddingList = getCorpusEmbeddings(modelName, corpus, accessTokeDTO);

    // Compare embeddings and rank results
    List<BigDecimal> similarityScores = new ArrayList<>();
    corpusEmbeddingList
        .forEach(corpusEmbedding -> similarityScores.add(
                                                         calculateCosineSimilarity(embeddingList, corpusEmbedding)));
    // Rank and print results
    List<String> results = rankAndPrintResults(corpus, similarityScores);

    // Convert results list to a JSONArray
    return new JSONArray(results);
  }

  private static List<List<BigDecimal>> getCorpusEmbeddings(String modelName, List<String> corpus, OAuthResponseDTO accessTokeDTO)
      throws JsonProcessingException {

    String embeddingResponse;
    String corpusBody;
    // Generate embeddings for the corpus
    List<List<BigDecimal>> corpusEmbeddings = new ArrayList<>();

    for (String text : corpus) {

      corpusBody = constructEmbeddingJSON(text);

      if (text != null && !text.isEmpty()) {
        embeddingResponse =
            executeEinsteinRequest(accessTokeDTO, constructEmbeddingJSON(corpusBody), modelName, URI_MODELS_API_EMBEDDINGS);

        EinsteinEmbeddingResponseDTO embeddingResponseDTO =
            new ObjectMapper().readValue(embeddingResponse, EinsteinEmbeddingResponseDTO.class);

        corpusEmbeddings.add(embeddingResponseDTO.getEmbeddings().get(0).getEmbedding());
      }
    }
    return corpusEmbeddings;
  }

  private static List<BigDecimal> getQueryEmbedding(OAuthResponseDTO accessTokeDTO, String body, String modelName)
      throws JsonProcessingException {

    String embeddingResponse = executeEinsteinRequest(accessTokeDTO, body, modelName, URI_MODELS_API_EMBEDDINGS);

    EinsteinEmbeddingResponseDTO embeddingResponseDTO =
        new ObjectMapper().readValue(embeddingResponse, EinsteinEmbeddingResponseDTO.class);

    return embeddingResponseDTO.getEmbeddings().get(0).getEmbedding();
  }

  private static List<String> createCorpusList(String filePath, String fileType, String splitOption)
      throws IOException, SAXException, TikaException {
    List<String> corpus;
    if (splitOption.equals("FULL")) {
      corpus = Arrays.asList(splitFullDocument(filePath, fileType));
    } else {
      corpus = Arrays.asList(splitByType(filePath, fileType, splitOption));
    }
    return corpus;
  }

  private static BigDecimal calculateCosineSimilarity(List<BigDecimal> embeddingList, List<BigDecimal> corpusEmbedding) {
    BigDecimal dotProduct = BigDecimal.ZERO;
    BigDecimal normA = BigDecimal.ZERO;
    BigDecimal normB = BigDecimal.ZERO;
    for (int i = 0; i < embeddingList.size(); i++) {
      BigDecimal a = embeddingList.get(i);
      BigDecimal b = corpusEmbedding.get(i);
      dotProduct = dotProduct.add(a.multiply(b));
      normA = normA.add(a.pow(2));
      normB = normB.add(b.pow(2));
    }
    return dotProduct.divide(
                             BigDecimal.valueOf(Math.sqrt(normA.doubleValue()) * Math.sqrt(normB.doubleValue())),
                             RoundingMode.HALF_UP);
  }



  private static List<String> rankAndPrintResults(List<String> corpus, List<BigDecimal> similarityScores) {

    if (!similarityScores.isEmpty() && !corpus.isEmpty()) {

      List<Integer> indices = IntStream
          .range(0, corpus.size())
          .boxed()
          .sorted((i, j) -> similarityScores.get(j)
              .compareTo(similarityScores.get(i)))
          .collect(Collectors.toList());

      return indices.stream()
          .map(index -> similarityScores.get(index) + " - " + corpus.get(index))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private static String getContentFromUrl(String urlString) throws IOException, SAXException, TikaException {
    BodyContentHandler handler = new BodyContentHandler(-1);
    Metadata metadata = new Metadata();
    InputStream inputstream = new URL(urlString).openStream();
    ParseContext pcontext = new ParseContext();

    Parser parser = new AutoDetectParser();
    parser.parse(inputstream, handler, metadata, pcontext);
    return handler.toString();
  }

  private static String getContentFromFile(String filePath) throws IOException, SAXException, TikaException {
    BodyContentHandler handler = new BodyContentHandler(-1);
    Metadata metadata = new Metadata();
    FileInputStream inputstream = new FileInputStream(filePath);
    ParseContext pcontext = new ParseContext();

    AutoDetectParser parser = new AutoDetectParser();
    parser.parse(inputstream, handler, metadata, pcontext);
    return handler.toString();
  }

  private static String getContentFromTxtFile(String filePath) throws IOException, SAXException, TikaException {
    BodyContentHandler handler = new BodyContentHandler(-1);
    Metadata metadata = new Metadata();
    FileInputStream inputstream = new FileInputStream(filePath);
    ParseContext pcontext = new ParseContext();

    TXTParser parser = new TXTParser();
    parser.parse(inputstream, handler, metadata, pcontext);
    return handler.toString();
  }

  private static String getFileTypeContextFromFile(String filePath, String fileType)
      throws IOException, SAXException, TikaException {

    switch (fileType) {
      case "URL":
        return getContentFromUrl(filePath);
      case "PDF":
        return getContentFromFile(filePath);
      default:
        return getContentFromTxtFile(filePath);
    }
  }

  private static String splitFullDocument(String filePath, String fileType) throws IOException, SAXException, TikaException {
    return getFileTypeContextFromFile(filePath, fileType);
  }

  private static String[] splitByType(String filePath, String fileType, String splitOption)
      throws IOException, SAXException, TikaException {
    String content = getFileTypeContextFromFile(filePath, fileType);
    return splitContent(content, splitOption);
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

  private static List<String> extractUrls(String input) {
    // Define the URL pattern
    String urlPattern = "(https?://[\\w\\-\\.]+(?:\\.[\\w\\-]+)+(?:[\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?)";

    // Compile the pattern
    Pattern pattern = Pattern.compile(urlPattern);

    // Create a matcher from the input string
    Matcher matcher = pattern.matcher(input);

    // Find and collect all matches
    List<String> urls = new ArrayList<>();
    while (matcher.find()) {
      // Group 1 contains the URL without trailing whitespace
      urls.add(matcher.group(1));
    }

    // Return null if no URLs are found
    return urls.isEmpty() ? null : urls;
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

  private static String constrcutJsonMessages(String message, ParamsModelDetails paramsModelDetails) {
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

  private static String getAttributes(String url, String filePath, String payload) throws IOException {

    try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {

      JSONTokener tokener = new JSONTokener(inputStream);
      JSONArray rootArray = new JSONArray(tokener);
      String responseString = "";

      for (int i = 0; i < rootArray.length(); i++) {

        JSONObject node = rootArray.getJSONObject(i);

        if (node.getString("url").trim().equals(url)) {
          String method = node.getString("method");
          String headers = node.getString("headers");

          URL urlObj = new URL(url);
          HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

          conn.setRequestMethod(method);
          if (headers != null && !headers.isEmpty()) {
            conn.setRequestProperty("Authorization", headers);
          }
          conn.setRequestProperty(ConstantUtil.CONTENT_TYPE_STRING, "application/json; charset=UTF-8");
          conn.setDoOutput(true);

          if (method.equals("POST")) {
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
              wr.writeBytes(payload);
              wr.flush();
            }
          }
          int responseCode = conn.getResponseCode();

          BufferedReader in;
          if (responseCode >= 200 && responseCode < 300) {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          } else {
            in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
          }

          String inputLine;
          StringBuilder response = new StringBuilder();

          while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
          }
          in.close();

          responseString = response.toString();
          break;
        }
      }
      return responseString;
    }
  }

  private static String extractPayload(String payload) {

    Pattern pattern = Pattern.compile("\\{.*\\}");
    Matcher matcher = pattern.matcher(payload);
    String response;
    if (matcher.find()) {
      response = matcher.group();
    } else {
      response = "Payload not found!";
    }
    return response;
  }

  private static String buildPayload(String payload) {

    String findPayload = extractPayload(payload);
    if (findPayload.equals("Payload not found!")) {
      return extractPayload(payload);
    }
    return findPayload;
  }
}
