package com.mule.einstein.internal.models;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.values.OfValues;

import static com.mule.einstein.internal.helpers.ConstantUtil.OPENAI_GPT_3_5_TURBO;

public class ParamsModelDetails {

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional(defaultValue = OPENAI_GPT_3_5_TURBO)
  private String modelName;

  public String getModelName() {
    return modelName;
  }

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional(defaultValue = "0.8")
  private Number probability;

  public Number getProbability() {
    return probability;
  }

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional(defaultValue = "en_US")
  private String locale;

  public String getLocale() {
    return locale;
  }



}
