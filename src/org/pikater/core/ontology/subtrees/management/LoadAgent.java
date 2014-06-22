package org.pikater.core.ontology.subtrees.management;

import org.pikater.core.ontology.subtrees.task.ExecuteTask;

import jade.content.AgentAction;

public class LoadAgent implements AgentAction {

	private static final long serialVersionUID = -2890249253440084L;

	private String filename;
	private ExecuteTask first_action = null;
	private byte [] object;
	
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public void setFirst_action(ExecuteTask first_action) {
		this.first_action = first_action;
	}
	public ExecuteTask getFirst_action() {
		return first_action;
	}

	public void setObject(byte [] object) {
		this.object = object;
	}
	public byte [] getObject() {
		return object;
	}
	
}
