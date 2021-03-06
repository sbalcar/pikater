package org.pikater.core.ontology.subtrees.agent;

import jade.content.AgentAction;

public class AgentClass  implements AgentAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5822829622281554852L;
	
	private String agentClass;

	public AgentClass() {}
	
	public AgentClass(String agentClass) {
		this.agentClass = agentClass;
	}
	
	public String getAgentClass() {
		return agentClass;
	}
	public void setAgentClass(String agentClass) {
		this.agentClass = agentClass;
	}
	

    @Override
    public boolean equals(Object o){
        if(o instanceof AgentClass){
        	AgentClass toCompare = (AgentClass) o;
            return agentClass.equals(toCompare.getAgentClass());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return agentClass.hashCode();
    }
	
}
