package org.pikater.core.options;

import org.pikater.core.agents.experiment.computing.Agent_WekaNBTreeCA;
import org.pikater.core.ontology.subtrees.agentInfo.AgentInfo;
import org.pikater.core.ontology.subtrees.batchDescription.ComputingAgent;

public class NBTree_CABox {
	
	public static AgentInfo get() {

		AgentInfo agentInfo = new AgentInfo();
		agentInfo.importAgentClass(Agent_WekaNBTreeCA.class);
		agentInfo.importOntologyClass(ComputingAgent.class);
	
		agentInfo.setName("NBTree");
		agentInfo.setDescription("NBTree Method");


		// Slots Definition
		agentInfo.setInputSlots(AAA_SlotHelper.getCAInputSlots());
		agentInfo.setOutputSlots(AAA_SlotHelper.getCAOutputSlots());

		return agentInfo;
	}
}
