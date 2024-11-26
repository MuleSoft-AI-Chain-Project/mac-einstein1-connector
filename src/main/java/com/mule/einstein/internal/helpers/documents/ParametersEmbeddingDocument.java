package com.mule.einstein.internal.helpers.documents;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.values.OfValues;

import static com.mule.einstein.internal.helpers.ConstantUtil.OPENAI_ADA_002;

public class ParametersEmbeddingDocument {

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional(defaultValue = OPENAI_ADA_002)
  private String modelName;
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

  public String getModelName() {
    return modelName;
  }

  public String getFileType() {
    return fileType;
  }

  public String getOptionType() {
    return optionType;
  }
}
