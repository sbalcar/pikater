package org.pikater.web.config;

import java.io.File;
import java.util.Properties;

import org.pikater.shared.PropertiesHandler;

public class ServerConfiguration extends PropertiesHandler
{
	/*
	 * Property strings defined in the application configuration properties file. Have to be kept in sync :).
	 */
	
	private static final String property_allowDefaultAdminAccount = "allowDefaultAdminAccount";
	private static final String property_forceUseTopologies = "forceUseTopologies";
	private static final String property_forceLeaveTopologies = "forceLeaveTopologies";
	private static final String property_dummyBoxes = "dummyBoxes";
	
	/*
	 * Variables to be parsed from the configuration file.
	 */
	
	public final boolean defaultAdminAccountAllowed;
	
	public final String forceUseTopologies;
	public final String forceLeaveTopologies;
	
	public final boolean useDummyBoxes;
	
	public ServerConfiguration(File resource)
	{
		Properties prop = openProperties(resource);
		if(prop != null)
		{
			defaultAdminAccountAllowed = new Boolean(getProperty(prop, property_allowDefaultAdminAccount));
			forceUseTopologies = getProperty(prop, property_forceUseTopologies);
			forceLeaveTopologies = getProperty(prop, property_forceLeaveTopologies);
			useDummyBoxes = new Boolean(getProperty(prop, property_dummyBoxes));
		}
		else
		{
			defaultAdminAccountAllowed = false;
			forceUseTopologies = null;
			forceLeaveTopologies = null;
			useDummyBoxes = false;
		}
	}
	
	// **********************************************************************************************
	// PUBLIC INTERFACE
	
	public boolean isValid()
	{
		return true;
	}
}