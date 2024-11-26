package com.mule.einstein.internal.error.provider;

import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.HashSet;
import java.util.Set;

import static com.mule.einstein.internal.error.EinsteinErrorType.*;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

public class EmbeddingErrorTypeProvider implements ErrorTypeProvider {

  @SuppressWarnings("rawtypes")
  @Override
  public Set<ErrorTypeDefinition> getErrorTypes() {
    return unmodifiableSet(new HashSet<>(asList(EMBEDDING_OPERATIONS_FAILURE, RAG_FAILURE,
                                                TOOLS_OPERATION_FAILURE)));
  }
}
