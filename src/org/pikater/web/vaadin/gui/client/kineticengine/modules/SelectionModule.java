package org.pikater.web.vaadin.gui.client.kineticengine.modules;

import java.util.HashSet;
import java.util.Set;

import net.edzard.kinetic.Container;
import net.edzard.kinetic.Group;
import net.edzard.kinetic.Kinetic;
import net.edzard.kinetic.Node;
import net.edzard.kinetic.Vector2d;
import net.edzard.kinetic.event.EventType;
import net.edzard.kinetic.event.KineticEvent;

import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTKeyboardManager;
import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTMisc;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine.EngineComponent;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.AbstractGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.BoxGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.graph.EdgeGraphItemClient;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.base.BoxListener;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.base.IEngineModule;
import org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo.drag.BoxDragListenerProvider;
import org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo.drag.IBoxDragContext;
import org.pikater.web.vaadin.gui.client.kineticengine.operations.undoredo.drag.IBoxDragEndContext;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.ClickMode;

@SuppressWarnings("deprecation")
public class SelectionModule implements IEngineModule
{
	public static String moduleID;
	
	/**
	 * The engine instance to work with.
	 */
	private final KineticEngine kineticEngine;
	
	/**
	 * A group for anything that is selected. Used to move the selection together in a single drag event and
	 * still leave their relative positions intact. It can not be achieved without this wrapper container. 
	 */
	private final Group selectionGroup;
	
	/**
	 * A self-explanatory variable.
	 */
	private final Set<BoxGraphItemClient> selectedBoxes;
	
	/**
	 * A self-explanatory variable.
	 */
	private final Set<EdgeGraphItemClient> selectedEdges;
	
	/** 
	 * Edges with exactly one of their ends (a box) selected.
	 */
	private final Set<EdgeGraphItemClient> edgesInBetween;
	
	/**
	 * The special event handlers/listeners to attach to graph items.
	 */
	private final class BoxClickListener extends BoxListener
 	{
 		public BoxClickListener(BoxGraphItemClient parentBox)
		{
 			super(parentBox);
		}

		@Override
		protected void handleInner(KineticEvent event)
		{
			if(GWTKeyboardManager.isShiftKeyDown()) // whichever click mode is currently set, don't hesitate when the right modifier key is down
 			{
				doSelectionRelatedOperation(SelectionOperation.getInvertSelectionOperation(parentBox), false, true, parentBox); // select or deselect this box only
	 			onFinish(event);
 			}
 			else if(kineticEngine.getContext().getClickMode() == ClickMode.SELECTION) // it's clear we want to select
 			{
 				if(selectedBoxes.size() == 1)
 				{
 					if(parentBox.isSelected()) // just this one box is selected
 					{
 						doSelectionRelatedOperation(SelectionOperation.DESELECTION, false, true, parentBox); // deselect it
 					}
 					else // some other box is selected
 					{
 						doSelectionRelatedOperation(SelectionOperation.DESELECTION, false, false, getSelectedBoxes()); // deselect it first
 						doSelectionRelatedOperation(SelectionOperation.SELECTION, false, true, parentBox); // and then select this box
 					}
 				}
 				else
 				{
 					doSelectionRelatedOperation(SelectionOperation.DESELECTION, false, false, getSelectedBoxes()); // first deselect everything
 					doSelectionRelatedOperation(SelectionOperation.SELECTION, false, true, parentBox); // and then select this box
 				}
 				onFinish(event);
 			}
			// else - silently let the event go (it could be processed by some other plugin later)
		}
		
		private void onFinish(KineticEvent event)
		{
			event.stopVerticalPropagation();
 			event.setProcessed();
 			
 			kineticEngine.draw(EngineComponent.STAGE);
		}
 	};
	
