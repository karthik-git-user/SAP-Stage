/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.dto;

public abstract class AbstractListItemDTO
{
	protected boolean selected;
	protected boolean customUnique;
	protected boolean autocreate;
	protected String description;
	protected String alias;

	public AbstractListItemDTO(final boolean selected, final boolean customUnique, final boolean autocreate)
	{
		this.selected = selected;
		this.customUnique = customUnique;
		this.autocreate = autocreate;
	}

	public boolean isCustomUnique()
	{
		return customUnique;
	}

	public void setCustomUnique(final boolean customUnique)
	{
		this.customUnique = customUnique;
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(final boolean selected)
	{
		this.selected = selected;
	}

	public boolean isAutocreate()
	{
		return autocreate;
	}

	public void setAutocreate(final boolean autocreate)
	{
		this.autocreate = autocreate;
	}

	public String getAlias()
	{
		return this.alias;
	}

	public abstract void setAlias(final String alias);

	public String getDescription()
	{
		return description;
	}
}
