package com.mulesoft.connector.agentforce.internal.botapi.helpers;

public class BotConstantUtil {

  private BotConstantUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static final String URI_BOT_API_METADATA_SERVICES_V_62 =
      "/services/data/v62.0";
  public static final String URI_BOT_API_METADATA_AGENTLIST =
      "/query?q=select%20Status%2CBotDefinitionId%2CBotDefinition.MasterLabel%20from%20BotVersion";
  public static final String V6_URI_BOT_API_BOTS = "/einstein/ai-agent/v1";
  public static final String URI_BOT_API_SESSIONS = "/sessions/";
  public static final String URI_BOT_API_AGENTS = "/agents/";
  public static final String URI_BOT_API_MESSAGES = "/messages";
  public static final String X_SESSION_END_REASON = "x-session-end-reason";
  public static final String END_SESSION_REASON_USERREQUEST = "UserRequest";
  public static final String CONTINUE_SESSION_MESSAGE_TYPE_TEXT = "Text";
  public static final String SESSION_ID = "sessionId";
  public static final String MESSAGES = "messages";
  public static final String MESSAGE = "message";

}
