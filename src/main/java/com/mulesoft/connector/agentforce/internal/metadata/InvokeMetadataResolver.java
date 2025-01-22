package com.mulesoft.connector.agentforce.internal.metadata;

import com.mulesoft.connector.agentforce.internal.models.CopilotAgentDetails;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.InputTypeResolver;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;

public class InvokeMetadataResolver
    implements InputTypeResolver<CopilotAgentDetails>, OutputTypeResolver<CopilotAgentDetails> {

  @Override
  public String getResolverName() {
    return InputTypeResolver.super.getResolverName();
  }

  @Override
  public MetadataType getOutputType(MetadataContext context, CopilotAgentDetails key)
      throws MetadataResolvingException, ConnectionException {
    key.setAgent("Testing");
    return null;
  }

  @Override
  public MetadataType getInputMetadata(MetadataContext context, CopilotAgentDetails key)
      throws MetadataResolvingException, ConnectionException {
    key.setAgent("Testing");
    return null;
  }

  @Override
  public String getCategoryName() {
    return null;
  }
}
