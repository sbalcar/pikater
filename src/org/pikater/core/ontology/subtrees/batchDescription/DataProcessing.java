package org.pikater.core.ontology.subtrees.batchDescription;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.jfree.util.Log;
import org.pikater.core.ontology.subtrees.newOption.NewOptions;
import org.pikater.core.ontology.subtrees.newOption.base.NewOption;
import org.pikater.shared.experiment.UniversalOntology;
import org.pikater.shared.logging.core.ConsoleLogger;

/**
 * Created by Stepan on 20.4.14.
 */
public class DataProcessing implements IDataProvider {

	private static final long serialVersionUID = -2418323249803736416L;

	private int id = -1;
	private String agentType;
	
	private List<NewOption> options;
	private List<ErrorSourceDescription> errors;
	private List<DataSourceDescription> dataSources;

	public DataProcessing() {
		this.options = new ArrayList<NewOption>();
		this.errors = new ArrayList<ErrorSourceDescription>();
		this.dataSources = new ArrayList<DataSourceDescription>();
	}
			
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getAgentType() {
		return agentType;
	}
	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	public List<NewOption> getOptions() {
		return options;
	}
	public void setOptions(List<NewOption> options) {
		this.options = options;
	}

	public List<ErrorSourceDescription> getErrors() {
		return errors;
	}
	public void setErrors(List<ErrorSourceDescription> errors) {
		this.errors = errors;
	}

    public List<DataSourceDescription> getDataSources() {
        return dataSources;
    }
    public void setDataSources(List<DataSourceDescription> dataSources) {
        this.dataSources = dataSources;
    }
    public void addDataSources(DataSourceDescription dataSources) {
    	
    	if (dataSources == null) {
    		throw new IllegalArgumentException("Argument dataSources can't be null");
    	}
        this.dataSources.add(dataSources);
    }

	
	@Override
	public List<NewOption> exportAllOptions() {
		return this.options;
	}
	@Override
	public void importAllOptions(List<NewOption> options) {
		if (options == null) {
			throw new IllegalArgumentException("Argument options can't be null");
		}
		this.options = options;
	}
		
	@Override
	public List<DataSourceDescription> exportAllDataSourceDescriptions() {		
		return this.dataSources;
	}
	@Override
	public void importAllDataSourceDescriptions(
			List<DataSourceDescription> dataSourceDescriptions) {
		this.dataSources = dataSourceDescriptions;
	}
	
	@Override
	public List<ErrorSourceDescription> exportAllErrors() {
		return this.errors;
	}
	@Override
	public void importAllErrors(List<ErrorSourceDescription> errors) {
		this.errors = errors;
	}
	
	public int generateIDs(int lastUsedId)
	{
		if (getId() == -1)
		{
			setId(lastUsedId + 1);
		}
		return getId();
	}

	public UniversalOntology exportUniversalOntology() {
		
		UniversalOntology ontologyInfo = new UniversalOntology();
		ontologyInfo.setId(getId());
		ontologyInfo.setOntologyClass(getClass());
		if (getAgentType() == null) {
			ontologyInfo.setAgentClass(null);
		} else {
			ontologyInfo.setAgentClass(getAgentType());
		}
		ontologyInfo.setOptions(new NewOptions(exportAllOptions()));
		
		return ontologyInfo;
	}
	public static DataProcessing importUniversalOntology(UniversalOntology uOntology) {
		
		Constructor<?> cons = null;
		try {
			cons = uOntology.getOntologyClass().getConstructor();
		} catch (NoSuchMethodException e) {
			Log.error(e.getMessage(), e);
		} catch (SecurityException e) {
			Log.error(e.getMessage(), e);
		}
		
		Object object = null;
		try {
			object = cons.newInstance();
		} catch (InstantiationException e) {
			Log.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			Log.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			Log.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			Log.error(e.getMessage(), e);
		}
		
		DataProcessing dataProcess = (DataProcessing) object;
		dataProcess.setId(uOntology.getId());
		if (uOntology.getAgentClass() == null) {
			dataProcess.setAgentType(null);
		} else {
			dataProcess.setAgentType(uOntology.getAgentClass());	
		}
		dataProcess.importAllOptions(uOntology.getOptions().getOptions());
		
		return dataProcess;
	}
	
	@Override
	public boolean equalsElement(IComputationElement element) {
		
		return this.getId() == element.getId();
	}
	
	@Override
	public DataProcessing clone() throws CloneNotSupportedException
	{
		List<NewOption> allOptionsCloned = new ArrayList<NewOption>();
		for (NewOption optionI : exportAllOptions()) {
			allOptionsCloned.add(optionI.clone());
		}

		List<ErrorSourceDescription> errorsCloned = new ArrayList<ErrorSourceDescription>();
		for (ErrorSourceDescription errorI : exportAllErrors()) {
			errorsCloned.add(errorI.clone());
		}

		List<DataSourceDescription> dataSourceCloned = new ArrayList<DataSourceDescription>();
		for (DataSourceDescription dataSourceI : exportAllDataSourceDescriptions()) {
			dataSourceCloned.add(dataSourceI.clone());
		}
		
		DataProcessing dataProcessing = (DataProcessing) super.clone();
		dataProcessing.setId(this.id);
		dataProcessing.setAgentType(this.getAgentType());
		dataProcessing.importAllOptions(allOptionsCloned);
		dataProcessing.importAllErrors(errorsCloned);
		dataProcessing.importAllDataSourceDescriptions(dataSourceCloned);
		
		return dataProcessing;
	}


	public void cloneSources() {
		
		List<DataSourceDescription> dataSourceCloned = new ArrayList<DataSourceDescription>();
		for (DataSourceDescription dataSourceI : exportAllDataSourceDescriptions())
		{
			try
			{
				dataSourceCloned.add(dataSourceI.clone());
			}
			catch(CloneNotSupportedException e)
			{
				ConsoleLogger.logThrowable("Can not clone sources. Returning...", e);
				return;
			}
		}
		
		importAllDataSourceDescriptions(dataSourceCloned);
		
		List<ErrorSourceDescription> errorSourceCloned = new ArrayList<ErrorSourceDescription>();
		for (ErrorSourceDescription errorSourceI : exportAllErrors())
		{
			try
			{
				errorSourceCloned.add(errorSourceI.clone());
			}
			catch(CloneNotSupportedException e)
			{
				ConsoleLogger.logThrowable("Could not close sources. Returning...", e);
				return;
			}
		}
		
		importAllErrors(errorSourceCloned);
	}
	
	
}
