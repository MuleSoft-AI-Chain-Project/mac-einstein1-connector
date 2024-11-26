package com.mule.einstein.internal.error;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

public enum EinsteinErrorType implements ErrorTypeDefinition<EinsteinErrorType> {
  CHAT_FAILURE, EMBEDDING_OPERATIONS_FAILURE, RAG_FAILURE, TOOLS_OPERATION_FAILURE
}
