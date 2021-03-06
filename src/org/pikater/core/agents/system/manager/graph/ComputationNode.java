package org.pikater.core.agents.system.manager.graph;

import org.pikater.core.CoreConfiguration;
import org.pikater.core.agents.system.manager.graph.edges.EdgeValue;
import org.pikater.core.agents.system.manager.parser.ComputationOutputBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Computation node stores inputs and outputs for some task in computation graph
 * (recommend, search, compute]. It laso contains appropriate strategy
 * determined by the node type User: Kuba Date: 10.5.2014 Time: 12:35
 */
public class ComputationNode {
	private boolean idle = true;
	private int id;
	/**
	 * The Number of tasks in progress.
	 */
	protected int numberOfTasksInProgress;
	private Map<String, ArrayList<ComputationOutputBuffer<EdgeValue>>> outputs = new HashMap<>();
	private Map<String, ComputationOutputBuffer> inputs = new HashMap<>();
	private StartComputationStrategy startBehavior;
	/**
	 * The Computation graph.
	 */
	protected ComputationGraph computationGraph;

	/**
	 * Instantiates a new Computation node.
	 * 
	 * @param computationGraph
	 *            Owning computation graph
	 */
	public ComputationNode(ComputationGraph computationGraph) {
		this.computationGraph = computationGraph;
		initDefault();
	}

	/**
	 * Instantiates a new Computation node.
	 * 
	 * @param executeStrategy
	 *            Strategy that will be executed if the inputs are filled
	 */
	public ComputationNode(StartComputationStrategy executeStrategy) {
		initDefault();
		startBehavior = executeStrategy;
	}

	/**
	 * Contains output with some name
	 * 
	 * @param outputName
	 *            Name of the output
	 * @return True if present
	 */
	public Boolean containsOutput(String outputName) {
		return outputs.containsKey(outputName);
	}

	/**
	 * Gte all inputs
	 * 
	 * @return All inputs defined in this node
	 */
	public Map<String, ComputationOutputBuffer> getInputs() {
		return inputs;
	}

	/**
	 * Can computation starts - inputs filled, is not blocked, etc.
	 * 
	 * @return True if all prerequisities satisifed
	 */
	public boolean canComputationStart() {

		if (!idle) {
			// another computation is running
			return false;
		}
		for (ComputationOutputBuffer inputI : inputs.values()) {

			if ((inputI.size() == 0) && (!inputI.isBlocked())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Add input to inputs
	 * 
	 * @param inputName
	 *            name of the new inputs
	 * @param buffer
	 *            Buffer of the input
	 */
	public void addInput(String inputName, ComputationOutputBuffer buffer) {
		if (!inputs.containsKey(inputName)) {
			inputs.put(inputName, buffer);
		}
	}

	/**
	 * Add ouputt to outputs
	 * 
	 * @param outputName
	 *            Name of the new output
	 */
	public void addOutput(String outputName) {
		if (!outputs.containsKey(outputName)) {

			outputs.put(outputName,
					new ArrayList<ComputationOutputBuffer<EdgeValue>>());
		}

	}

	/**
	 * Add buffer to named output
	 * 
	 * @param outputName
	 *            name of the output receiving the buffer
	 * @param buffer
	 *            Buffer to add
	 */
	public void addBufferToOutput(String outputName,
			ComputationOutputBuffer buffer) {
		addOutput(outputName);
		outputs.get(outputName).add(buffer);
	}

	/**
	 * Add edge value to output and check if the computation can go to the
	 * successors
	 * 
	 * @param o
	 *            New edge value that will be added to the output
	 * @param outputName
	 *            Name of the output
	 */
	public void addToOutputAndProcess(EdgeValue o, String outputName) {
		addToOutputAndProcess(o, outputName, false, false);
	}

	/**
	 * Add edge value to output and check if the computation can go to the
	 * successors
	 * 
	 * @param o
	 *            New edge value that will be added to the output
	 * @param outputName
	 *            Name of the output
	 * @param unblock
	 *            If output should be unblocked
	 * @param isData
	 *            Mark as data
	 */
	public void addToOutputAndProcess(EdgeValue o, String outputName,
			Boolean unblock, Boolean isData) {

		if (outputs.get(outputName) == null) {
			return;
		}
		List<ComputationOutputBuffer<EdgeValue>> outs = outputs.get(outputName);
		for (ComputationOutputBuffer<EdgeValue> outI : outs) {
			if (unblock) {
				outI.unblock();
			}

			outI.setData(isData);

			outI.insert(o);
			// was zero before - check for computation start
			if ((outI.size() == 1) && outI.getTarget().canComputationStart()) {
				outI.getTarget().startComputation();
			}
		}
	}

	/**
	 * Starts computation - will be busy and execute appropriate behavior
	 */
	public void startComputation() {
		idle = false;
		startBehavior.execute(this);
	}

	/**
	 * Computation finished, not busy, will check if computation can start and
	 * execute if possible
	 */
	public void computationFinished() {
		idle = true;
		numberOfTasksChanged();
		if (canComputationStart()) {
			startComputation();
		}
	}

	/**
	 * Gets id of this node
	 * 
	 * @return Id of this node
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets id of this node
	 * 
	 * @param id
	 *            Id to be set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets startegz that will be executed on computation start
	 * 
	 * @param startBehavior
	 *            the start behavior
	 */
	public void setStartBehavior(StartComputationStrategy startBehavior) {
		this.startBehavior = startBehavior;
	}

	/**
	 * Inits default settings
	 */
	private void initDefault() {
		id = CoreConfiguration.getGUIDGenerator().getAndAllocateGUID();
	}

	/**
	 * Number of tasks have changed - update state
	 */
	public void numberOfTasksChanged() {
		if (numberOfTasksInProgress == 0) {
			computationGraph.updateState();
		}
	}

	/**
	 * Decrease number of tasks
	 */
	public void decreaseNumberOfOutstandingTask() {
		numberOfTasksInProgress--;
		numberOfTasksChanged();
	}

	/**
	 * Increase number of tasks
	 */
	public void increaseNumberOfOutstandingTask() {
		numberOfTasksInProgress++;
		numberOfTasksChanged();
	}

	/**
	 * Any tasks running?
	 * 
	 * @return the boolean
	 */
	public boolean existsUnfinishedTasks() {
		return numberOfTasksInProgress > 0 || !idle;
	}

	/**
	 * Find output.
	 * 
	 * @param in
	 *            output name
	 * @return the list
	 */
	public List<String> findOutput(String in) {
		List<String> keys = new ArrayList<>();

		for (Object o : outputs.entrySet()) {
			Map.Entry<String, ArrayList<ComputationOutputBuffer<EdgeValue>>> pairs = (Map.Entry) o;
			Iterator it1 = pairs.getValue().iterator();
			while (it1.hasNext()) {
				ComputationOutputBuffer<EdgeValue> cob = (ComputationOutputBuffer<EdgeValue>) it1
						.next();
				String target = cob.getTargetInput();
				if (target != null && target.equals(in)) {
					keys.add(pairs.getKey());
				}
			}
		}
		return keys;
	}
}
