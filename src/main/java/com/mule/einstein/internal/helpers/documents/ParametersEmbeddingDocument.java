package com.mule.einstein.internal.helpers.documents;

import com.mule.einstein.internal.models.provider.EmbeddingModelApiNameProvider;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.values.OfValues;

import static com.mule.einstein.internal.helpers.ConstantUtil.OPENAI_ADA_002;

public class ParametersEmbeddingDocument {

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @OfValues(EmbeddingModelApiNameProvider.class)
  @Optional(defaultValue = OPENAI_ADA_002)
  @DisplayName("Model API Name")
  private String modelApiName;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @OfValues(DocumentFileType.class)
  @Optional(defaultValue = "PDF")
  @DisplayName("File Type")
  private String fileType;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @OfValues(DocumentSplitOptions.class)
  @Optional(defaultValue = "FULL")
  @DisplayName("Option Type")
  private String optionType;

  public String getModelApiName() {
    return modelApiName;
  }

  public String getFileType() {
    return fileType;
  }

  public String getOptionType() {
    return optionType;
  }
}
