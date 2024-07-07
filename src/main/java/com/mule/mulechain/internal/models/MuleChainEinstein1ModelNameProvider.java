package com.mule.mulechain.internal.models;
import java.util.Set;
import org.mule.runtime.api.value.Value;
import org.mule.runtime.extension.api.values.ValueBuilder;
import org.mule.runtime.extension.api.values.ValueProvider;
import org.mule.runtime.extension.api.values.ValueResolvingException;

public class MuleChainEinstein1ModelNameProvider implements ValueProvider {

	private static final Set<Value> VALUES_FOR = ValueBuilder.getValuesFor(
	"Anthropic Claude 3 Haiku on Amazon",
	"Azure OpenAI GPT 3.5 Turbo",
	"Azure OpenAI GPT 3.5 Turbo 16k",
	"Azure OpenAI GPT 4 Turbo",
	"OpenAI GPT 3.5 Turbo",
	"OpenAI GPT 3.5 Turbo 16k",
	"OpenAI GPT 4",
	"OpenAI GPT 4 32k",
	"OpenAI GPT 4o (Omni)",
	"OpenAI GPT 4 Turbo"
	);

	@Override
	public Set<Value> resolve() throws ValueResolvingException {
		
		return VALUES_FOR;
	}

}