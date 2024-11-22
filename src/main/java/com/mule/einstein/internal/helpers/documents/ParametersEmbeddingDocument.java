package com.mule.einstein.internal.helpers.documents;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.values.OfValues;

import com.mule.einstein.internal.models.EmbeddingNameProvider;

public class ParametersEmbeddingDocument {
	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@OfValues(EmbeddingNameProvider.class)
	@Optional(defaultValue = "OpenAI Ada 002")
	private String modelName;

	public String getModelName() {
		return modelName;
	}

	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@OfValues(DocumentFileType.class)
	@Optional(defaultValue = "PDF")
	private String fileType;

	public String getFileType() {
		return fileType;
	}

	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@OfValues(DocumentSplitOptions.class)
	@Optional(defaultValue = "FULL")
	private String optionType;

	public String getOptionType() {
		return optionType;
	}


}