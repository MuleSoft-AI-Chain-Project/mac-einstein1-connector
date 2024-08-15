package com.mule.mulechain.internal;

import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.sdk.api.annotation.JavaVersionSupport;

import com.mule.mulechain.internal.operations.MuleChainEinstein1Operations;

import static org.mule.sdk.api.meta.JavaVersion.JAVA_11;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_17;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_8;

/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "mac-einstein")
@Extension(name = "MAC Einstein")
@ConnectionProviders(MuleChainEinstein1ConnectionProvider.class)
@Operations(MuleChainEinstein1Operations.class)
@JavaVersionSupport({JAVA_8, JAVA_11, JAVA_17})
public class MuleChainEinstein1Extension {

}
