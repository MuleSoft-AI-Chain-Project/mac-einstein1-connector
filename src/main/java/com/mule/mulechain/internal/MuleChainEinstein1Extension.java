package com.mule.mulechain.internal;

import com.mule.mulechain.internal.config.MuleChainEinstein1Configuration;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.sdk.api.annotation.JavaVersionSupport;

import static org.mule.sdk.api.meta.JavaVersion.*;

/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "ms-einstein1")
@Extension(name = "Mulesoft Einstein AI")
@Configurations({MuleChainEinstein1Configuration.class})
@JavaVersionSupport({JAVA_8, JAVA_11, JAVA_17})
public class MuleChainEinstein1Extension {

}
