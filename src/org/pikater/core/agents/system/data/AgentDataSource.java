package org.pikater.core.agents.system.data;

import jade.content.onto.Ontology;
import jade.util.leap.Serializable;

import org.apache.commons.io.FileUtils;
import org.pikater.core.agents.PikaterAgent;
import org.pikater.core.ontology.DataOntology;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Kuba
 * Date: 2.5.2014
 * Time: 11:38
 * AgentData source maintains a list of files in pathToLocals directory
 * and provides a path to datasource on request. If the datasource is
 * not in the local the agent tries to get it from other containers(TODO).
 */
public class AgentDataSource extends PikaterAgent {

	private static final long serialVersionUID = -2175414070329509621L;

	private static final String PATH_TO_LOCAL_SOURCES = "core" +
			System.getProperty("file.separator") +
			"data" + System.getProperty("file.separator") +
			"dataSources" + System.getProperty("file.separator");
	
    private Set<String> ownedDataSources = new HashSet<String>();
    public static final String SERVICE_TYPE = "AgentDataSource";

	/**
	 * Get ontologies which is using this agent
	 */
	@Override
	public List<Ontology> getOntologies() {
		
		List<Ontology> ontologies = new ArrayList<Ontology>();
		ontologies.add(DataOntology.getInstance());
		
		return ontologies;
	}
	
    //TODO: move to more appropriate location
    public static  void SerializeFile(Serializable toSerialize,
    		String fileName) throws IOException {
    	
        FileOutputStream fileOut =
        		new FileOutputStream(PATH_TO_LOCAL_SOURCES+fileName);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(toSerialize);
        out.close();
        fileOut.close();
    }

	/**
	 * Agent setup
	 */
    protected void setup() {
        try {
            initDefault();
            registerWithDF(SERVICE_TYPE);
            cleanup();
        } catch (Exception e) {
                    logSevere("Failed to complete setup");
        }
    }

    public String getPathToDataSource(String dataSourceName) {
    	
      if (ownedDataSources.contains(dataSourceName)) {
              return PATH_TO_LOCAL_SOURCES+dataSourceName;
      } else {
          throw new IllegalArgumentException();
          //TODO: obtain from other containers
      }
    }

    public void addDataSourceToOwned(String dataSourceName) {
          ownedDataSources.add(dataSourceName);
    }

    private void cleanup() throws IOException {
        logInfo("Cleaning local datasources directory "
        		+ "from the datasources of the previous run");
        
        File localDataSourcesDirectory = new File(PATH_TO_LOCAL_SOURCES);
        FileUtils.cleanDirectory(localDataSourcesDirectory);
    }

}

