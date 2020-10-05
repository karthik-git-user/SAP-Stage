/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.editor.controllers;

import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorConstants.ENABLE_SAVE_BUTTON_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorTrimmer.trimMap;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.compileDuplicationMap;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.convertIntegrationObjectToDTOMap;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.createListItem;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.createTreeItem;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.findInTreechildren;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.getListItemStructureType;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.getReferenceClassificationAttributes;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.getStructuredAttributes;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.isClassificationAttributePresent;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.markRowsWithDuplicateNames;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.renameTreeitem;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.updateDTOs;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorValidator.validateDefinitions;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorValidator.validateHasKey;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorValidator.validateHasNoDuplicateAttributeNames;
import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.IntegrationObjectRootUtils.resolveIntegrationObjectRoot;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.constants.IntegrationbackofficeConstants;
import de.hybris.platform.integrationbackoffice.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.dto.ListItemStructureType;
import de.hybris.platform.integrationbackoffice.services.ReadService;
import de.hybris.platform.integrationbackoffice.services.WriteService;
import de.hybris.platform.integrationbackoffice.utility.ItemTypeMatchSelector;
import de.hybris.platform.integrationbackoffice.widgets.editor.data.IntegrationFilterState;
import de.hybris.platform.integrationbackoffice.widgets.editor.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.editor.data.TreeNodeData;
import de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorAccessRights;
import de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorAttributesFilteringService;
import de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorBlacklists;
import de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorConstants;
import de.hybris.platform.integrationbackoffice.widgets.modals.builders.AuditReportBuilder;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.CreateIntegrationObjectModalData;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.RenameAttributeModalData;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.SelectedClassificationAttributesData;
import de.hybris.platform.integrationbackoffice.widgets.providers.ClassificationAttributeQualifierProvider;
import de.hybris.platform.integrationservices.enums.ItemTypeMatchEnum;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.Log;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.assertj.core.util.Arrays;
import org.slf4j.Logger;
import org.zkoss.lang.Strings;
import org.zkoss.zhtml.Button;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

import com.hybris.cockpitng.annotations.GlobalCockpitEvent;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.util.DefaultWidgetController;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;

/**
 * Controls the functionality of the editor
 */
public final class IntegrationObjectEditorController extends DefaultWidgetController
{
	private static final Logger LOG = Log.getLogger(IntegrationObjectEditorController.class);
	private static final int MAX_DEPTH = 5;
	private static final String ASCENDING = "ascending";
	private static final String DESCENDING = "descending";
	@WireVariable
	private transient WriteService writeService;
	@WireVariable
	private transient ReadService readService;
	@WireVariable
	private transient EditorAttributesFilteringService editorAttrFilterService;
	@WireVariable
	private transient CockpitEventQueue cockpitEventQueue;
	@WireVariable
	private transient NotificationService notificationService;
	@WireVariable
	private transient EditorAccessRights editorAccessRights;
	@WireVariable
	private transient AuditReportBuilder auditReportBuilder;
	@WireVariable
	private transient ClassificationAttributeQualifierProvider classificationAttributeQualifierProvider;
	@Resource
	private transient ItemTypeMatchSelector itemTypeMatchSelector;

	private Tree composedTypeTree;
	private Listbox attributesListBox;
	private Listheader attributeNameListheader;
	private Listheader descriptionListheader;
	private List<String> attributeMenuPopupLabels;
	private Deque<ComposedTypeModel> ancestors;
	private boolean editModeFlag;
	private transient IntegrationFilterState filterState = IntegrationFilterState.SHOW_ALL;
	private transient Map<ComposedTypeModel, List<AbstractListItemDTO>> currentAttributesMap;
	private transient Map<ComposedTypeModel, ItemTypeMatchEnum> itemTypeMatchMap = new HashMap<>();
	private transient Set<SubtypeData> subtypeDataSet;
	private transient boolean isModified;
	private IntegrationObjectModel selectedIntegrationObject;
	private ComposedTypeModel selectedComposedType;
	private transient Map<ComposedTypeModel, Map<String, List<AbstractListItemDTO>>> attributeDuplicationMap;

	@Override
	public void initialize(final Component component)
	{
		super.initialize(component);
		setEditModeFlag(editorAccessRights.isUserAdmin());
		setModified(false);
		attributeMenuPopupLabels = new ArrayList<>();
		attributeMenuPopupLabels.add(getLabel("integrationbackoffice.editMode.menuItem.viewDetails"));
		attributeMenuPopupLabels.add(getLabel("integrationbackoffice.editMode.menuItem.renameAttribute"));
		attributeMenuPopupLabels.add(getLabel("integrationbackoffice.editMode.menuItem.retypeAttribute"));
	}

	///////////////////////////////////
	//   GLOBAL ACCESSORS/MUTATORS   //
	///////////////////////////////////

	public void setReadService(final ReadService readService)
	{
		this.readService = readService;
	}

	public Tree getComposedTypeTree()
	{
		return composedTypeTree;
	}

	public void setComposedTypeTree(final Tree composedTypeTree)
	{
		this.composedTypeTree = composedTypeTree;
	}

	public Listbox getAttributesListBox()
	{
		return attributesListBox;
	}

	public void setAttributesListBox(final Listbox attributesListBox)
	{
		this.attributesListBox = attributesListBox;
	}

	public Deque<ComposedTypeModel> getAncestors()
	{
		return ancestors;
	}

	public void setAncestors(final Deque<ComposedTypeModel> ancestors)
	{
		this.ancestors = ancestors;
	}

	public boolean getEditModeFlag()
	{
		return editModeFlag;
	}

	public void setEditModeFlag(final boolean editModeFlag)
	{
		this.editModeFlag = editModeFlag;
	}

	public IntegrationFilterState getFilterState()
	{
		return filterState;
	}

	public void setFilterState(final IntegrationFilterState state)
	{
		filterState = state;
	}

	public Map<ComposedTypeModel, List<AbstractListItemDTO>> getCurrentAttributesMap()
	{
		return currentAttributesMap;
	}

	public void setCurrentAttributesMap(final Map<ComposedTypeModel, List<AbstractListItemDTO>> currentAttributesMap)
	{
		this.currentAttributesMap = currentAttributesMap;
	}

	public Map<ComposedTypeModel, ItemTypeMatchEnum> getItemTypeMatchMap()
	{
		return itemTypeMatchMap;
	}

	public void setItemTypeMatchMap(final Map<ComposedTypeModel, ItemTypeMatchEnum> itemTypeMatchMap)
	{
		this.itemTypeMatchMap = itemTypeMatchMap;
	}

	public Set<SubtypeData> getSubtypeDataSet()
	{
		return subtypeDataSet;
	}

	public void setSubtypeDataSet(final Set<SubtypeData> subtypeDataSet)
	{
		this.subtypeDataSet = subtypeDataSet;
	}

	public boolean isModified()
	{
		return isModified;
	}

	public void setModified(final boolean modified)
	{
		isModified = modified;
	}

	public IntegrationObjectModel getSelectedIntegrationObject()
	{
		return selectedIntegrationObject;
	}

	public void setSelectedIntegrationObject(final IntegrationObjectModel integrationObject)
	{
		selectedIntegrationObject = integrationObject;
	}

	public ComposedTypeModel getSelectedComposedType()
	{
		return selectedComposedType;
	}

