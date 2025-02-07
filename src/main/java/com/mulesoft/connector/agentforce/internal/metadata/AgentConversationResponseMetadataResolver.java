package com.mulesoft.connector.agentforce.internal.metadata;

import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;

public class AgentConversationResponseMetadataResolver implements OutputTypeResolver<String> {

  @Override
  public MetadataType getOutputType(MetadataContext context, String key) {

    return context.getTypeBuilder()
        .stringType()
        .build();
  }

  @Override
  public String getCategoryName() {
    return "AgentConversationResponseResolver";
  }
}
