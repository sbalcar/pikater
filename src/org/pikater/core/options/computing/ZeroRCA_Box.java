package org.pikater.core.options.computing;

import org.pikater.core.agents.experiment.computing.Agent_ZeroRCA;
import org.pikater.core.ontology.subtrees.agentInfo.AgentInfo;
import org.pikater.core.ontology.subtrees.batchDescription.ComputingAgent;
import org.pikater.core.options.AAA_SlotHelper;

public class ZeroRCA_Box {
	
	public static AgentInfo get() {

		AgentInfo agentInfo = new AgentInfo();
		agentInfo.importAgentClass(Agent_ZeroRCA.class);
		agentInfo.importOntologyClass(ComputingAgent.class);
	
		agentInfo.setName("ZeroR");
		agentInfo.setDescription("Zero R Method");

		agentInfo.addOptions(AAA_SlotHelper.getCAOptions());
		
		// Slots Definition
		agentInfo.setInputSlots(AAA_SlotHelper.getCAInputSlots());
		agentInfo.setOutputSlots(AAA_SlotHelper.getCAOutputSlots());
		
		return agentInfo;
	}
}