	protected NotificationService getNotificationService()
	{
		return notificationService;
	}

	public Map<ComposedTypeModel, Map<String, List<AbstractListItemDTO>>> getAttributeDuplicationMap()
	{
		return attributeDuplicationMap;
	}

	public AuditReportBuilder getDefaultAuditReportBuilder()
	{
		return this.auditReportBuilder;
	}

	public void setDefaultAuditReportBuilder(final AuditReportBuilder auditReportBuilder)
	{
		this.auditReportBuilder = auditReportBuilder;
	}

	///////////////////////////////
	//   GLOBAL COCKPIT EVENTS   //
	///////////////////////////////

	@GlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
	public void handleIntegrationObjectDeletedEvent(final CockpitEvent event)
	{
		if (event.getDataAsCollection().stream().anyMatch(IntegrationObjectModel.class::isInstance))
		{
			clearSelectedIntegrationObject();
		}
	}

	/////////////////////
	//   VIEW EVENTS   //
	/////////////////////

	@ViewEvent(componentID = "composedTypeTree", eventName = Events.ON_SELECT)
	public void composedTypeTreeOnSelect()
	{
		final TreeNodeData tnd = getComposedTypeTree().getSelectedItem().getValue();
		selectedComposedType = tnd.getComposedTypeModel();
		sendOutput("sendClickedItem", selectedComposedType.getCode());
		populateListBox(selectedComposedType);
	}

	///////////////////////
	//   SOCKET EVENTS   //
	///////////////////////

