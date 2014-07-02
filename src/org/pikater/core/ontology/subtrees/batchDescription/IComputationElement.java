package org.pikater.core.ontology.subtrees.batchDescription;

import java.util.List;

import org.pikater.core.ontology.subtrees.option.Option;
import org.pikater.shared.experiment.universalformat.UniversalOntology;

import jade.content.Concept;

/**
 * Created by Martin Pilat on 28.12.13.
 */
public interface IComputationElement extends Concept {
	
	public int getId();
	public void setId(int id);
	
	public List<Option> exportAllOptions();
	public void importAllOptions(List<Option> options);
	
	public List<ErrorDescription> exportAllErrors();
	public void importAllErrors(List<ErrorDescription> errors);
	
	public List<DataSourceDescription> exportAllDataSourceDescriptions();
	public void importAllDataSourceDescriptions(List<DataSourceDescription> dataSourceDescriptions);
	
	public UniversalOntology exportUniversalOntology();
}
