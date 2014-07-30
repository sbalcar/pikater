package org.pikater.core.ontology.subtrees.batchDescription;

import jade.content.Concept;

/**
 * Created by Martin Pilat on 28.12.13.
 */
public class ErrorDescription implements Concept {

	private static final long serialVersionUID = 1229354484860699593L;
	
	private IErrorProvider provider;
    private String type;

    public IErrorProvider getProvider() {
        return provider;
    }

    public void setProvider(IErrorProvider provider) {
        this.provider = provider;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public ErrorDescription clone() {
    	
    	ErrorDescription description = new ErrorDescription();
    	description.setProvider(provider);
    	description.setType(getType());
    	return description;
    }
}
