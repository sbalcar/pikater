package org.pikater.web.vaadin.gui.server.components.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.wcs.wcslib.vaadin.widget.multifileupload.receiver.UploadReceiver;

/**
 * As stated in the Javadoc of {@link IMyUploadReceiver}, this implementation
 * strives to replace the <a href="http://goo.gl/7XF84Z">original receiver
 * implementation</a> in Vaadin's 'MultiFileUpload' plugin to:
 * <ul>
 * <li> Give us more control over the construction of the server-side file.
 * <li> Unbind handling of the uploaded file from Vaadin (not to succumb to
 * request timeout).
 * <li> Give the handler more information about the file. Pass {@link File}
 * instead of {@link InputStream}.
 * </ul>
 * <font color="red">At the same time, doing so is a drawback for future Vaadin or plugin updates.
 * Newer versions (especially the plugin) may not support hacks like these.</font>
 * 
 * When (re-)implementing this class, it is advised to consult the 
 * <a href="http://goo.gl/piWOPJ">original UploadStatePanel implementation</a>.
 */
public class MyUploadReceiver implements UploadReceiver
{
	/**
	 * Original fields, kept intact.
	 */
	private static final String TEMPFILE_NAME_PREFIX = "upload_tmpfile_";
    private File file;
    
    //-------------------------------------------------------
    // ORIGINAL INTERFACE
    
    /**
     * Original implementation, kept intact.
     * Used to create an output stream to write pieces of uploaded file to.
     */
    @Override
	public OutputStream receiveUpload()
	{
    	try
    	{
    		if (file == null)
    		{
    			file = createTempFile();
    			file.deleteOnExit();
            }
            return new FileOutputStream(file);
        }
    	catch (final IOException e)
    	{
    		throw new RuntimeException(e);
    	}
	}
    
    /**
     * <ul>
     * <li>ORIGINAL USE-CASE:</br>
     * called to get a stream on the uploaded file and pass it to a 
     * {@link com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler UploadFinishedHandler}
     * before calling {@link #deleteTempFile()}.
     * <li>OUR USE-CASE:</br>
     * this method has to return null to unbind uploaded file handling from Vaadin. The original
     * implementation contains a safety null check and doesn't throw exceptions so it should be fine. 
     * </ul>
     */
    @Override
	public InputStream getStream()
	{
    	return null;
	}

    /**
     * <ul>
     * <li>ORIGINAL USE-CASE:</br>
     * Called if {@link #getStream()} returns a non-null stream or when the upload fails. 
     * <li>OUR USE-CASE:</br>
     * Called by the plugin, when the upload fails.
     * </ul>
     */
	@Override
	public void deleteTempFile()
	{
		if (file != null && file.exists())
		{
			file.delete();
			file = null;
        }
	}
	
	//-------------------------------------------------------
    // INTERFACE ADDED BY US
	
	/**
	 * Original implementation, made protected to allow overriding.
	 */
    protected File createTempFile()
    {
    	final String tempFileName = TEMPFILE_NAME_PREFIX + System.currentTimeMillis();
    	try
    	{
    		return File.createTempFile(tempFileName, null);
    	}
    	catch (IOException e)
    	{
    		throw new RuntimeException(e);
        }
    }
    
    /**
     * The method we are doing all this for. See this class's Javadoc.
     * @return
     */
	public File getUploadedFileHandler()
	{
		return file;
	}
}