package org.pikater.web.visualisation.definition.result;

import org.pikater.web.visualisation.definition.AttrMapping;
import org.pikater.web.visualisation.definition.ImageType;

public class DSVisOneSubresult extends AbstractDSVisSubresult
{
	private final AttrMapping attrInfo;

	public DSVisOneSubresult(AttrMapping attrInfo, ImageType imageType)
	{
		super(imageType);
		
		this.attrInfo = attrInfo;
	}

	public AttrMapping getAttrInfo()
	{
		return attrInfo;
	}
}