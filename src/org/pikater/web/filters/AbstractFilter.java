package org.pikater.web.filters;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pikater.shared.logging.PikaterLogger;
import org.pikater.web.request.HttpRequestUtils;

public abstract class AbstractFilter implements Filter
{
	private boolean DEBUG;
	
	//----------------------------------------------------------
	// SINGLE (AND SIMPLE) IMPLEMENTATION OF SOME INHERITED ROUTINES
	
	@Override
	public void init(FilterConfig arg0) throws ServletException
	{
		DEBUG = Boolean.parseBoolean(arg0.getServletContext().getInitParameter("DEBUG"));
	}
	
	@Override
	public void destroy()
	{
	}
	
	//----------------------------------------------------------
	// REQUEST DISPATCHING ROUTINES
	
	/**
	 * Client-side redirect. Affects the client browser's address bar.
	 * @param servletResponse
	 * @param redirectString
	 * @throws IOException
	 * @throws ServletException
	 */
	protected void clientRedirect(ServletResponse servletResponse, String redirectString) throws IOException, ServletException
	{
		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
		if(redirectString.startsWith("/"))
		{
			redirectString = redirectString.substring(1);
		}
		httpResponse.sendRedirect(redirectString);
	}
	
	/**
	 * Deprecated: debug first - see client reditect above
	 * 
	 * Server-side forward. Doesn't affect the client browser's address bar.
	 * @param servletRequest
	 * @param servletResponse
	 * @param forwardString
	 * @throws IOException
	 * @throws ServletException
	 */
	@Deprecated
	protected void serverForward(ServletRequest servletRequest, ServletResponse servletResponse, String forwardString) throws IOException, ServletException
	{
		servletRequest.getRequestDispatcher(forwardString).forward(servletRequest, servletResponse);
	}
	
	//----------------------------------------------------------
	// PROGRAMMATIC HELPER ROUTINES
	
	protected boolean isServletPathDefined(HttpServletRequest httpRequest)
	{
		return HttpRequestUtils.getServletPathWhetherMappedOrNot(httpRequest) != null;
	}
	
	//----------------------------------------------------------
	// DEBUG ROUTINES
	
	protected boolean isDebugMode()
	{
		return DEBUG;
	}
	
	protected void printRequestComponents(String filterName, HttpServletRequest httpRequest)
	{
		PikaterLogger.log(Level.WARNING, String.format("Filter '%s' intercepted request:\n"
				+ "Full URL: %s\n"
				+ "Derived servlet path: %s\n",
				filterName,
				HttpRequestUtils.getFullURL(httpRequest),
				HttpRequestUtils.getServletPathWhetherMappedOrNot(httpRequest)
		));
	}
}
