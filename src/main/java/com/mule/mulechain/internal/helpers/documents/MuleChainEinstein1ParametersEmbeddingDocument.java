package com.mule.mulechain.internal.helpers.documents;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.values.OfValues;

import com.mule.mulechain.internal.models.MuleChainEinstein1EmbeddingNameProvider;

public class MuleChainEinstein1ParametersEmbeddingDocument {
	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@OfValues(MuleChainEinstein1EmbeddingNameProvider.class)
	@Optional(defaultValue = "OpenAI Ada 002")
	private String modelName;

	public String getModelName() {
		return modelName;
	}

	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@OfValues(MuleChainEinstein1DocumentFileType.class)
	@Optional(defaultValue = "PDF")
	private String fileType;

	public String getFileType() {
		return fileType;
	}

	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@OfValues(MuleChainEinstein1DocumentSplitOptions.class)
	@Optional(defaultValue = "FULL")
	private String optionType;

	public String getOptionType() {
		return optionType;
	}


}