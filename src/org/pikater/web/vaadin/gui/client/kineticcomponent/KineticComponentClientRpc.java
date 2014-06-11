package org.pikater.web.vaadin.gui.client.kineticcomponent;

import org.pikater.shared.experiment.webformat.BoxInfo;
import org.pikater.shared.experiment.webformat.ExperimentGraph;

import com.vaadin.shared.communication.ClientRpc;

public interface KineticComponentClientRpc extends ClientRpc
{
	void command_createBox(BoxInfo info);
	void command_receiveExperimentToLoad(ExperimentGraph experiment);
	void command_resetKineticEnvironment();
	
	void request_reloadVisualStyle();
	void request_sendExperimentToSave();
}