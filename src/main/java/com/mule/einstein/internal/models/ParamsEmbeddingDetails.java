package com.mule.einstein.internal.models;

import com.mule.einstein.internal.models.provider.EmbeddingModelApiNameProvider;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.values.OfValues;

import static com.mule.einstein.internal.helpers.ConstantUtil.OPENAI_ADA_002;

public class ParamsEmbeddingDetails {

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @OfValues(EmbeddingModelApiNameProvider.class)
  @Optional(defaultValue = OPENAI_ADA_002)
  private String modelName;

  public String getModelName() {
    return modelName;
  }
}
