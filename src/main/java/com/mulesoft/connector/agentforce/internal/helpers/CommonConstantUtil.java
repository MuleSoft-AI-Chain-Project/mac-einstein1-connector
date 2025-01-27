package com.mulesoft.connector.agentforce.internal.helpers;

import java.util.concurrent.TimeUnit;

public class CommonConstantUtil {

  private CommonConstantUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static final String HTTP_METHOD_DELETE = "DELETE";

  public static final String HTTP_METHOD_POST = "POST";

  public static final String HTTP_METHOD_GET = "GET";

  public static final String CONTENT_TYPE_STRING = "Content-Type";

  public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json;charset=utf-8";

  public static final String AUTHORIZATION = "Authorization";

  public static final Integer CONNECTION_TIMEOUT = 15;

  public static final TimeUnit CONNECTION_TIMEOUT_TIMEUNIT = TimeUnit.SECONDS;

  public static final Integer READ_TIMEOUT = 20;

  public static final TimeUnit READ_TIMEOUT_TIMEUNIT = TimeUnit.SECONDS;

}