	/**
	 * Constructor.
	 * @param kineticEngine
	 */
	public SelectionModule(KineticEngine kineticEngine)
	{
		moduleID = GWTMisc.getSimpleName(this.getClass());
		this.kineticEngine = kineticEngine;
		this.selectedBoxes = new HashSet<BoxGraphItemClient>();
		this.selectedEdges = new HashSet<EdgeGraphItemClient>();
		this.edgesInBetween = new HashSet<EdgeGraphItemClient>();
		
		this.selectionGroup = Kinetic.createGroup();
		this.selectionGroup.setPosition(Vector2d.origin);
		this.selectionGroup.setDraggable(true);
		
		BoxDragListenerProvider listenerProvider = new BoxDragListenerProvider(new IBoxDragContext()
		{
			@Override
			public KineticEngine getEngine()
			{
				return SelectionModule.this.kineticEngine;
			}
			
			@Override
			public Vector2d getCurrentPosition()
			{
				return selectionGroup.getPosition();
			}

			@Override
			public BoxGraphItemClient getBoxBeingDragged()
			{
				return null;
			}
			
			@Override
			public Set<EdgeGraphItemClient> getEdgesInBetween()
			{
				return edgesInBetween;
			}

			@Override
			public Node[] getAllMovedNodes()
			{
				return selectionGroup.getChildren().toArray(new Node[0]); 
			}

			@Override
			public void setNewPositions(Node[] allMovedNodes, IBoxDragEndContext context)
			{
				for(Node node : allMovedNodes)
				{
					node.setPosition(node.getX() + context.getDelta().x, node.getY() + context.getDelta().y);
				}
				
				// this will always affect the current selection group but it doesn't matter since this is an invariant:
				selectionGroup.setPosition(Vector2d.origin);
			}

			@Override
			public void setOriginalPositions(Node[] allMovedNodes, IBoxDragEndContext context)
			{
				for(Node node : allMovedNodes)
				{
					node.setPosition(node.getX() - context.getDelta().x, node.getY() - context.getDelta().y);
				}
			}
		});
		this.selectionGroup.addEventListener(listenerProvider.getDragStartListener(), EventType.Basic.DRAGSTART);
		this.selectionGroup.addEventListener(listenerProvider.getDragMoveListener(), EventType.Basic.DRAGMOVE);
		this.selectionGroup.addEventListener(listenerProvider.getDragEndListener(), EventType.Basic.DRAGEND);
		
		this.kineticEngine.getContainer(EngineComponent.LAYER_SELECTION).add(selectionGroup);
	}
	
	// **********************************************************************************************
	// INHERITED INTERFACE

	@Override
	public String getModuleID()
	{
		return moduleID;
	}
	
	@Override
	public void createModuleCrossReferences()
	{
	}

	@Override
	public String[] getGraphItemTypesToAttachHandlersTo()
	{
		return new String[] { GWTMisc.getSimpleName(BoxGraphItemClient.class) };
	}

	@Override
	public void attachHandlers(AbstractGraphItemClient<?> graphItem)
	{
		if(graphItem instanceof BoxGraphItemClient)
		{
			BoxGraphItemClient box = (BoxGraphItemClient) graphItem;
			box.getMasterNode().addEventListener(new BoxClickListener(box), EventType.Basic.CLICK.withName(moduleID));
		}
		else
		{
			throw new IllegalStateException();
		}
	}
	
	// *****************************************************************************************************
	// PUBLIC TYPES AND INTERFACE TO PERFORM ALL SELECTION/DESELECTION RELATED OPERATIONS
	
	public enum SelectionOperation
	{
		SELECTION,
		DESELECTION;
		
		public static SelectionOperation getInvertSelectionOperation(AbstractGraphItemClient<?> graphItem)
		{
			return graphItem.isSelected() ? DESELECTION : SELECTION;
		}
	}
	
	/**
	 * A single method for all selection related operations, acts as a guidepost. Only boxes can be
	 * selected/deselected with this method as it internally selects/deselects the appropriate
	 * edges too.</br>
	 * As huge as this method aims to be, it concentrates heavy logic into this class while offering
	 * a simple declarative interface to the calling code. It also simplifies code needed to launch
	 * additional operations when this one finishes. See the arguments.
	 * @param opKind
	 * @param drawOnFinish
	 * @param notifyServer
	 * @param boxes
	 */
	public void doSelectionRelatedOperation(SelectionOperation opKind, boolean drawOnFinish, boolean notifyServer, BoxGraphItemClient... boxes)
	{
		boolean aBoxInverted = false;
		for(BoxGraphItemClient box : boxes)
		{
			/*
			 * First select/deselect all the boxes (not the edges).
			 * This block of code only reverses the boxes' selection state (both visually and programmatically)
			 * and moves them to the correct kinetic container. See {@link BoxPrototype#invertSelection()}.
			 */
			boolean currentSelectionInverted = false;
			if(opKind == SelectionOperation.SELECTION)
			{
				if(!box.isSelected())
				{
					box.invertSelection();
					currentSelectionInverted = true;
				}
			}
			else
			{
				if(box.isSelected())
				{
					box.invertSelection();
					currentSelectionInverted = true;
				}
			}
			
			if(currentSelectionInverted) // if we inverted the current box's selection
			{
				aBoxInverted = true;
				
				// change the inner state of this class accordingly and select/deselect edges 
				if(box.isSelected())
				{
					onBoxSelection(box);
				}
				else
				{
					onBoxDeselection(box);
				}
			}
		}
		if(aBoxInverted && notifyServer)
		{
			Integer[] selectedBoxesIDs = new Integer[selectedBoxes.size()];
			int index = 0;
			for(BoxGraphItemClient box : selectedBoxes)
			{
				selectedBoxesIDs[index] = box.getInfo().getID();
				index++;
			}
			kineticEngine.getContext().command_selectionChange(selectedBoxesIDs);
		}
		if(drawOnFinish)
		{
			kineticEngine.draw(EngineComponent.STAGE);
		}
	}
	
