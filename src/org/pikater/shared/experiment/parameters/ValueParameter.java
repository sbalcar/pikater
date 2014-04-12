package org.pikater.shared.experiment.parameters;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ValueParameter")
public class ValueParameter<T> extends AbstractParameter
{
	private T value;

	public ValueParameter(T value)
	{
		super();
		
		this.value = value;
	}
	
	public void setValue(T value)
	{
    	this.value = value;
    }
    
	public T getValue()
	{
    	return this.value;
    }
}
