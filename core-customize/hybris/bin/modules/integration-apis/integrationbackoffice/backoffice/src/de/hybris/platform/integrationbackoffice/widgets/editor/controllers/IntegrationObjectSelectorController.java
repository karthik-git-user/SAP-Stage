/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.editor.controllers;

import com.hybris.cockpitng.annotations.GlobalCockpitEvent;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.components.Actions;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.util.DefaultWidgetController;

import de.hybris.platform.integrationbackoffice.services.ReadService;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.createComboItem;

public final class IntegrationObjectSelectorController extends DefaultWidgetController
{

	@Wire
	private Actions actions;
	@WireVariable
	private transient ReadService readService;
	@WireVariable
	private transient CockpitEventQueue cockpitEventQueue;

	private Combobox integrationObjectComboBox;

	private static final String SETTING_ACTIONS_SLOT = "actions";
	private static final String MODEL_KEY_CURRENT_OBJECT = "currentObject";

	@Override
	public void initialize(final Component component)
	{
		super.initialize(component);
		loadIntegrationObjects();
		initActions();
	}

	@GlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECT_CREATED_EVENT, scope = CockpitEvent.SESSION)
	public void handleIntegrationObjectCreatedEvent(final CockpitEvent event)
	{
		if (event.getDataAsCollection().stream().anyMatch(IntegrationObjectModel.class::isInstance))
		{
			loadIntegrationObjects();
			setSelectedIntegrationObject((IntegrationObjectModel) event.getData());
			storeIntegrationObjectInModel(event.getData());
		}
	}

	@GlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, scope = CockpitEvent.SESSION)
	public void handleIntegrationObjectUpdatedEvent(final CockpitEvent event)
	{
		if (event.getDataAsCollection().stream().anyMatch(IntegrationObjectModel.class::isInstance))
		{
			loadIntegrationObjects();
			setSelectedIntegrationObject((IntegrationObjectModel) event.getData());
			storeIntegrationObjectInModel(event.getData());
		}
	}

	@GlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
	public void handleIntegrationObjectDeletedEvent(final CockpitEvent event)
	{
		if (event.getDataAsCollection().stream().anyMatch(IntegrationObjectModel.class::isInstance))
		{
			loadIntegrationObjects();
			integrationObjectComboBox.setValue(null);
			storeIntegrationObjectInModel((Object) null);
		}
	}

	@SocketEvent(socketId = "receiveRefresh")
	public void refreshButtonOnClick()
	{
		loadIntegrationObjects();
	}

	@ViewEvent(componentID = "integrationObjectComboBox", eventName = Events.ON_CHANGE)
	public void integrationObjectComboBoxOnChange()
	{
		if (integrationObjectComboBox.getSelectedItem() != null)
		{
			sendOutput("comboBoxOnChange", integrationObjectComboBox.getSelectedItem().getValue());
			sendOutput("isObjectSelected", true);
			storeIntegrationObjectInModel((Object) integrationObjectComboBox.getSelectedItem().getValue());
		}
	}

	private void initActions()
	{
		actions.setConfig(String.format("component=%s", getWidgetSettings().getString(SETTING_ACTIONS_SLOT)));
		actions.reload();
	}

	private void loadIntegrationObjects()
	{
		integrationObjectComboBox.getItems().clear();
		readService.getIntegrationObjectModels().forEach(integrationObject ->
				integrationObjectComboBox.appendChild(createComboItem(integrationObject.getCode(), integrationObject)));
	}

	private void setSelectedIntegrationObject(final IntegrationObjectModel integrationObject)
	{
		for (final Comboitem comboitem : integrationObjectComboBox.getItems())
		{
			if (comboitem.getValue().equals(integrationObject))
			{
				integrationObjectComboBox.setSelectedIndex(comboitem.getIndex());
				break;
			}
		}
	}

	private void storeIntegrationObjectInModel(Object integrationObject)
	{
		if (integrationObject == null)
		{
			storeIntegrationObjectInModel((String) null);
		}
		else
		{
			IntegrationObjectModel integrationObjectModel = (IntegrationObjectModel) integrationObject;
			storeIntegrationObjectInModel(integrationObjectModel.getRootItem().getType().getCode());
		}
	}

	private void storeIntegrationObjectInModel(String type)
	{
		getModel().setValue(MODEL_KEY_CURRENT_OBJECT, type);
	}

	@SocketEvent(socketId = "receiveClickedItem")
	public void getSelected(final String selectedComposedType)
	{
		storeIntegrationObjectInModel(selectedComposedType);
	}
}