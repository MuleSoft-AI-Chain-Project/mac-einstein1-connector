package com.mule.einstein.internal;

import com.mule.einstein.internal.config.ConnectorConfiguration;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.sdk.api.annotation.JavaVersionSupport;

import static org.mule.sdk.api.meta.JavaVersion.*;

/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "ms-einstein")
@Extension(name = "Einstein AI")
@Configurations({ConnectorConfiguration.class})
@JavaVersionSupport({JAVA_8, JAVA_11, JAVA_17})
public class EinsteinConnector {

}
