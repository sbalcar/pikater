package org.pikater.web.vaadin.gui.client.kineticeditor;

import org.pikater.shared.experiment.webformat.SchemaDataSource;
import org.pikater.shared.experiment.webformat.BoxInfoCollection;

import com.vaadin.shared.AbstractComponentState;

public class KineticEditorState extends AbstractComponentState
{
	private static final long serialVersionUID = 7400546695911691608L;
	
	/*
	 * VAADIN SERIALIZATION ISSUES:
	 * For more information:
	 * http://dev.vaadin.com/wiki/Vaadin7/SharedState
	 * 
	 * GWT SERIALIZATION ISSUES:
	 * - GENERAL - Classes used in GWT RPC have to implement isSerializable or Serializable.
	 * - GENERAL - Protected constructor makes GWT happy.
	 * - GENERAL - Final fields are not serialized in GWT.
	 * - SPECIFIC - String.format method is not translatable to GWT.
	 * For more information:
	 * http://www.gwtproject.org/doc/latest/DevGuideServerCommunication.html#DevGuideSerializableTypes
	 */
	
	//---------------------------------------------------------------------
	// APPLICATION-WIDE BOX DEFINITIONS
	
	/*
	 * TODO: instead of sharing the box definitions like this, use a UI extension to make it "global"?
	 * Well, it would have to get transferred every time a user opens that UI... not good.
	 * 
	 * Another possibility would be to wrap this feature in a dedicated component. Doh... terrible but doable.
	 */
	
	/**
	 * The box definitions that this Editor component's boxes will refer to. Has to be set on the server-component before
	 * being pushed to the client.
	 */
	public BoxInfoCollection infoProvider = null;
	
	/**
	 * The previously defined experiment to view in the editor. It is expected expected to be valid and will be loaded
	 * when the state is shared with the client.
	 * Has to be set on the server-component before being pushed to the client.
	 */
	public SchemaDataSource experimentToLoad = null;
}