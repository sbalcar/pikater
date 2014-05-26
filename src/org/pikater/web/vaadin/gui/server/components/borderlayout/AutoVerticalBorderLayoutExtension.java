package org.pikater.web.vaadin.gui.server.components.borderlayout;

import org.pikater.web.vaadin.gui.client.extensions.AutoVerticalBorderLayoutExtensionClientRpc;
import org.pikater.web.vaadin.gui.client.extensions.AutoVerticalBorderLayoutExtensionServerRpc;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.CustomLayout;

public class AutoVerticalBorderLayoutExtension extends AbstractExtension
{
	private static final long serialVersionUID = 8278201529558658998L;
	
	public AutoVerticalBorderLayoutExtension()
	{
		registerRpc(new AutoVerticalBorderLayoutExtensionServerRpc()
		{
		});
	}
	
	public AutoVerticalBorderLayoutExtensionClientRpc getClientRPC()
	{
		return getRpcProxy(AutoVerticalBorderLayoutExtensionClientRpc.class);
	}
	
	/**
	 * Exposing the inherited API.
	 * @param mainUI
	 */
	public void extend(CustomLayout layout)
    {
        super.extend(layout);
    }
}