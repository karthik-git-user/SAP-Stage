/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticleb2caddon.renderer;

import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import com.sap.retail.commercesuite.saparticleb2caddon.model.ArticleComponentTabParagraphComponentModel;


/**
 * Renderer class for the article components tab view.
 *
 * The renderer overwrites the DefaultAddonCMSComponentRenderer. The getUIExperienceFolder() method is overwritten to
 * enable the correct determination of views of the responsive UI.
 *
 * @param <C> the the component to render
 */
public class ArticleComponentsRenderer<C extends ArticleComponentTabParagraphComponentModel> extends
		DefaultAddOnCMSComponentRenderer<C>
{
	private Map<UiExperienceLevel, String> uiExperienceViewFolderNames;



	/**
	 * Getter for uiExperienceViewFolderNames map.
	 *
	 * @return the uiExperienceViewFolderNames map
	 */
	public Map<UiExperienceLevel, String> getUiExperienceViewFolderNames()
	{
		return uiExperienceViewFolderNames;
	}

	/**
	 * Set the uiExperienceViewFolderNames map to the given map.
	 *
	 * @param uiExperienceViewFolderNames the uiExperienceViewFolderNames map to use
	 */
	public void setUiExperienceViewFolderNames(final Map<UiExperienceLevel, String> uiExperienceViewFolderNames)
	{
		this.uiExperienceViewFolderNames = uiExperienceViewFolderNames;
	}

	@Override
	protected String getUIExperienceFolder()
	{
		return StringUtils.lowerCase(getUiExperienceViewFolderNames().get(getUiExperienceService().getUiExperienceLevel()));
	}

}
