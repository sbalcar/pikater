package org.pikater.web.config;

import javax.servlet.ServletContext;

public class ServerConfigurationInterface
{
	public enum ServerConfItem
	{
		CONTEXT,
		CONFIG,
		JADE_TOPOLOGIES
	};
	
	/**
	 * Unique keys to use for the attributes to store.
	 */
	private static ServerConfiguration config = null;
	private static JadeTopologies jadeTopologies = null;
	private static ServletContext context = null;
	
	// **************************************************************************************************
	// PUBLIC INTERFACE
	
	public static void setField(ServerConfItem item, Object value)
	{
		switch (item)
		{
			case CONTEXT:
				if(context != null)
				{
					throw new IllegalStateException();
				}
				else
				{
					context = (ServletContext) value;
				}
				break;
			case CONFIG:
				if(config != null)
				{
					throw new IllegalStateException();
				}
				else
				{
					config = (ServerConfiguration) value;
				}
				break;
			case JADE_TOPOLOGIES:
				if(jadeTopologies != null)
				{
					throw new IllegalStateException();
				}
				else
				{
					jadeTopologies = (JadeTopologies) value;
				}
				break;
			default:
				throw new IllegalArgumentException();
		}
	}
	
	public static ServletContext getContext()
	{
		return context;
	}
	
	public static ServerConfiguration getConfig()
	{
		return config;
	}
	
	public static JadeTopologies getJadeTopologies()
	{
		return jadeTopologies;
	}

	public static boolean isEverythingOK()
	{
		return (getContext() != null) && 
				(getConfig() != null) && getConfig().isValid() && 
				(getJadeTopologies() != null) && (getJadeTopologies().getConnectedTopologies().size() > 1); 
	}
}