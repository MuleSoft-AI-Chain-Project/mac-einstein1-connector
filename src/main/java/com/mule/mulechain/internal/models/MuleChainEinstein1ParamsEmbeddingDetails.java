package com.mule.mulechain.internal.models;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.values.OfValues;

public class MuleChainEinstein1ParamsEmbeddingDetails {
	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@OfValues(MuleChainEinstein1EmbeddingNameProvider.class)
	@Optional(defaultValue = "OpenAI Ada 002")
	private String modelName;

	public String getModelName() {
		return modelName;
	}

}