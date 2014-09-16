package org.pikater.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.pikater.core.agents.gateway.WebToCoreEntryPoint;
import org.pikater.shared.logging.web.GeneralPikaterLogger;
import org.pikater.shared.logging.web.PikaterLogger;
import org.pikater.shared.quartz.PikaterJobScheduler;
import org.pikater.shared.util.IOUtils;
import org.pikater.web.config.WebAppConfiguration;

@WebListener
public class StartupAndQuitListener implements ServletContextListener
{
	/**
	 * Application startup event.
	 */
	@Override
	public void contextInitialized(ServletContextEvent event)
	{
		/*
	    // first let's see whether our app is clustered and replicates the servlet context
	    // event.getServletContext().setAttribute("config", this);
	 	ServletContext ctx = event.getServletContext();
	 	if(ctx.getAttribute("storage") == null) // servlet context is replicated and was successfully loaded
	 	{
	 		// create new 
	 		ctx.setAttribute("storage", SharedStorage.getInstance());
	 	}
	 	else
	 	{
	 		System.out.println("storage was already set");
	 	}
	 	*/
		
		// this must be first
		PikaterLogger.setLogger(new GeneralPikaterLogger()
		{
			private final Logger innerLogger = Logger.getLogger("log4j");

			@Override
			public void logThrowable(String problemDescription, Throwable t)
			{
				innerLogger.log(Level.SEVERE, "exception occured: " + problemDescription + "\n" + throwableToStackTrace(t));
			}

			@Override
			public void log(Level logLevel, String message)
			{
				innerLogger.log(logLevel, message);
			}
		});
		
		try
		{
			/*
			 * GENERAL NOTE: don't alter the code order. There may be some dependencies.
			 */
			
			announceCheckOrAction("setting initial application state & essential variables");
			WebAppConfiguration.setContext(event.getServletContext());
			IOUtils.setAbsoluteBaseAppPath(event.getServletContext().getRealPath("/"));
			
			announceCheckOrAction("initializing task scheduler");
			PikaterJobScheduler.initStaticScheduler(IOUtils.getAbsolutePath(IOUtils.getAbsoluteWEBINFCLASSESPath(), PikaterJobScheduler.class));
			
			if(WebAppConfiguration.isCoreEnabled())
			{
				announceCheckOrAction("checking core connection");
				try
				{
					WebToCoreEntryPoint.checkLocalConnection();
				}
				catch(Exception e)
				{
					throw new IllegalStateException("Could not establish connection with pikater core.", e);
				}
			}
			
			announceCheckOrAction("database connection");
			/*
			 * // TODO: doesn't work in context listeners for some reason
			if(!ServerConfigurationInterface.avoidUsingDBForNow() && !MyPGConnection.isConnectionToCurrentPGDBEstablished())
			{
				Class.forName("org.postgresql.Driver");
				DriverManager.registerDriver(new Driver());
				throw new IllegalStateException("Could not connect to database.");
			}
			*/

			PikaterLogger.log(Level.INFO, "\n**********************************************************\n"
					+ "APPLICATION SETUP SUCCESSFULLY FINISHED\n"
					+ "**********************************************************");
		}
		catch(Exception e)
		{
			PikaterLogger.log(Level.SEVERE, "APPLICATION LAUNCH REQUIREMENTS WERE NOT MET. ABORTED.");
			throw new IllegalStateException(e); // will be printed just afterwards
		}
	}
	
	/**
	 * Application shutdown	event.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
		PikaterJobScheduler.shutdownStaticScheduler();
	}
	
	//---------------------------------------------------------------
	// PRIVATE INTERFACE
	
	private void announceCheckOrAction(String message)
	{
		PikaterLogger.log(Level.INFO, "**********************************************************\n"
				+ "CHECKING REQUIREMENT / DOING ACTION: " + message);
	}
}