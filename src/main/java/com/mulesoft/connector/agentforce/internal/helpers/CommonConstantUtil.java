package com.mulesoft.connector.agentforce.internal.helpers;

public class CommonConstantUtil {

  public static final String URI_OAUTH_TOKEN = "/services/oauth2/token";
  
  public static final String HTTP_METHOD_DELETE = "DELETE";

  public static final String HTTP_METHOD_POST = "POST";

  public static final String HTTP_METHOD_GET = "GET";

  public static final String CONTENT_TYPE_STRING = "Content-Type";

  public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json;charset=utf-8";

  public static final String QUERY_PARAM_GRANT_TYPE = "grant_type";

  public static final String QUERY_PARAM_CLIENT_ID = "client_id";

  public static final String QUERY_PARAM_CLIENT_SECRET = "client_secret";

  public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

  public static final String AUTHORIZATION = "Authorization";

  public static final Integer CONNECTION_TIMEOUT = 15000;

  public static final Integer READ_TIMEOUT = 20000;
}
