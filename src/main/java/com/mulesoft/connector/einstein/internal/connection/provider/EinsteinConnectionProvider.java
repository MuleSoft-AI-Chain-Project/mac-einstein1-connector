package com.mulesoft.connector.einstein.internal.connection.provider;

import com.mulesoft.connector.einstein.internal.connection.EinsteinConnection;
import com.mulesoft.connectors.commons.template.connection.provider.ConnectorConnectionProvider;

//In future if we are adding new connection provider, we will make this abstract class and common parameters of
// connection providers will go here
public interface EinsteinConnectionProvider extends ConnectorConnectionProvider<EinsteinConnection> {
}
