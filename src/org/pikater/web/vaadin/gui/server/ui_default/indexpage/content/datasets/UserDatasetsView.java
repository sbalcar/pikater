package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.datasets;

import org.pikater.shared.database.views.tableview.datasets.DataSetTableDBView;
import org.pikater.web.vaadin.ManageAuth;
import org.pikater.web.vaadin.ManageSession;
import org.pikater.web.vaadin.ManageUserUploads;
import org.pikater.web.vaadin.gui.server.components.dbviews.DatasetDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;
import org.pikater.web.vaadin.gui.server.components.upload.MyUploadStateWindow;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.datasets.upload.DatasetUploadWizard;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import de.steinwedel.messagebox.MessageBox;

public class UserDatasetsView extends DatasetsView
{
	private static final long serialVersionUID = 5568928739598297585L;
	
	/**
	 * This should be a constant reference across all UI instances.
	 */
	private ManageUserUploads uploadManager;
	
	/**
	 * One upload manager per UI instance.
	 */
	private MyUploadStateWindow uploadInfoProvider;
	
	public UserDatasetsView()
	{
		super();
		
		getMainLayout().addCustomActionComponent(new Button("Upload a new dataset", new Button.ClickListener()
		{
			private static final long serialVersionUID = -1045335713385385849L;

			@Override
			public void buttonClick(ClickEvent event)
			{
				MessageBox mb = GeneralDialogs.wizardDialog("Dataset upload guide", new DatasetUploadWizard(uploadManager, uploadInfoProvider));
				mb.setWidth("600px");
				mb.setHeight("500px");
			}
		}));
	}
	
	@Override
	public void enter(ViewChangeEvent event)
	{
		this.uploadManager = (ManageUserUploads) ManageSession.getAttribute(VaadinSession.getCurrent(), ManageSession.key_userUploads);
		this.uploadInfoProvider = uploadManager.createUploadInfoProvider();
			
		// always call these last, when you're absolutely ready to display the content
		getMainLayout().setView(new DatasetDBViewRoot<DataSetTableDBView>(new DataSetTableDBView(
				ManageAuth.getUserEntity(VaadinSession.getCurrent()
		))));
		super.finishInit(); // don't forget to!
	}
	
	@Override
	public boolean isReadyToClose()
	{
		return !uploadInfoProvider.isAFileBeingUploaded(); // only inspect uploads in this UI instance
	}
	
	@Override
	public String getCloseMessage()
	{
		return "Uploads will be interrupted. Continue?";
	}
	
	@Override
	public void beforeClose()
	{
		super.beforeClose();
		this.uploadInfoProvider.interruptAll(); // only interrupt uploads originated in this UI instance
	}
}