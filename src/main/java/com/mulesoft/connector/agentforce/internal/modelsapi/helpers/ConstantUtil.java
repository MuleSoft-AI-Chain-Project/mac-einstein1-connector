package com.mulesoft.connector.agentforce.internal.modelsapi.helpers;

public class ConstantUtil {

  private ConstantUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static final String URI_MODELS_API = "/einstein/platform/v1/models/";
  public static final String URI_MODELS_API_EMBEDDINGS = "/embeddings";
  public static final String URI_MODELS_API_GENERATIONS = "/generations";
  public static final String URI_MODELS_API_CHAT_GENERATIONS = "/chat-generations";

  public static final String X_SFDC_APP_CONTEXT = "x-sfdc-app-context";
  public static final String X_CLIENT_FEATURE_ID = "x-client-feature-id";
  public static final String AI_PLATFORM_MODELS_CONNECTED_APP = "ai-platform-models-connected-app";
  public static final String EINSTEIN_GPT = "EinsteinGPT";

  public static final String MODELAPI_OPENAI_ADA_002 = "sfdc_ai__DefaultOpenAITextEmbeddingAda_002";
  public static final String MODELAPI_OPENAI_GPT_3_5_TURBO = "sfdc_ai__DefaultOpenAIGPT35Turbo";
}
