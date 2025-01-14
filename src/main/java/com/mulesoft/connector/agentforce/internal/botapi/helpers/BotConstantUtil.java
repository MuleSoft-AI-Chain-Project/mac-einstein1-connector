package com.mulesoft.connector.agentforce.internal.botapi.helpers;

public class BotConstantUtil {

  public static final String URI_BOT_API_METADATA =
      "/services/data/v62.0/query?q=select%20Status%2CBotDefinitionId%2CBotDefinition.MasterLabel%20from%20BotVersion";

  public static final String X_SESSION_END_REASON = "x-session-end-reason";
  public static final String X_ORG_ID = "X-Org-Id";
  public static final String SESSION_END_REASON_USERREQUEST = "UserRequest";

}
