package com.mulesoft.connector.einstein.internal.extension;

import com.mulesoft.connector.einstein.internal.connection.provider.CustomOauthClientCredentialsConnectionProvider;
import com.mulesoft.connector.einstein.internal.operation.EinsteinGenerationOperations;
import com.mulesoft.connector.einstein.internal.operation.EinsteinEmbeddingOperations;
import com.mulesoft.connector.einstein.internal.error.AgentforceErrorType;
import org.mule.runtime.api.meta.Category;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.runtime.extension.api.annotation.license.RequiresEnterpriseLicense;
import org.mule.sdk.api.annotation.JavaVersionSupport;

import static org.mule.sdk.api.meta.JavaVersion.JAVA_11;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_17;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_8;

/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations and
 * sources are going to be declared.
 */
@Xml(prefix = "ms-einstein")
@Extension(name = "Einstein", category = Category.SELECT)
@ErrorTypes(AgentforceErrorType.class)
@Operations({EinsteinEmbeddingOperations.class,
    EinsteinGenerationOperations.class})
@ConnectionProviders(CustomOauthClientCredentialsConnectionProvider.class)
@RequiresEnterpriseLicense(allowEvaluationLicense = true)
@JavaVersionSupport({JAVA_8, JAVA_11, JAVA_17})
public class EinsteinConnector {

}
