package org.pikater.core.ontology.subtrees.newOption.restrictions;

import java.util.ArrayList;
import java.util.List;

import org.pikater.core.ontology.subtrees.newOption.base.ValueType;

public class TypeRestriction implements IRestriction
{
	private static final long serialVersionUID = -135700897678377163L;
	
	private List<ValueType> types;

	public TypeRestriction()
	{
		this.types = new ArrayList<ValueType>();
	}
	public TypeRestriction(List<ValueType> types)
	{
		this();
		setTypes(types);
	}
	
	public List<ValueType> getTypes()
	{
		return types;
	}
	public void setTypes(List<ValueType> types)
	{
		this.types.clear();
		this.types.addAll(types);
	}
	
	public void addType(ValueType type)
	{
		types.add(type);
	}
	public boolean isValidAgainst(Object obj)
	{
		return isValid() && (obj instanceof ValueType) && types.contains((ValueType) obj);
	}
	
	@Override
	public TypeRestriction clone()
	{
		List<ValueType> typesCopied = new ArrayList<ValueType>();
		for(ValueType type : types)
		{
			typesCopied.add(type.clone());
		}
		return new TypeRestriction(typesCopied);
	}
	@Override
	public boolean isValid()
	{
		return (types != null) && !types.isEmpty();
	}
}