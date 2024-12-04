package com.mule.einstein.internal.models;

import com.mule.einstein.internal.models.provider.ModelApiNameProvider;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.values.OfValues;

import static com.mule.einstein.internal.helpers.ConstantUtil.OPENAI_GPT_3_5_TURBO;

public class ParamsModelDetails {

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @OfValues(ModelApiNameProvider.class)
  @Optional(defaultValue = OPENAI_GPT_3_5_TURBO)
  @DisplayName("Model Name")
  private String modelName;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional(defaultValue = "0.8")
  @DisplayName("Probability")
  private Number probability;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional(defaultValue = "en_US")
  @DisplayName("Locale")
  private String locale;

  public String getModelName() {
    return modelName;
  }

  public Number getProbability() {
    return probability;
  }

  public String getLocale() {
    return locale;
  }
}
