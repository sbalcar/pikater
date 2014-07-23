package org.pikater.core.agents.experiment.virtual;

import org.pikater.core.ontology.subtrees.agentInfo.AgentInfo;
import org.pikater.core.options.CrossvalidationEvaluationMethod_VirtualBox;

public class Agent_VirtualBoxCrossvalidationEvaluationMethod extends Agent_VirtualBoxProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3074428585945051239L;

	@Override
	protected AgentInfo getAgentInfo() {

		return CrossvalidationEvaluationMethod_VirtualBox.get();
	}


}
