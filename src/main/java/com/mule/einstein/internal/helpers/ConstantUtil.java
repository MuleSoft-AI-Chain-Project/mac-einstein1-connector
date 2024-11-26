package com.mule.einstein.internal.helpers;

public class ConstantUtil {

  public static final String URL_BASE = "https://api.salesforce.com/einstein/platform/v1/models/";

  public static final String URI_HTTPS_PREFIX = "https://";
  public static final String URI_OAUTH_TOKEN = "/services/oauth2/token";
  public static final String URI_MODELS_API_EMBEDDINGS = "/embeddings";
  public static final String URI_MODELS_API_GENERATIONS = "/generations";
  public static final String URI_MODELS_API_CHAT_GENERATIONS = "/chat-generations";
  public static final String HTTP_METHOD_POST = "POST";
  public static final String CONTENT_TYPE_STRING = "Content-Type";
  public static final String QUERY_PARAM_GRANT_TYPE = "grant_type";
  public static final String QUERY_PARAM_CLIENT_ID = "client_id";
  public static final String QUERY_PARAM_CLIENT_SECRET = "client_secret";
  public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
  public static final String OPENAI_ADA_002 = "sfdc_ai__DefaultOpenAITextEmbeddingAda_002";
  public static final String OPENAI_GPT_3_5_TURBO = "sfdc_ai__DefaultOpenAIGPT35Turbo";



}
