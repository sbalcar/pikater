package org.pikater.core.ontology.subtrees.batchDescription;


import java.util.ArrayList;
import java.util.List;

import org.pikater.core.ontology.subtrees.option.Option;
import org.pikater.core.ontology.subtrees.task.EvaluationMethod;


/**
 * Created by Martin Pilat on 28.12.13.
 */
public class ComputingAgent extends DataProcessing implements IDataProvider, IComputingAgent, IErrorProvider {

	private static final long serialVersionUID = 2127755171666013125L;

	private String agentType;
	private IModelDescription model;
    private List<Option> options = new ArrayList<Option>();
    private EvaluationMethod evaluationMethod;
    
	private DataSourceDescription trainingData;
    private DataSourceDescription testingData;
    private DataSourceDescription validationData;

    public String getAgentType() {
		return agentType;
	}
	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	public IModelDescription getModel() {
		return model;
	}
	public void setModel(IModelDescription model) {
		this.model = model;
	}    

    public DataSourceDescription getTrainingData() {
        return trainingData;
    }
    public void setTrainingData(DataSourceDescription trainingData) {
        this.trainingData = trainingData;
    }

    public DataSourceDescription getTestingData() {
        return testingData;
    }
    public void setTestingData(DataSourceDescription testingData) {
        this.testingData = testingData;
    }

    public DataSourceDescription getValidationData() {
        return validationData;
    }
    public void setValidationData(DataSourceDescription validationData) {
        this.validationData = validationData;
    }    
	
	public EvaluationMethod getEvaluationMethod() {
		return evaluationMethod;
	}
	public void setEvaluationMethod(EvaluationMethod evaluationMethod) {
		this.evaluationMethod = evaluationMethod;
	}
	
    public List<Option> getOptions() {
        return options;
    }
    public void setOptions(List<Option> options) {
    	
    	if (options == null) {
    		throw new NullPointerException("Argument options can't be null");
    	}
        this.options = options;
    }
    public void addOption(Option option) {
    	
    	if (option == null) {
    		throw new NullPointerException("Argument option can't be null");
    	}
        this.options.add(option);
    }


	@Override
	public List<Option> exportAllOptions() {
		
		Option agentTypeOption = new Option();
		agentTypeOption.setName("agentType");
		agentTypeOption.setValue(agentType);

		Option modelOption = new Option();
		modelOption.setName("model");
		modelOption.setValue(model.toString()); // TODO

		Option evaluationMethodOption = new Option();
		evaluationMethodOption.setName("evaluationMethod");
		evaluationMethodOption.setValue(evaluationMethod.toString()); // TODO

		
		List<Option> options = new ArrayList<Option>();
		options.add(agentTypeOption);
		options.add(modelOption);
		options.add(evaluationMethodOption);
		options.addAll(this.options);
		return options;
	}
	@Override
	public void importAllOptions(List<Option> options) {
		
		List<Option> optionss = new ArrayList<Option>();
		
		for (Option optionI : options) {
			
			if (optionI.getName().equals("agentType")) {
				this.agentType = optionI.getValue();
				
			} else if (optionI.getName().equals("model")) {
				//this.evaluationMethod = optionI.getValue(); TODO:

			} else if (optionI.getName().equals("evaluationMethod")) {
				//this.evaluationMethod = optionI.getValue();  TODO:
				
			} else {
				optionss.add(optionI);
			}
		}
		
		this.options = optionss;
		
	}
	
	@Override
	public List<ErrorDescription> exportAllErrors() {
		return new ArrayList<ErrorDescription>();
	}
	@Override
	public void importAllErrors(List<ErrorDescription> errors) {
		
		if (errors != null && !errors.isEmpty()) {
			throw new IllegalArgumentException("Argument errors can be only null");
		}
	}	
    
	@Override
	public List<DataSourceDescription> exportAllDataSourceDescriptions() {
		
		trainingData.setDataInputType("trainingData");
		testingData.setDataInputType("testingData");
		validationData.setDataInputType("validationData");

		List<DataSourceDescription> slots = new ArrayList<DataSourceDescription>();
		slots.add(trainingData);
		slots.add(testingData);
		slots.add(validationData);
		
		return slots;
	}
	
	@Override
	public void importAllDataSourceDescriptions(List<DataSourceDescription> dataSourceDescriptions) {
				
		for (DataSourceDescription descriptionI : dataSourceDescriptions) {
						
			if (descriptionI.getDataInputType().equals("trainingData")) {
				trainingData = descriptionI;
				
			} else if (descriptionI.getDataInputType().equals("testingData")) {
				testingData = descriptionI;
				
			} else if (descriptionI.getDataInputType().equals("validationData")) {
				validationData = descriptionI;
				
			} else {
				throw new IllegalArgumentException(descriptionI.getDataOutputType());
			}
			
		}
		
	}
	

}

