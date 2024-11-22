package com.mule.einstein.internal.models;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.values.OfValues;

import com.mule.einstein.internal.helpers.documents.DocumentFileType;
import com.mule.einstein.internal.helpers.documents.DocumentSplitOptions;

public class RAGParamsModelDetails {


	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@OfValues(EmbeddingNameProvider.class)
	@Optional(defaultValue = "OpenAI Ada 002")
	private String embeddingName;

	public String getEmbeddingName() {
		return embeddingName;
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


	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@OfValues(ModelNameProvider.class)
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