	/** 
	 * @param edge
	 * @param originalEndPoint the original endpoint of the edge
	 * @param newEndPoint the new endpoint of the edge
	 * @param staticEndPoint the other endpoint of the edge that is unaffected by the drag operation 
	 */
	public void onEdgeDragOperationFinished(EdgeGraphItemClient edge, BoxGraphItemClient originalEndPoint, BoxGraphItemClient newEndPoint, BoxGraphItemClient staticEndPoint)
	{
		// IMPORTANT: this code assumes that the endpoint swap has not been done yet
		if(originalEndPoint.isSelected() != newEndPoint.isSelected())
		{
			if(originalEndPoint.isSelected()) // the original endpoint IS selected and the new one is NOT, so:
			{
				if(staticEndPoint.isSelected()) // the unaffected endpoint IS selected, so:
				{
					edge.invertSelection();
					selectedEdges.remove(edge);
					edgesInBetween.add(edge);
				}
				else // the unaffected endpoint is NOT selected, so:
				{
					edgesInBetween.remove(edge);
				}
			}
			else // the original endpoint is NOT selected and the new one IS, so:
			{
				if(staticEndPoint.isSelected()) // the unaffected endpoint IS selected, so:
				{
					edge.invertSelection();
					selectedEdges.add(edge);
					edgesInBetween.remove(edge);
				}
				else // the unaffected endpoint is NOT selected, so:
				{
					edgesInBetween.add(edge);
				}
			}
		}
	}
	
	public void onEdgeCreateOperation(EdgeGraphItemClient edge)
	{
		if(edge.isSelected())
		{
			throw new IllegalStateException("The given edge is selected. Edges being created can not be selected.");
		}
		else
		{
			switch (edge.getSelectedEndpointsCount())
			{
				case 1:
					edgesInBetween.add(edge);
					break;
				case 2:
					edge.invertSelection();
					selectedEdges.add(edge);
					break;
				default:
					break;
			}
		}
	}
	
	// *****************************************************************************************************
	// OTHER PUBLIC INTERFACE
	
	public Container getSelectionContainer()
	{
		return selectionGroup;
	}
	
	public BoxGraphItemClient[] getSelectedBoxes()
	{
		return selectedBoxes.toArray(new BoxGraphItemClient[0]);
	}
	
	public EdgeGraphItemClient[] getSelectedEdges()
	{
		return selectedEdges.toArray(new EdgeGraphItemClient[0]);
	}
	
	public EdgeGraphItemClient[] getEdgesInBetween()
	{
		return edgesInBetween.toArray(new EdgeGraphItemClient[0]);
	}
	
	public EdgeGraphItemClient[] getAllRelatedEdges()
	{
		Set<EdgeGraphItemClient> result = new HashSet<EdgeGraphItemClient>(selectedEdges);
		result.addAll(edgesInBetween);
		return result.toArray(new EdgeGraphItemClient[0]);
	}
	
	// *****************************************************************************************************
	// PRIVATE INTERFACE
	
	private void onBoxSelection(BoxGraphItemClient box)
	{
		selectedBoxes.add(box);
		
		for(EdgeGraphItemClient edge : box.connectedEdges)
		{
			if(edge.areBothEndsSelected())
			{
				edge.invertSelection();
				edgesInBetween.remove(edge);
				selectedEdges.add(edge);
			}
			else // 1 end is selected
			{
				edgesInBetween.add(edge);
			}
		}
	}
	
	private void onBoxDeselection(BoxGraphItemClient box)
	{
		selectedBoxes.remove(box);
		
		for(EdgeGraphItemClient edge : box.connectedEdges)
		{
			// at least one of the edge's endpoints must not be selected at this moment, so:
			if(edge.isExactlyOneEndSelected())
			{
				edge.invertSelection();
				edgesInBetween.add(edge);
				selectedEdges.remove(edge);
			}
			else // neither end is selected
			{
				edgesInBetween.remove(edge);
			}
		}
	}
}
