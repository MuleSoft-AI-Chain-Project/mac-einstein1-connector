package com.mule.einstein.internal.helpers;

import static com.mule.einstein.internal.helpers.ConstantUtil.*;
import static com.mule.einstein.internal.helpers.ConstantUtil.QUERY_PARAM_CLIENT_SECRET;

public class RequestHelper {

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
}
