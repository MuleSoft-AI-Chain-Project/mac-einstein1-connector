package com.mulesoft.connector.einstein.internal.modelsapi.models;

import com.mulesoft.connector.einstein.internal.modelsapi.models.provider.ModelApiNameProvider;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.values.OfValues;

import static com.mulesoft.connector.einstein.internal.modelsapi.helpers.ConstantUtil.MODELAPI_OPENAI_GPT_3_5_TURBO;

public class ParamsModelDetails {

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @OfValues(ModelApiNameProvider.class)
  @Optional(defaultValue = MODELAPI_OPENAI_GPT_3_5_TURBO)
  @DisplayName("Model API Name")
  private String modelApiName;

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

  public String getModelApiName() {
    return modelApiName;
  }

  public Number getProbability() {
    return probability;
  }

  public String getLocale() {
    return locale;
  }
}
