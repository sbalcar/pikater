package org.pikater.web.vaadin.gui.client.kineticengine.modules;

import java.util.HashSet;
import java.util.Set;

import net.edzard.kinetic.Container;
import net.edzard.kinetic.Group;
import net.edzard.kinetic.Kinetic;
import net.edzard.kinetic.Node;
import net.edzard.kinetic.Vector2d;
import net.edzard.kinetic.event.EventType;
import net.edzard.kinetic.event.IEventListener;
import net.edzard.kinetic.event.KineticEvent;

import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTCursorManager;
import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTKeyboardManager;
import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTMisc;
import org.pikater.web.vaadin.gui.client.gwtmanagers.GWTCursorManager.MyCursor;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine.EngineComponent;
import org.pikater.web.vaadin.gui.client.kineticengine.graphitems.BoxPrototype;
import org.pikater.web.vaadin.gui.client.kineticengine.graphitems.EdgePrototype;
import org.pikater.web.vaadin.gui.client.kineticengine.graphitems.ExperimentGraphItem;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.base.BoxListener;
import org.pikater.web.vaadin.gui.client.kineticengine.modules.base.IEngineModule;
import org.pikater.web.vaadin.gui.shared.KineticComponentClickMode;

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
	private final Set<BoxPrototype> selectedBoxes;
	
	/** 
	 * Edges with exactly one of their ends (a box) selected.
	 */
	private final Set<EdgePrototype> edgesInBetween;
	
	/**
	 * The special event handlers/listeners to attach to graph items.
	 */
	private final class BoxClickListener extends BoxListener
 	{
 		public BoxClickListener(BoxPrototype parentBox)
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
 			else if(kineticEngine.getContext().getClickMode() == KineticComponentClickMode.SELECTION) // it's clear we want to select
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
		this.selectedBoxes = new HashSet<BoxPrototype>();
		this.edgesInBetween = new HashSet<EdgePrototype>();
		
		this.selectionGroup = Kinetic.createGroup();
		this.selectionGroup.setPosition(Vector2d.origin);
		this.selectionGroup.setDraggable(true);
		this.selectionGroup.addEventListener(selectionGroupDragStartHandler, EventType.Basic.DRAGSTART);
		this.selectionGroup.addEventListener(selectionGroupDragMoveHandler, EventType.Basic.DRAGMOVE);
		this.selectionGroup.addEventListener(selectionGroupDragEndHandler, EventType.Basic.DRAGEND);
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
	public String[] getItemsToAttachTo()
	{
		return new String[] { GWTMisc.getSimpleName(BoxPrototype.class) };
	}

	@Override
	public void attachEventListeners(ExperimentGraphItem graphItem)
	{
		if(graphItem instanceof BoxPrototype)
		{
			BoxPrototype box = (BoxPrototype) graphItem;
			box.getMasterNode().addEventListener(new BoxClickListener(box), EventType.Basic.CLICK.withName(moduleID));
		}
		else
		{
			throw new IllegalStateException();
		}
	}
	
	// *****************************************************************************************************
	// SELECTION DRAGGING CODE
	
	/*
	 * Event handlers to define selection dragging. 
	 */
	private final IEventListener selectionGroupDragStartHandler = new IEventListener()
	{
		@Override
		public void handle(KineticEvent event)
		{
			GWTCursorManager.setCursorType(kineticEngine.getContext().getStageDOMElement(), MyCursor.MOVE);
			
			// start of a drag operation - turn all edges in between into dashed lines of a special color that connect the centers of their endpoints boxes
			kineticEngine.fromEdgesToBaseLines(getEdgesInBetween(), null); // draws changes by default
			event.stopVerticalPropagation();
		}
	};
	private final IEventListener selectionGroupDragMoveHandler = new IEventListener()
	{
		@Override
		public void handle(KineticEvent event)
		{
			kineticEngine.updateBaseLines(getEdgesInBetween(), null); // draws changes by default
			event.stopVerticalPropagation();
		}
	};
	private final IEventListener selectionGroupDragEndHandler = new IEventListener()
	{
		@Override
		public void handle(KineticEvent event)
		{
			GWTCursorManager.rollBackCursor(kineticEngine.getContext().getStageDOMElement());
			
			// end of a drag - undo the effects of drag start and propagate the drag changes to selection items
			kineticEngine.moveSelected(getEdgesInBetween()); // propagate the selection group's position to the selected items
			kineticEngine.fromBaseLinesToEdges(getEdgesInBetween()); // draws changes by default
			event.stopVerticalPropagation();
		}
	};
	
	// *****************************************************************************************************
	// PUBLIC TYPES AND INTERFACE TO PERFORM ALL SELECTION/DESELECTION RELATED OPERATIONS
	
	public enum SelectionOperation
	{
		SELECTION,
		DESELECTION;
		
		public static SelectionOperation getInvertSelectionOperation(ExperimentGraphItem graphItem)
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
	public void doSelectionRelatedOperation(SelectionOperation opKind, boolean drawOnFinish, boolean notifyServer, BoxPrototype... boxes)
	{
		boolean aBoxInverted = false;
		for(BoxPrototype box : boxes)
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
		if(aBoxInverted && notifyServer && kineticEngine.getContext().openOptionsManagerOnSelectionChange())
		{
			String[] selectedBoxesIDs = new String[selectedBoxes.size()];
			int index = 0;
			for(BoxPrototype box : selectedBoxes)
			{
				selectedBoxesIDs[index] = box.getInfo().boxID;
				index++;
			}
			kineticEngine.getContext().command_openOptionsManager(selectedBoxesIDs);
		}
		if(drawOnFinish)
		{
			kineticEngine.draw(EngineComponent.STAGE);
		}
	}
	
	public void onEdgeDragOperationFinished(EdgePrototype edge, BoxPrototype originalEndPoint, BoxPrototype newEndPoint, BoxPrototype staticEndPoint)
	{
		// IMPORTANT: this code assumes that the endpoint swap has not been done yet
		if(originalEndPoint.isSelected() != newEndPoint.isSelected())
		{
			if(originalEndPoint.isSelected())
			{
				// the original endpoint IS selected and the new one is NOT, so:
				if(staticEndPoint.isSelected())
				{
					edge.invertSelection();
					edgesInBetween.add(edge);
				}
				else
				{
					edgesInBetween.remove(edge);
				}
			}
			else
			{
				// the original endpoint is NOT selected and the new one IS, so:
				if(staticEndPoint.isSelected())
				{
					edge.invertSelection();
					edgesInBetween.remove(edge);
				}
				else
				{
					edgesInBetween.add(edge);
				}
			}
		}
	}
	
	public void onEdgeCreateOperation(EdgePrototype edge)
	{
		if(edge.isSelected())
		{
			throw new IllegalArgumentException("The provided edge is selected. Created edges can not be selected.");
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
	
	public Node[] getSelectedKineticNodes()
	{
		return getSelectionContainer().getChildren().toArray(new Node[0]);
	}
	
	public BoxPrototype[] getSelectedBoxes()
	{
		return selectedBoxes.toArray(new BoxPrototype[0]);
	}
	
	public Set<EdgePrototype> getEdgesInBetween()
	{
		return edgesInBetween;
	}
	
	// *****************************************************************************************************
	// PRIVATE INTERFACE
	
	private void onBoxSelection(BoxPrototype box)
	{
		selectedBoxes.add(box);
		
		for(EdgePrototype edge : box.connectedEdges)
		{
			if(edge.areBothEndsSelected())
			{
				edge.invertSelection();
				edgesInBetween.remove(edge);
			}
			else // 1 end is selected
			{
				edgesInBetween.add(edge);
			}
		}
	}
	
	private void onBoxDeselection(BoxPrototype box)
	{
		selectedBoxes.remove(box);
		
		for(EdgePrototype edge : box.connectedEdges)
		{
			// at least one of the edge's endpoints must not be selected at this moment, so:
			if(edge.isExactlyOneEndSelected())
			{
				edge.invertSelection();
				edgesInBetween.add(edge);
			}
			else // neither end is selected
			{
				edgesInBetween.remove(edge);
			}
		}
	}
}