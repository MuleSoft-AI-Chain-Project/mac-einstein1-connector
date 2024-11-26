package com.mule.einstein.internal.models;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.values.OfValues;

import com.mule.einstein.internal.helpers.documents.DocumentFileType;
import com.mule.einstein.internal.helpers.documents.DocumentSplitOptions;

import static com.mule.einstein.internal.helpers.ConstantUtil.OPENAI_ADA_002;
import static com.mule.einstein.internal.helpers.ConstantUtil.OPENAI_GPT_3_5_TURBO;

public class RAGParamsModelDetails {

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional(defaultValue = OPENAI_ADA_002)
  private String embeddingName;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @OfValues(DocumentFileType.class)
  @Optional(defaultValue = "PDF")
  private String fileType;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @OfValues(DocumentSplitOptions.class)
  @Optional(defaultValue = "FULL")
  private String optionType;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional(defaultValue = OPENAI_GPT_3_5_TURBO)
  private String modelName;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional(defaultValue = "0.8")
  private Number probability;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional(defaultValue = "en_US")
  private String locale;

  public String getEmbeddingName() {
    return embeddingName;
  }

  public String getFileType() {
    return fileType;
  }

  public String getOptionType() {
    return optionType;
  }

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
