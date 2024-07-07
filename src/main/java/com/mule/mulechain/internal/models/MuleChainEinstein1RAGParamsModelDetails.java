package com.mule.mulechain.internal.models;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.values.OfValues;

import com.mule.mulechain.internal.helpers.documents.MuleChainEinstein1DocumentFileType;
import com.mule.mulechain.internal.helpers.documents.MuleChainEinstein1DocumentSplitOptions;

public class MuleChainEinstein1RAGParamsModelDetails {


	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@OfValues(MuleChainEinstein1EmbeddingNameProvider.class)
	@Optional(defaultValue = "OpenAI Ada 002")
	private String embeddingName;

	public String getEmbeddingName() {
		return embeddingName;
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


	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@OfValues(MuleChainEinstein1ModelNameProvider.class)
	@Optional(defaultValue = "OpenAI GPT 3.5 Turbo")
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