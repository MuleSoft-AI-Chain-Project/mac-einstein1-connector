package com.mulesoft.connector.agentforce.internal.botapi.models;

import com.mulesoft.connector.agentforce.internal.botapi.metadata.AgentListValueProvider;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.values.OfValues;

public class BotAgentParameterGroup {

  @Parameter
  @Placement(order = 1)
  @OfValues(AgentListValueProvider.class)
  @DisplayName("Agent List")
  private String agent;

  public String getAgent() {
    return agent;
  }

  public void setAgent(String agentValue) {
    this.agent = agentValue;
  }

}
