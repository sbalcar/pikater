package org.pikater.shared.database.views.jirka.abstractview.values;

import java.util.Set;

import org.pikater.shared.database.views.jirka.abstractview.AbstractTableDBView.ColumnType;

/**
 * A single value chosen from a set of values. Read-only or editable.
 */
public abstract class RepresentativeDBViewValue extends AbstractDBViewValue<String>
{
	private final Set<String> values;
	
	public RepresentativeDBViewValue(Set<String> values, String selectedValue, boolean readOnly)
	{
		super(ColumnType.REPRESENTATIVE, selectedValue, readOnly);
		this.values = values;
	}
	
	@Override
	public void setValue(String newValue) throws UnsupportedOperationException, IllegalArgumentException
	{
		if(values.contains(newValue))
		{
			super.setValue(newValue);
		}
		else
		{
			throw new IllegalArgumentException("The provided value does not belong to the set of allowed values.");
		}
	}
	
	public Set<String> getValues()
	{
		return values;
	}
}