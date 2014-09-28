package org.pikater.core.agents.system.computation.graph.edges;

/**
 * Edge with agent type
 * User: Kuba
 * Date: 10.5.2014
 * Time: 13:36
 */
public class AgentTypeEdge extends EdgeValue {
	private String agentType;

    //TODO: looks obsolete
	private Integer model;

    /**
     *
     * @param agentType Agent type
     */
	public AgentTypeEdge(String agentType) {
		this.agentType = agentType;
	}

	public AgentTypeEdge(String agentType, Integer model) {
		this(agentType);
		this.model = model;
	}

	public String getAgentType() {
		return agentType;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	public Integer getModel() {
		return model;
	}

	public void setModel(Integer model) {
		this.model = model;
	}
}
