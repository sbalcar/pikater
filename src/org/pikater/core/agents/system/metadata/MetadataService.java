package org.pikater.core.agents.system.metadata;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.domain.FIPAException;
import jade.domain.FIPAService;
import jade.lang.acl.ACLMessage;

import org.pikater.core.CoreAgents;
import org.pikater.core.agents.PikaterAgent;
import org.pikater.core.ontology.MetadataOntology;
import org.pikater.core.ontology.subtrees.metadata.NewComputedData;
import org.pikater.core.ontology.subtrees.metadata.NewDataset;

/**
 * 
 * Class offers possibility to call the most used
 * requests to the {@link Agent_MetadataQueen}
 *
 */
public class MetadataService {
	
	/**
	 * Sends a request to generate {@link Metadata} for the DataSet
	 * to {@link Agent_MetadataQueen}
	 * @param agent Sender {@link PikaterAgent} of the message 
	 * @param dataSetID ID of the dataset we request computation of metadata for
	 * @param userID ID of user requesting metadata computation
	 */
	public static void requestMetadataForDataset(PikaterAgent agent,
			int dataSetID, int userID) {
		
		AID receiver = new AID(CoreAgents.METADATA_QUEEN.getName(), false);

		NewDataset newDataset = new NewDataset();
		newDataset.setUserID(userID);
		newDataset.setDataSetID(dataSetID);
		
		agent.logInfo("Sending request to store metadata for DataSetID: " + dataSetID);

		try {
			Ontology ontology = MetadataOntology.getInstance();

			/*
			 * Message composition
			 */
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			request.addReceiver(receiver);
			request.setLanguage(agent.getCodec().getName());
			request.setOntology(ontology.getName());
			agent.getContentManager().fillContent(request, new Action(receiver, newDataset));

			ACLMessage reply = FIPAService.doFipaRequestClient(agent, request, 10000);
			if (reply == null) {
				agent.logSevere("Reply not received.");
			} else {
				agent.logInfo("Reply received: " +
						ACLMessage.getPerformative(reply.getPerformative()) +
						" " + reply.getContent());
			}
		} catch (CodecException e) {
			agent.logException("Codec error occurred: " + e.getMessage(), e);
		} catch (OntologyException e) {
			agent.logException("Ontology error occurred: " + e.getMessage(), e);
		} catch (FIPAException e) {
			agent.logException("FIPA error occurred: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Sends a request to generate {@link Metadata} for the computed data
	 * to {@link Agent_MetadataQueen}
	 * @param agent {@link PikaterAgent} sending the message
	 * @param computedDataID ID of the dataset, that have been created by som {@link PikaterAgent}
	 * @param userID ID of user requesting metadata computation (in this case should be the owner
	 * of the experiment)
	 */
	public static void requestMetadataForComputedData(PikaterAgent agent,
			int computedDataID, int userID) {

		AID receiver = new AID(CoreAgents.METADATA_QUEEN.getName(), false);

		NewComputedData newComputedData = new NewComputedData();
		newComputedData.setUserID(userID);
		newComputedData.setComputedDataID(computedDataID);
		
		agent.logInfo("Sending request to store metadata for "
				+ "ComputedDataID: " + computedDataID);

		try {
			Ontology ontology = MetadataOntology.getInstance();

			/*
			 * Message composition 
			 */
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			request.addReceiver(receiver);
			request.setLanguage(agent.getCodec().getName());
			request.setOntology(ontology.getName());
			
			Action action = new Action(receiver, newComputedData);
			agent.getContentManager().fillContent(request, action);

			ACLMessage reply = FIPAService.doFipaRequestClient(agent, request);
			if (reply == null) {
				agent.logSevere("Reply not received.");
			} else {
				agent.logInfo("Reply received: " +
						ACLMessage.getPerformative(reply.getPerformative()) +
						" " + reply.getContent());
			}
		} catch (CodecException e) {
			agent.logException("Codec error occurred: " + e.getMessage(), e);
		} catch (OntologyException e) {
			agent.logException("Ontology error occurred: " + e.getMessage(), e);
		} catch (FIPAException e) {
			agent.logException("FIPA error occurred: " + e.getMessage(), e);
		}
	}
	
}