	@SocketEvent(socketId = "receiveIntegrationObjectComboBox")
	public void loadIntegrationObject(final IntegrationObjectModel integrationObject)
	{
		setSelectedIntegrationObject(resolveIntegrationObjectRoot(integrationObject));
		attributeDuplicationMap = new HashMap<>();
		final IntegrationObjectItemModel root = selectedIntegrationObject.getRootItem();
		if (root != null)
		{
			final Map<ComposedTypeModel, List<AbstractListItemDTO>> convertedMap = convertIntegrationObjectToDTOMap(readService,
					selectedIntegrationObject);
			compileSubtypeDataSet(convertedMap);
			createTree(root.getType(), convertedMap);
			final Map<ComposedTypeModel, List<AbstractListItemDTO>> trimmedMap = trimMap(readService, getCurrentAttributesMap(),
					getComposedTypeTree(), false);
			if (!("").equals(validateHasKey(trimmedMap)))
			{
				showObjectLoadedFurtherConfigurationMessage();
			}
		}
		else
		{
			clearTree();
			getNotificationService().notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.WARNING, getLabel("integrationbackoffice.editMode.warning.msg.invalidObjectLoaded"));
		}
		setModified(false);
	}

	@SocketEvent(socketId = "createIntegrationObjectEvent")
	public void createNewIntegrationObject(final CreateIntegrationObjectModalData data)
	{
		setModified(false);
		attributeDuplicationMap = new HashMap<>();
		setFilterState(IntegrationFilterState.SHOW_ALL);
		setSubtypeDataSet(new HashSet<>());
		sendOutput("filterStateChangeOutput", IntegrationFilterState.SHOW_ALL);

		createTree(data.getComposedTypeModel(), Collections.emptyMap());
		final Map<ComposedTypeModel, List<AbstractListItemDTO>> trimmedMap = createIntegrationObject(data);

		if (!("").equals(validateHasKey(trimmedMap)))
		{
			getNotificationService().notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.WARNING,
					getLabel("integrationbackoffice.editMode.warning.msg.serviceCreatedNeedsFurtherConfig"));
		}
		else
		{
			getNotificationService().notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.SUCCESS, getLabel("integrationbackoffice.editMode.info.msg.serviceCreated"));
		}
	}

	@SocketEvent(socketId = "saveEvent")
	public void updateIntegrationObject(final String message)
	{
		final Map<ComposedTypeModel, List<AbstractListItemDTO>> trimmedAttributesMap = trimMap(readService,
				getCurrentAttributesMap(), getComposedTypeTree(), false);
		if (validation(trimmedAttributesMap))
		{
			setItemTypeMatchMap(getItemTypeMatchForIntegrationObjectItem(selectedIntegrationObject));
			final IntegrationObjectModel ioModel = setItemTypeMatchForIntegrationObject(
					writeService.createDefinitions(selectedIntegrationObject, trimmedAttributesMap,
							selectedIntegrationObject.getRootItem().getCode()));
			getItemTypeMatchMap().clear();
			if (selectedIntegrationObject.getRootItem() != null)
			{
				persistenceSetup(ioModel);
			}
			else
			{
				Messagebox.show(getLabel("integrationbackoffice.editMode.warning.msg.saveConfirmation"),
						getLabel("integrationbackoffice.editMode.warning.title.saveConfirmation"), new Messagebox.Button[]
								{ Messagebox.Button.OK, Messagebox.Button.CANCEL }, new String[]
								{ getLabel("integrationbackoffice.editMode.button.saveDefinition") }, null, null, clickEvent -> {
							if (clickEvent.getButton() == Messagebox.Button.OK)
							{
								persistenceSetup(ioModel);
								setModified(true);
							}
						});
			}
		}
	}

	@SocketEvent(socketId = "refreshEvent")
	public void refreshButtonOnClick(final String message)
	{
		if (selectedIntegrationObject != null)
		{
			loadIntegrationObject(selectedIntegrationObject);
			setModified(false);
			attributeDuplicationMap.clear();
		}
	}

	@SocketEvent(socketId = "receiveDelete")
	public void deleteActionOnPerform()
	{
		if (selectedIntegrationObject != null)
		{
			Messagebox.show(
					getLabel("integrationbackoffice.editMode.info.msg.deleteConfirmation",
							Arrays.array(selectedIntegrationObject.getCode())),
					getLabel("integrationbackoffice.editMode.info.title.deleteConfirmation"), new Messagebox.Button[]
							{ Messagebox.Button.OK, Messagebox.Button.CANCEL }, null, null, null, clickEvent -> {
						if (clickEvent.getButton() == Messagebox.Button.OK)
						{
							getNotificationService().notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
									NotificationEvent.Level.SUCCESS, getLabel("integrationbackoffice.editMode.info.msg.delete"));
							writeService.deleteIntegrationObject(selectedIntegrationObject);
							cockpitEventQueue.publishEvent(
									new DefaultCockpitEvent(ObjectFacade.OBJECTS_DELETED_EVENT, selectedIntegrationObject, null));
						}
					});
		}
		else
		{
			showNoServiceSelectedMessage();
		}
	}

	@SocketEvent(socketId = "metadataModalEvent")
	public void metaDataModelRequestHandler(final String message)
	{
		sendCurrentIntegrationObject("openMetadataViewer");
	}

	private void sendCurrentIntegrationObject(final String message)
	{
		if (selectedIntegrationObject != null && selectedIntegrationObject.getRootItem() != null)
		{
			final Map<ComposedTypeModel, List<AbstractListItemDTO>> trimmedMap = trimMap(readService, getCurrentAttributesMap(),
					getComposedTypeTree(), false);
			if (!("").equals(validateHasKey(trimmedMap)))
			{
				showObjectLoadedFurtherConfigurationMessage();
			}
			else
			{
				sendOutput(message, selectedIntegrationObject);
			}
		}
		else
		{
			showNoServiceSelectedMessage();
		}
	}

	@SocketEvent(socketId = "openItemTypeIOIModalEvent")
	public void itemTypeIOIModalRequestHandler(final String message)
	{
		sendCurrentIntegrationObject("openItemTypeIOIModal");
	}

	@SocketEvent(socketId = "auditReportEvent")
	public void downloadIntegrationObjectReport()
	{
		if (getSelectedIntegrationObject() != null && getSelectedIntegrationObject().getRootItem() != null)
		{
			final Map<String, InputStream> auditReportMapRes = auditReportBuilder.generateAuditReport(
					getSelectedIntegrationObject());
			if (!auditReportMapRes.isEmpty())
			{
				LOG.info("Audit Report has been Created Successfully!");
				auditReportBuilder.downloadAuditReport(auditReportMapRes);
			}
			else
			{
				LOG.info("Audit Report Creation Failed!");
			}
		}
		else
		{
			showNoServiceSelectedMessage();
		}
	}

	@SocketEvent(socketId = "receiveClone")
	public void cloneActionOnPerform()
	{
		if (selectedIntegrationObject != null && selectedIntegrationObject.getRootItem() != null)
		{
			if (!isModified())
			{
				sendOutput("openCloneModal", selectedIntegrationObject);
			}
			else
			{
				getNotificationService().notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
						NotificationEvent.Level.WARNING,
						getLabel("integrationbackoffice.editMode.warning.msg.saveBeforeCloning"));
			}
		}
		else
		{
			showNoServiceSelectedMessage();
		}
	}

	@SocketEvent(socketId = "saveButtonItemTypeMatch")
	public void itemTypeMatchSaveHandler(final Collection<IntegrationObjectItemModel> integrationObjectItemModels)
	{
		LOG.debug("The number of items to be saved {}", integrationObjectItemModels.size());
		writeService.persistIntegrationObjectItems(integrationObjectItemModels);
		cockpitEventQueue
				.publishEvent(new DefaultCockpitEvent(ObjectFacade.OBJECTS_UPDATED_EVENT, selectedIntegrationObject, null));
		getNotificationService().notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
				NotificationEvent.Level.SUCCESS, getLabel("integrationbackoffice.itemTypeMatchIOIModal.msg.save"));
	}

	@SocketEvent(socketId = "cloneIntegrationObjectEvent")
	public void cloneIntegrationObject(final CreateIntegrationObjectModalData data)
	{
		if (data.getComposedTypeModel() != null)
		{
			filterStateChange(IntegrationFilterState.SHOW_ALL);
			sendOutput("filterStateChangeOutput", IntegrationFilterState.SHOW_ALL);
			setModified(false);

			setItemTypeMatchMap(getItemTypeMatchForIntegrationObjectItem(selectedIntegrationObject));
			final Map<ComposedTypeModel, List<AbstractListItemDTO>> trimmedMap = createIntegrationObject(data);
			getItemTypeMatchMap().clear();
			if (!("").equals(validateHasKey(trimmedMap)))
			{
				getNotificationService().notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
						NotificationEvent.Level.WARNING,
						getLabel("integrationbackoffice.editMode.warning.msg.serviceClonedNeedsFurtherConfig"));
			}
			else
			{
				getNotificationService().notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
						NotificationEvent.Level.SUCCESS, getLabel("integrationbackoffice.editMode.info.msg.serviceCloned"));
			}
		}
		else
		{
			getNotificationService().notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.WARNING,
					getLabel("integrationbackoffice.editMode.warning.msg.integrationContextLost"));
		}
	}

	@SocketEvent(socketId = "renameAttributeEvent")
	public void renameAttribute(final RenameAttributeModalData renameAttributeModalData)
	{
		final ComposedTypeModel parentComposedType = renameAttributeModalData.getParent();
		final AbstractListItemDTO updatedDTO = renameAttributeModalData.getDto();
		final String alias = updatedDTO.getAlias();
		final AbstractListItemDTO matchedDTO;

		if (updatedDTO instanceof ListItemAttributeDTO)
		{
			final AttributeDescriptorModel attributeDescriptor = ((ListItemAttributeDTO) updatedDTO).getAttributeDescriptor();
			matchedDTO = getCurrentAttributesMap().get(parentComposedType)
			                                      .stream()
			                                      .filter(ListItemAttributeDTO.class::isInstance)
			                                      .map(ListItemAttributeDTO.class::cast)
			                                      .filter(listItemDTO -> listItemDTO.getAttributeDescriptor()
			                                                                        .equals(attributeDescriptor))
			                                      .findFirst()
			                                      .get();
		}
		else
		{
			final String categoryCode = ((ListItemClassificationAttributeDTO) updatedDTO).getCategoryCode();
			final String classificationAttributeCode = ((ListItemClassificationAttributeDTO) updatedDTO)
					.getClassificationAttributeCode();
			matchedDTO = getCurrentAttributesMap().get(parentComposedType)
			                                      .stream()
			                                      .filter(ListItemClassificationAttributeDTO.class::isInstance)
			                                      .map(ListItemClassificationAttributeDTO.class::cast)
			                                      .filter(listItemDTO -> listItemDTO.getCategoryCode().equals(categoryCode)
					                                      && listItemDTO.getClassificationAttributeCode()
					                                                    .equals(classificationAttributeCode))
			                                      .findFirst()
			                                      .get();
		}

		matchedDTO.setAlias(alias);
		matchedDTO.setSelected(true);

		if (matchedDTO instanceof ListItemAttributeDTO)
		{
			final ListItemAttributeDTO listItemAttributeDTO = (ListItemAttributeDTO) matchedDTO;
			checkTreeNodeForStructuredType(listItemAttributeDTO);
			if (readService.isComplexType(listItemAttributeDTO.getType()))
			{
				getComposedTypeTree().getItems().forEach(treeitem -> {
					final TreeNodeData treeNodeData = treeitem.getValue();
					if (parentComposedType.equals(treeNodeData.getComposedTypeModel()))
					{
						final String qualifier = listItemAttributeDTO.getAttributeDescriptor().getQualifier();
						final Treeitem childTreeitem = findInTreechildren(qualifier, treeitem.getTreechildren());
						renameTreeitem(childTreeitem, matchedDTO);
					}
				});
			}
		}
		else
		{
			final ListItemClassificationAttributeDTO classificationAttributeDTO = (ListItemClassificationAttributeDTO) matchedDTO;
			if (classificationAttributeDTO.getClassAttributeAssignmentModel().getReferenceType() != null)
			{
				getComposedTypeTree().getItems().forEach(treeitem -> {
					final TreeNodeData treeNodeData = treeitem.getValue();
					if (parentComposedType.equals(treeNodeData.getComposedTypeModel()))
					{
						final String qualifier = classificationAttributeDTO.getClassificationAttributeCode();
						final Treeitem childTreeitem = findInTreechildren(qualifier, treeitem.getTreechildren());
						renameTreeitem(childTreeitem, matchedDTO);
					}
				});
			}
		}
		autoSelectAttributeRelation(getComposedTypeTree().getSelectedItem());

		if (readService.isProductType(getSelectedComposedType().getCode()))
		{
			attributeDuplicationMap = compileDuplicationMap(getSelectedComposedType(),
					getCurrentAttributesMap().get(getSelectedComposedType()), attributeDuplicationMap);
		}
		populateListBox(parentComposedType);
		focusOnListitem(alias);
		enableSaveButton();
	}

	@SocketEvent(socketId = "retypeAttributeEvent")
	public void retypeAttribute(final SubtypeData subtypeData)
	{
		final String alias = subtypeData.getAttributeAlias();
		final ComposedTypeModel parentComposedType = subtypeData.getParentNodeType();
		final ComposedTypeModel newComposedType = readService.getComposedTypeModelFromTypeModel(subtypeData.getSubtype());
		final Treeitem currentTreeitem = getComposedTypeTree().getSelectedItem();
		final ListItemAttributeDTO dto = getCurrentAttributesMap().get(parentComposedType)
		                                                          .stream()
		                                                          .filter(ListItemAttributeDTO.class::isInstance)
		                                                          .map(ListItemAttributeDTO.class::cast)
		                                                          .filter(listItemDTO -> listItemDTO.getAlias().equals(alias))
		                                                          .findFirst()
		                                                          .get();

		// Update data structures
		dto.setType(newComposedType);
		dto.setSelected(true);
		if (dto.isAutocreate() && newComposedType.getAbstract())
		{
			dto.setAutocreate(false);
			getNotificationService().notifyUser(IntegrationbackofficeConstants.EXTENSIONNAME,
					IntegrationbackofficeConstants.NOTIFICATION_TYPE, NotificationEvent.Level.WARNING,
					getLabel("integrationbackoffice.editMode.warning.msg.abstractTypeAutocreate", Arrays.array(dto.getAlias())));
		}
		autoSelectAttributeRelation(currentTreeitem);
		populateAttributesMap(newComposedType);
		getSubtypeDataSet().add(subtypeData);

		// Add node for collections or circular dependency changes
		final String dtoAttributeDescriptorQualifier = dto.getAttributeDescriptor().getQualifier();
		if (readService.isComplexType(dto.getType()))
		{
			final Treechildren nodeChildren = currentTreeitem.getTreechildren();
			if (findInTreechildren(dtoAttributeDescriptorQualifier, nodeChildren) == null)
			{
				createDynamicTreeNode(dto.getAttributeDescriptor().getQualifier(), dto.getAlias(), dto.getType());
			}
		}

		// Replace nodes with new nodes containing subtype and rebuild branches of replaced nodes
		final List<Treeitem> treeitems = new ArrayList<>(getComposedTypeTree().getItems());
		final Iterator<Treeitem> iterator = treeitems.iterator();
		while (iterator.hasNext())
		{
			final Treeitem treeitem = iterator.next();
			final TreeNodeData treeNodeData = treeitem.getValue();
			final ComposedTypeModel composedTypeModel = treeNodeData.getComposedTypeModel();
			if (parentComposedType.equals(composedTypeModel))
			{
				final Treechildren treechildren = treeitem.getTreechildren();
				final Treeitem childTreeitem = findInTreechildren(dtoAttributeDescriptorQualifier, treechildren);
				if (childTreeitem != null)
				{
					final TreeNodeData childTreeNodeData = childTreeitem.getValue();
					childTreeNodeData.setComposedTypeModel(newComposedType);

					final Treeitem updatedTreeitem = createTreeItem(childTreeNodeData, childTreeitem.isOpen());
					updatedTreeitem.appendChild(new Treechildren());
					treechildren.removeChild(childTreeitem);
					treechildren.appendChild(updatedTreeitem);

					setAncestors(determineTreeitemAncestors(updatedTreeitem));
					populateTree(updatedTreeitem, Collections.emptyMap());
					iterator.remove();
				}
			}
		}

		LOG.info("Base type {} updated to subtype {} under parent of type {}", subtypeData.getBaseType().getCode(),
				newComposedType.getCode(), parentComposedType.getCode());
		Events.sendEvent(Events.ON_SELECT, getComposedTypeTree(), currentTreeitem);
		focusOnListitem(alias);
		enableSaveButton();
	}

	@SocketEvent(socketId = "filterStateChangeInput")
	public void filterStateChange(final IntegrationFilterState state)
	{
		setFilterState(state);
		final IntegrationObjectItemModel root = selectedIntegrationObject.getRootItem();
		if (root != null)
		{
			if (getEditModeFlag())
			{
				createTree(root.getType(), trimMap(readService, getCurrentAttributesMap(), getComposedTypeTree(), false));
			}
			else
			{
				createTree(root.getType(), convertIntegrationObjectToDTOMap(readService, selectedIntegrationObject));
			}
		}
		else
		{
			clearTree();
			getNotificationService().notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.WARNING, getLabel("integrationbackoffice.editMode.warning.msg.invalidObjectLoaded"));
		}
	}

	/**
	 * @deprecated since 2005. Use {@link #addClassificationAttributes(SelectedClassificationAttributesData)} instead.
	 */
	@Deprecated(since = "2005", forRemoval = true)
	public void addClassificationAttributes(final Collection<ClassAttributeAssignmentModel> attributes)
	{
		addClassificationAttributes(new SelectedClassificationAttributesData(attributes, false));
	}

	@SocketEvent(socketId = "addClassificationAttributesEvent")
	public void addClassificationAttributes(final SelectedClassificationAttributesData selectedClassificationAttributesData)
	{
		if (!readService.isProductType(getSelectedComposedType().getCode()))
		{
			getNotificationService().notifyUser(IntegrationbackofficeConstants.EXTENSIONNAME,
					IntegrationbackofficeConstants.NOTIFICATION_TYPE, NotificationEvent.Level.WARNING,
					getLabel("integrationbackoffice.editMode.warning.msg.cannotAddClassificationAttributeToNotProductType"));
			return;
		}

		boolean attributeAlreadyPresent = false;
		final boolean useFullClassificationQualifier = selectedClassificationAttributesData.isUseFullQualifier();
		for (final ClassAttributeAssignmentModel assignment : selectedClassificationAttributesData.getAssignments())
		{
			if (!isClassificationAttributePresent(assignment, getCurrentAttributesMap().get(getSelectedComposedType())))
			{
				final String alias = useFullClassificationQualifier ? classificationAttributeQualifierProvider.provide(
						assignment) : "";
				final ListItemClassificationAttributeDTO dto = new ListItemClassificationAttributeDTO(true, false, false,
						assignment,
						alias);
				getCurrentAttributesMap().get(getSelectedComposedType()).add(dto);
				if (ClassificationAttributeTypeEnum.REFERENCE.equals(assignment.getAttributeType()))
				{
					createDynamicTreeNode(dto.getClassificationAttributeCode(), dto.getAlias(), assignment.getReferenceType());
				}
			}
			else if (!assignment.getMandatory())
			{
				attributeAlreadyPresent = true;
			}
		}
		if (attributeAlreadyPresent)
		{
			getNotificationService().notifyUser(IntegrationbackofficeConstants.EXTENSIONNAME,
					IntegrationbackofficeConstants.NOTIFICATION_TYPE, NotificationEvent.Level.WARNING,
					getLabel("integrationbackoffice.editMode.warning.msg.attributeAlreadyPresent"));
		}

		attributeDuplicationMap = compileDuplicationMap(getSelectedComposedType(),
				getCurrentAttributesMap().get(getSelectedComposedType()), attributeDuplicationMap);
		populateListBox(getSelectedComposedType());
		sendOutput(ENABLE_SAVE_BUTTON_OUTPUT_SOCKET, true);
	}

	//////////////////////////////////////
	//   PUBLIC/PRIVATE LOGIC METHODS   //
	//////////////////////////////////////

	/*
	 * PERSISTENCE
	 */

	private void persistenceSetup(final IntegrationObjectModel ioModel)
	{
		persistIntegrationObject(ioModel);
		setModified(false);
		sendOutput(ENABLE_SAVE_BUTTON_OUTPUT_SOCKET, false);
	}

	private void persistIntegrationObject(final IntegrationObjectModel ioModel)
	{
		writeService.persistIntegrationObject(ioModel);
		cockpitEventQueue
				.publishEvent(new DefaultCockpitEvent(ObjectFacade.OBJECTS_UPDATED_EVENT, selectedIntegrationObject, null));
		getNotificationService().notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
				NotificationEvent.Level.SUCCESS, getLabel("integrationbackoffice.editMode.info.msg.save"));
	}

	private Map<ComposedTypeModel, List<AbstractListItemDTO>> createIntegrationObject(final CreateIntegrationObjectModalData data)
	{
		final IntegrationObjectModel selectedIO = writeService.createIntegrationObject(data.getName(), data.getType());
		setSelectedIntegrationObject(selectedIO);
		final Map<ComposedTypeModel, List<AbstractListItemDTO>> trimmedMap = trimMap(readService, getCurrentAttributesMap(),
				getComposedTypeTree(), false);
		final IntegrationObjectModel ioModel = setItemTypeMatchForIntegrationObject(
				writeService.createDefinitions(selectedIntegrationObject, trimmedMap, data.getComposedTypeModel().getCode()));
		writeService.persistIntegrationObject(ioModel);
		cockpitEventQueue.publishEvent(
				new DefaultCockpitEvent(ObjectFacade.OBJECT_CREATED_EVENT, selectedIntegrationObject, null));
		return trimmedMap;
	}

	private IntegrationObjectModel setItemTypeMatchForIntegrationObject(IntegrationObjectModel integrationObjectModel)
	{
		integrationObjectModel.getItems().forEach(this::setItemTypeMatchForIntegrationObjectItem);
		return integrationObjectModel;
	}

	private void setItemTypeMatchForIntegrationObjectItem(IntegrationObjectItemModel item)
	{
		if (item.getItemTypeMatch() == null)
		{
			if (getItemTypeMatchMap().containsKey(item.getType()))
			{
				item.setItemTypeMatch(getItemTypeMatchMap().get(item.getType()));
			}
			else
			{
				item.setItemTypeMatch(getDefaultItemTypeMatchEnum(item));
			}
		}
	}

	private Map<ComposedTypeModel, ItemTypeMatchEnum> getItemTypeMatchForIntegrationObjectItem(
			IntegrationObjectModel integrationObjectModel)
	{
		return integrationObjectModel.getItems().stream()
		                             .filter(it -> it.getItemTypeMatch() != null)
		                             .collect(Collectors.toMap(IntegrationObjectItemModel::getType,
				                             IntegrationObjectItemModel::getItemTypeMatch));
	}

	private ItemTypeMatchEnum getDefaultItemTypeMatchEnum(IntegrationObjectItemModel integrationObjectItemModel)
	{
		return ItemTypeMatchEnum.valueOf(itemTypeMatchSelector.getToSelectItemTypeMatch(integrationObjectItemModel).name());
	}

	/*
	 * TREE UI MANAGEMENT
	 */

	private void clearTree()
	{
		getComposedTypeTree().getTreechildren().getChildren().clear();
		getAttributesListBox().getItems().clear();
		setAncestors(new ArrayDeque<>());
	}

	private void createTree(final ComposedTypeModel rootType,
	                        final Map<ComposedTypeModel, List<AbstractListItemDTO>> existingDefinitions)
	{
		clearTree();
		getAncestors().push(rootType);

		final Treechildren rootLevel = getComposedTypeTree().getTreechildren();
		final TreeNodeData tnd = new TreeNodeData(null, null, rootType);
		final Treeitem rootTreeItem = createTreeItem(tnd, true);
		rootLevel.appendChild(rootTreeItem);

		setCurrentAttributesMap(new HashMap<>());
		if (getFilterState() == IntegrationFilterState.SHOW_ALL)
		{
			populateAttributesMap(rootType);
			populateTree(rootTreeItem, existingDefinitions);
			loadExistingDefinitions(existingDefinitions);
		}
		else
		{
			setCurrentAttributesMap(existingDefinitions);
			populateTreeInOnlySelectedMode(rootTreeItem, existingDefinitions);
		}

		rootTreeItem.setSelected(true);
		Events.sendEvent(Events.ON_SELECT, getComposedTypeTree(), rootTreeItem);
	}

	private void populateTree(final Treeitem parent, final Map<ComposedTypeModel, List<AbstractListItemDTO>> existingDefinitions)
	{
		final ComposedTypeModel parentType = ((TreeNodeData) parent.getValue()).getComposedTypeModel();
		final List<AbstractListItemDTO> existingAttributes = (existingDefinitions.get(
				parentType) == null) ? Collections.emptyList()
				: existingDefinitions.get(parentType);
		final Set<AttributeDescriptorModel> filteredAttributes = editorAttrFilterService.filterAttributesForTree(parentType);
		final Set<AttributeDescriptorModel> existingCollections = getStructuredAttributes(existingAttributes);
		filteredAttributes.addAll(existingCollections);

		final Set<TreeNodeData> treeNodeDataSet = filteredAttributes.stream().filter(attributeDescriptor -> {
			final ComposedTypeModel attributeType = readService.getComplexTypeForAttributeDescriptor(attributeDescriptor);
			return (attributeType != null);
		}).map(attributeDescriptor -> {
			final String attributeDescriptorQualifier = attributeDescriptor.getQualifier();
			final ComposedTypeModel attributeType = readService.getComplexTypeForAttributeDescriptor(attributeDescriptor);
			final ComposedTypeModel attributeSubtype = findSubtypeMatch(parentType, attributeDescriptorQualifier, attributeType);
			final ComposedTypeModel determinedType = attributeSubtype != null ? attributeSubtype : attributeType;
			for (final AbstractListItemDTO dto : existingAttributes)
			{
				if (dto instanceof ListItemAttributeDTO
						&& ((ListItemAttributeDTO) dto).getAttributeDescriptor().equals(attributeDescriptor))
				{
					return new TreeNodeData(attributeDescriptorQualifier, dto.getAlias(), determinedType);
				}
			}
			return new TreeNodeData(attributeDescriptorQualifier, null, determinedType);
		}).collect(Collectors.toSet());

		treeNodeDataSet.addAll(getReferenceClassificationAttributes(existingAttributes));
		treeNodeDataSet.stream()
		               .sorted((attribute1, attribute2) -> attribute1.getAlias().compareToIgnoreCase(attribute2.getAlias()))
		               .forEach(treeNodeData -> {
			               final ComposedTypeModel composedType = treeNodeData.getComposedTypeModel();
			               if (!getAncestors().contains(composedType)
					               && !EditorBlacklists.getTypesBlackList().contains(composedType.getCode()))
			               {
				               getAncestors().addFirst(composedType);

				               if (!getCurrentAttributesMap().containsKey(composedType))
				               {
					               populateAttributesMap(composedType);
				               }

				               final Treeitem treeitem = appendTreeitem(parent, treeNodeData);
				               if (treeitem.getLevel() <= MAX_DEPTH)
				               {
					               populateTree(treeitem, existingDefinitions);
				               }

				               getAncestors().pollFirst();
			               }
		               });
	}

	private void populateTreeInOnlySelectedMode(final Treeitem parent,
	                                            final Map<ComposedTypeModel, List<AbstractListItemDTO>> existingDefinitions)
	{
		final TreeNodeData parentTreeNodeData = parent.getValue();
		final ComposedTypeModel parentType = parentTreeNodeData.getComposedTypeModel();
		final List<AbstractListItemDTO> existingAttributes = (existingDefinitions.get(
				parentType) == null) ? Collections.emptyList()
				: existingDefinitions.get(parentType);
		final List<TreeNodeData> treeNodeDataSet = existingAttributes.stream()
		                                                             .filter(ListItemAttributeDTO.class::isInstance)
		                                                             .map(ListItemAttributeDTO.class::cast)
		                                                             .filter(listItemDTO -> {
			                                                             final ComposedTypeModel attributeType = readService
					                                                             .getComplexTypeForAttributeDescriptor(
							                                                             listItemDTO.getAttributeDescriptor());
			                                                             return (attributeType != null);
		                                                             })
		                                                             .map(listItemDTO -> {
			                                                             final AttributeDescriptorModel attributeDescriptor = listItemDTO
					                                                             .getAttributeDescriptor();
			                                                             final String attributeDescriptorQualifier = attributeDescriptor
					                                                             .getQualifier();
			                                                             final ComposedTypeModel attributeType = readService.getComplexTypeForAttributeDescriptor(
					                                                             attributeDescriptor);
			                                                             final ComposedTypeModel attributeSubtype = findSubtypeMatch(
					                                                             parentType, attributeDescriptorQualifier,
					                                                             attributeType);
			                                                             final ComposedTypeModel determinedType = attributeSubtype != null ? attributeSubtype : attributeType;
			                                                             return new TreeNodeData(attributeDescriptorQualifier,
					                                                             listItemDTO.getAlias(), determinedType);
		                                                             })
		                                                             .collect(Collectors.toList());
		treeNodeDataSet.addAll(getReferenceClassificationAttributes(existingAttributes));
		treeNodeDataSet.stream()
		               .sorted((attribute1, attribute2) -> attribute1.getAlias().compareToIgnoreCase(attribute2.getAlias()))
		               .forEach(treeNodeData -> {
			               final ComposedTypeModel composedType = treeNodeData.getComposedTypeModel();
			               if (!getAncestors().contains(composedType))
			               {
				               getAncestors().addFirst(composedType);
				               final Treeitem treeitem = appendTreeitem(parent, treeNodeData);
				               populateTreeInOnlySelectedMode(treeitem, existingDefinitions);
				               getAncestors().pollFirst();
			               }
		               });
	}

	public ComposedTypeModel findSubtypeMatch(final ComposedTypeModel parentType, final String attributeQualifier,
	                                          final ComposedTypeModel attributeType)
	{
		final ComposedTypeModel attributeSubtype;
		final Optional<SubtypeData> data = getSubtypeDataSet().stream().filter(p -> p.getParentNodeType().equals(parentType)
				&& p.getBaseType().equals(attributeType) && attributeQualifier.equals(p.getAttributeQualifier())).findFirst();
		if (data.isPresent())
		{
			attributeSubtype = readService.getComposedTypeModelFromTypeModel(data.get().getSubtype());
		}
		else
		{
			attributeSubtype = null;
		}
		return attributeSubtype;
	}

	private Treeitem appendTreeitem(final Treeitem parent, final TreeNodeData tnd)
	{
		final Treeitem treeitem = createTreeItem(tnd, false);
		if (parent.getTreechildren() == null)
		{
			parent.appendChild(new Treechildren());
		}
		parent.getTreechildren().appendChild(treeitem);
		return treeitem;
	}

	private void createDynamicTreeNode(final String qualifier, final String alias, final TypeModel typeModel)
	{
		final ComposedTypeModel type = (ComposedTypeModel) typeModel;
		final TreeNodeData tnd = new TreeNodeData(qualifier, alias, type);
		final Treeitem parent = getComposedTypeTree().getSelectedItem();
		final Treeitem treeItem = appendTreeitem(parent, tnd);
		populateAttributesMap(type);
		getAncestors().clear();
		populateTree(treeItem, Collections.emptyMap());
	}

	/*
	 * LISTBOX UI MANAGEMENT
	 */

	private void populateListBox(final ComposedTypeModel key)
	{
		getAttributesListBox().getItems().clear();
		final String attributeNamesSortingDirection = attributeNameListheader.getSortDirection();
		final String descriptionsSortingDirection = descriptionListheader.getSortDirection();

		getCurrentAttributesMap().get(key).stream().sorted((dto1, dto2) -> {
			if (ASCENDING.equals(attributeNamesSortingDirection))
			{
				return dto1.getAlias().compareToIgnoreCase(dto2.getAlias());
			}
			else if (DESCENDING.equals(attributeNamesSortingDirection))
			{
				return dto2.getAlias().compareToIgnoreCase(dto1.getAlias());
			}
			else if (ASCENDING.equals(descriptionsSortingDirection))
			{
				return dto1.getDescription().compareToIgnoreCase(dto2.getDescription());
			}
			else if (DESCENDING.equals(descriptionsSortingDirection))
			{
				return dto2.getDescription().compareToIgnoreCase(dto1.getDescription());
			}
			else
			{
				return dto1.getAlias().compareToIgnoreCase(dto2.getAlias());
			}
		}).forEach((AbstractListItemDTO dto) -> {
			final Listitem listItem = setupListItem(key, dto);
			getAttributesListBox().appendChild(listItem);
		});


		if (markRowsWithDuplicateNames(getAttributesListBox().getItems(),
				getAttributeDuplicationMap().get(getSelectedComposedType())))
		{
			getNotificationService().notifyUser(IntegrationbackofficeConstants.EXTENSIONNAME,
					IntegrationbackofficeConstants.NOTIFICATION_TYPE, NotificationEvent.Level.WARNING,
					getLabel("integrationbackoffice.editMode.warning.msg.nameDuplication"));
		}
	}

	private Listitem setupListItem(final ComposedTypeModel key, final AbstractListItemDTO abstractListItemDTO)
	{
		boolean isComplex = false;
		boolean hasSubtypes = false;
		if (abstractListItemDTO instanceof ListItemAttributeDTO)
		{
			final ListItemAttributeDTO dto = (ListItemAttributeDTO) abstractListItemDTO;
			isComplex = readService.isComplexType(dto.getType());
			if (isComplex)
			{
				hasSubtypes = !readService.getComposedTypeModelFromTypeModel(dto.getBaseType()).getSubtypes().isEmpty();
			}
		}
		final Listitem listItem = createListItem(abstractListItemDTO, isComplex, hasSubtypes, attributeMenuPopupLabels,
				getEditModeFlag(), readService);
		final Checkbox uniqueCheckbox = (Checkbox) listItem.getChildren()
		                                                   .get(EditorConstants.UNIQUE_CHECKBOX_COMPONENT_INDEX)
		                                                   .getFirstChild();
		final Checkbox autocreateCheckbox = (Checkbox) listItem.getChildren()
		                                                       .get(EditorConstants.AUTOCREATE_CHECKBOX_COMPONENT_INDEX)
		                                                       .getFirstChild();
		final Button optionsBtn = (Button) listItem.getLastChild().getFirstChild();
		final Menuitem viewDetails = (Menuitem) optionsBtn.getFirstChild().getFirstChild();
		final Menuitem renameAttribute = (Menuitem) optionsBtn.getFirstChild().getFirstChild().getNextSibling();
		final Menuitem retypeAttribute = (Menuitem) optionsBtn.getFirstChild().getLastChild();

		final List<AbstractListItemDTO> itemAttributeDTOs = new ArrayList<>(getCurrentAttributesMap().get(key));
		itemAttributeDTOs.remove(abstractListItemDTO);
		final RenameAttributeModalData renameAttributeModalData = new RenameAttributeModalData(itemAttributeDTOs,
				abstractListItemDTO, key);

		if (getEditModeFlag())
		{
			if (abstractListItemDTO instanceof ListItemAttributeDTO)
			{
				addListItemEvents((ListItemAttributeDTO) abstractListItemDTO, listItem, uniqueCheckbox, autocreateCheckbox);
			}
			else
			{
				listItem.addEventListener(Events.ON_CLICK, event -> {
					if (!listItem.isSelected())
					{
						autocreateCheckbox.setChecked(false);
					}
					updateAttribute(listItem);
				});
			}
			addCheckboxEvents(listItem, uniqueCheckbox, autocreateCheckbox);
			addClassificationEvents(listItem);
		}
		else
		{
			addMaintainStateEvents(abstractListItemDTO, listItem);
		}

		addButtonEvents(listItem, optionsBtn);
		addMenuItemEvents(renameAttributeModalData, viewDetails, renameAttribute, retypeAttribute);

		if (!getEditModeFlag())
		{
			uniqueCheckbox.setDisabled(true);
			autocreateCheckbox.setDisabled(true);
			renameAttribute.setVisible(false);
		}
		return listItem;
	}

	private void focusOnListitem(final String alias)
	{
		for (final Listitem listitem : getAttributesListBox().getItems())
		{
			final AbstractListItemDTO dto = listitem.getValue();
			if (dto.getAlias().equals(alias))
			{
				Clients.scrollIntoView(listitem);
				break;
			}
		}
	}

	private void autoSelectAttributeRelation(Treeitem currentTreeitem)
	{
		final Treeitem rootTreeitem = (Treeitem) getComposedTypeTree().getTreechildren().getFirstChild();
		while (currentTreeitem != rootTreeitem)
		{
			final String qualifier = ((TreeNodeData) currentTreeitem.getValue()).getQualifier();
			final Treeitem parentTreeitem = currentTreeitem.getParentItem();
			final ComposedTypeModel parentType = ((TreeNodeData) parentTreeitem.getValue()).getComposedTypeModel();
			getCurrentAttributesMap().get(parentType).stream().filter(ListItemAttributeDTO.class::isInstance)
			                         .map(ListItemAttributeDTO.class::cast).forEach(listItemDTO -> {
				if (listItemDTO.getAttributeDescriptor().getQualifier().equals(qualifier))
				{
					listItemDTO.setSelected(true);
				}
			});
			currentTreeitem = parentTreeitem;
		}
	}

	/*
	 * DATA STRUCTURE MANAGEMENT
	 */

	private void populateAttributesMap(final ComposedTypeModel typeModel)
	{
		if (getCurrentAttributesMap().get(typeModel) == null)
		{
			final List<AbstractListItemDTO> dtoList = new ArrayList<>();
			final Set<AttributeDescriptorModel> filteredAttributes = editorAttrFilterService.filterAttributesForAttributesMap(
					typeModel);
			filteredAttributes.forEach(attribute -> {
				final boolean selected = attribute.getUnique() && !attribute.getOptional();
				final ListItemStructureType structureType = getListItemStructureType(readService, attribute);
				dtoList.add(new ListItemAttributeDTO(selected, false, false, attribute, structureType, "", null));
			});
			getCurrentAttributesMap().put(typeModel, dtoList);
		}
	}

	private void loadExistingDefinitions(final Map<ComposedTypeModel, List<AbstractListItemDTO>> existingDefinitions)
	{
		existingDefinitions.forEach((key, value) -> getCurrentAttributesMap().forEach((key2, value2) -> {
			if (key2.equals(key))
			{
				getCurrentAttributesMap().replace(key2, updateDTOs(value2, value));
			}
		}));
	}

	public void compileSubtypeDataSet(final Map<ComposedTypeModel, List<AbstractListItemDTO>> existingDefinitions)
	{
		setSubtypeDataSet(new HashSet<>());
		existingDefinitions.forEach((key, value) -> value.stream().filter(ListItemAttributeDTO.class::isInstance)
		                                                 .map(ListItemAttributeDTO.class::cast).forEach(dto -> {
					if (!dto.getType().equals(dto.getBaseType()))
					{
						final SubtypeData data = new SubtypeData(key, dto.getType(), dto.getBaseType(), dto.getAlias(),
								dto.getAttributeDescriptor().getQualifier());
						getSubtypeDataSet().add(data);
					}
				}));
	}

	/*
	 * EVENT LISTENERS
	 */

	private void addMenuItemEvents(final RenameAttributeModalData ramd, final Menuitem viewDetails,
	                               final Menuitem renameAttribute,
	                               final Menuitem retypeAttribute)
	{
		renameAttribute.addEventListener(Events.ON_CLICK, event -> sendOutput("openRenameAttribute", ramd));

		if (ramd.getDto() instanceof ListItemAttributeDTO)
		{
			viewDetails.addEventListener(Events.ON_CLICK, event -> sendOutput("openAttributeDetails", ramd.getDto()));

			final ListItemAttributeDTO ramdDTO = (ListItemAttributeDTO) ramd.getDto();
			final SubtypeData data = new SubtypeData(ramd.getParent(), ramdDTO.getType(), ramdDTO.getBaseType(),
					ramd.getDto().getAlias(), ramdDTO.getAttributeDescriptor().getQualifier());
			retypeAttribute.addEventListener(Events.ON_CLICK, event -> sendOutput("openRetypeAttribute", data));
		}
		else
		{
			viewDetails.addEventListener(Events.ON_CLICK,
					event -> sendOutput("openClassificationAttributeDetails", ramd.getDto()));
		}
	}


	private void addCheckboxEvents(final Listitem listItem, final Checkbox uniqueCheckbox, final Checkbox autocreateCheckbox)
	{
		uniqueCheckbox.addEventListener(Events.ON_CHECK, event -> checkboxEventActions(listItem, uniqueCheckbox));
		autocreateCheckbox.addEventListener(Events.ON_CHECK, event -> checkboxEventActions(listItem, autocreateCheckbox));
	}

	private void checkboxEventActions(final Listitem listItem, final Checkbox checkbox)
	{
		if (!listItem.isDisabled())
		{
			if (checkbox.isChecked())
			{
				listItem.setSelected(true);
				if (listItem.getValue() instanceof ListItemAttributeDTO)
				{
					checkTreeNodeForStructuredType(listItem.getValue());
				}
			}
			updateAttribute(listItem);
		}
	}

	private void addButtonEvents(final Listitem listItem, final Button detailsBtn)
	{
		detailsBtn.addEventListener(Events.ON_CLICK, event -> {
			if (!listItem.isDisabled())
			{
				final Menupopup menuPopup = (Menupopup) listItem.getLastChild().getFirstChild().getFirstChild();
				menuPopup.open(detailsBtn);
			}
		});
	}

	private void addListItemEvents(final ListItemAttributeDTO dto, final Listitem listItem, final Checkbox uniqueCheckbox,
	                               final Checkbox autocreateCheckbox)
	{
		if (dto.isRequired())
		{
			listItem.addEventListener(Events.ON_CLICK, event ->
				maintainSelectionState(listItem, true));
		}
		else if (!dto.isSupported())
		{
			listItem.addEventListener(Events.ON_CLICK, event -> {
				maintainSelectionState(listItem, false);
				getNotificationService().notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
						NotificationEvent.Level.WARNING,
						getLabel("integrationbackoffice.editMode.warning.msg.illegalMapTypeAttribute",
								Arrays.array(dto.getAttributeDescriptor().getQualifier())));
			});
		}
		else if (!listItem.isDisabled())
		{
			listItem.addEventListener(Events.ON_CLICK, event -> {
				if (listItem.isSelected())
				{
					checkTreeNodeForStructuredType(dto);
				}
				else
				{
					if (!dto.getAttributeDescriptor().getUnique())
					{
						uniqueCheckbox.setChecked(false);
					}
					autocreateCheckbox.setChecked(false);
				}
				updateAttribute(listItem);
			});
		}
	}

	private void addMaintainStateEvents(final AbstractListItemDTO dto, final Listitem listItem)
	{
		if (dto.isSelected())
		{
			listItem.addEventListener(Events.ON_CLICK, event -> maintainSelectionState(listItem, true));
		}
		else
		{
			listItem.addEventListener(Events.ON_CLICK, event -> maintainSelectionState(listItem, false));
		}
	}

	private void maintainSelectionState(final Listitem listItem, final boolean selected)
	{
		listItem.setSelected(selected);
	}

	private void addClassificationEvents(final Listitem listItem)
	{
		listItem.addEventListener(Events.ON_CLICK, event -> {
			if (readService.isProductType(getSelectedComposedType().getCode()))
			{
				attributeDuplicationMap = compileDuplicationMap(getSelectedComposedType(),
						getCurrentAttributesMap().get(getSelectedComposedType()), attributeDuplicationMap);
				markRowsWithDuplicateNames(getAttributesListBox().getItems(),
						getAttributeDuplicationMap().get(getSelectedComposedType()));
			}
		});
	}

	/*
	 * LOGICAL HELPER METHODS
	 */

	private Deque<ComposedTypeModel> determineTreeitemAncestors(Treeitem currentTreeitem)
	{
		final Deque<ComposedTypeModel> currentTreePath = new ArrayDeque<>();
		while (currentTreeitem.getLevel() > 0)
		{
			currentTreePath.addFirst(((TreeNodeData) currentTreeitem.getValue()).getComposedTypeModel());
			currentTreeitem = currentTreeitem.getParentItem();
		}
		currentTreePath.addFirst(((TreeNodeData) currentTreeitem.getValue()).getComposedTypeModel());
		return currentTreePath;
	}

	private boolean validation(final Map<ComposedTypeModel, List<AbstractListItemDTO>> trimmedAttributesMap)
	{
		final String VALIDATION_MESSAGE_TITLE = getLabel("integrationbackoffice.editMode.error.title.validation");
		final String VALIDATION_DUPLICATION_TITLE = getLabel("integrationbackoffice.editMode.error.title.duplicationValidation");
		String validationError;
		validationError = validateDefinitions(trimmedAttributesMap);
		if (!("").equals(validationError))
		{
			Messagebox.show(
					getLabel("integrationbackoffice.editMode.error.msg.definitionValidation", Arrays.array(validationError)),
					VALIDATION_MESSAGE_TITLE, 1, Messagebox.ERROR);
			return false;
		}
		validationError = validateHasKey(trimmedAttributesMap);
		if (!("").equals(validationError))
		{
			Messagebox.show(getLabel("integrationbackoffice.editMode.error.msg.uniqueValidation", Arrays.array(validationError)),
					VALIDATION_MESSAGE_TITLE, 1, Messagebox.ERROR);
			return false;
		}
		validationError = validateHasNoDuplicateAttributeNames(getAttributeDuplicationMap());
		if (!("").equals(validationError))
		{
			Messagebox.show(
					getLabel("integrationbackoffice.editMode.error.msg.duplicationValidation", Arrays.array(validationError)),
					VALIDATION_DUPLICATION_TITLE, 1, Messagebox.ERROR);
			return false;
		}

		return true;
	}

	/*
	 * UI HELPER METHODS
	 */

	public void updateAttribute(final Listitem listitem)
	{
		final AbstractListItemDTO dto = listitem.getValue();
		final List<Component> components = listitem.getChildren();
		final Checkbox uCheckbox = ((Checkbox) components.get(EditorConstants.UNIQUE_CHECKBOX_COMPONENT_INDEX).getFirstChild());
		final Checkbox aCheckbox = ((Checkbox) components.get(EditorConstants.AUTOCREATE_CHECKBOX_COMPONENT_INDEX)
		                                                 .getFirstChild());
		final Listcell attributeLabel = (Listcell) components.get(EditorConstants.ATTRIBUTE_NAME_COMPONENT_INDEX);
		dto.setAlias(attributeLabel.getLabel());
		dto.setSelected(listitem.isSelected());
		dto.setCustomUnique(uCheckbox.isChecked());
		dto.setAutocreate(aCheckbox.isChecked());
		if (getEditModeFlag())
		{
			enableSaveButton();
		}
	}

	private void checkTreeNodeForStructuredType(final ListItemAttributeDTO dto)
	{
		final boolean isStructuredType = dto.getStructureType() == ListItemStructureType.MAP
				|| dto.getStructureType() == ListItemStructureType.COLLECTION;
		if (isStructuredType && readService.isComplexType(dto.getType()))
		{
			final Treechildren nodeChildren = getComposedTypeTree().getSelectedItem().getTreechildren();
			final String attributeDescriptorQualifier = dto.getAttributeDescriptor().getQualifier();
			if (findInTreechildren(attributeDescriptorQualifier, nodeChildren) == null)
			{
				createDynamicTreeNode(dto.getAttributeDescriptor().getQualifier(), dto.getAlias(), dto.getType());
			}
		}
	}

	private void showNoServiceSelectedMessage()
	{
		getNotificationService().notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
				NotificationEvent.Level.WARNING, getLabel("integrationbackoffice.editMode.warning.msg.noServiceSelected"));
	}

	private void showObjectLoadedFurtherConfigurationMessage()
	{
		getNotificationService().notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
				NotificationEvent.Level.WARNING,
				getLabel("integrationbackoffice.editMode.warning.msg.serviceLoadedNeedsFurtherConfig"));
	}

	private void enableSaveButton()
	{
		if (!isModified())
		{
			setModified(true);
			sendOutput(ENABLE_SAVE_BUTTON_OUTPUT_SOCKET, true);
		}
	}

	private void clearSelectedIntegrationObject()
	{
		clearTree();
		setSelectedIntegrationObject(null);
		attributeDuplicationMap.clear();
	}
}
