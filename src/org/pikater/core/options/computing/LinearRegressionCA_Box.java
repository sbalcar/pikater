package org.pikater.core.options.computing;

import org.pikater.core.agents.experiment.computing.Agent_WekaLinearRegression;
import org.pikater.core.ontology.subtrees.agentinfo.AgentInfo;
import org.pikater.core.ontology.subtrees.batchdescription.ComputingAgent;
import org.pikater.core.ontology.subtrees.newoption.base.NewOption;
import org.pikater.core.ontology.subtrees.newoption.restrictions.RangeRestriction;
import org.pikater.core.ontology.subtrees.newoption.values.BooleanValue;
import org.pikater.core.ontology.subtrees.newoption.values.FloatValue;
import org.pikater.core.ontology.subtrees.newoption.values.IntegerValue;
import org.pikater.core.options.OptionsHelper;
import org.pikater.core.options.SlotsHelper;

public class LinearRegressionCA_Box {

	public static AgentInfo get() {
				
		/*
		 
		# name, type, number od values, parameters range / set
		# r ... range
		# s ... set  (example: s 1, 2, 3, 4, 5, 6, 7, 8)
		# 
		
		# Set the attriute selection method to use. 1 = None, 2 = Greedy (default 0 = M5' method)
		$ S int 1 1 r 0 2
		
		# Do not try to eliminate colinear attributes
		$ C boolean
		
		# The ridge parameter (default 1.0e-8)
		$ R float 1 1 r 0.0000000001 0.0001 

		*/
		
		NewOption optionS = new NewOption("S", new IntegerValue(0), new RangeRestriction(
				new IntegerValue(0),
				new IntegerValue(2))
		);
		optionS.setDescription("Set the attriute selection method to use. 1 = None, 2 = Greedy (default 0 = M5' method)");
		
		
		NewOption optionC = new NewOption("C", new BooleanValue(false));
		optionC.setDescription("Do not try to eliminate colinear attributes");
		

		NewOption optionR = new NewOption("R", new FloatValue(0.00000001f), new RangeRestriction(
				new FloatValue(0.0000000001f),
				new FloatValue(0.0001f))
		);
		optionR.setDescription("The ridge parameter (default 1.0e-8)");
		
	
		AgentInfo agentInfo = new AgentInfo();
		agentInfo.importAgentClass(Agent_WekaLinearRegression.class);
		agentInfo.importOntologyClass(ComputingAgent.class);
	
		agentInfo.setName("LinearRegression");
		agentInfo.setDescription("Linear Regression");
	
		agentInfo.addOption(optionS);
		agentInfo.addOption(optionC);
		agentInfo.addOption(optionR);
		agentInfo.addOptions(OptionsHelper.getCAOptions());
	
		// Slots Definition
		agentInfo.setInputSlots(SlotsHelper.getInputSlots_CA());
		agentInfo.setOutputSlots(SlotsHelper.getOutputSlots_CA());
	
		return agentInfo;
	}
	
}
