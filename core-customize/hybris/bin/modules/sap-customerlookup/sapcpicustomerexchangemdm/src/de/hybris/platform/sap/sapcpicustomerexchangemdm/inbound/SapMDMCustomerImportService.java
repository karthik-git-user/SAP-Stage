/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchangemdm.inbound;

import de.hybris.platform.core.model.user.CustomerModel;


/**
 * SapMDMCustomerImportService Handles Customer In Bound Object Processing
 */
public interface SapMDMCustomerImportService
{

	/**
	 * Process the In Bound Message of Consumer Creation Notification From Back End
	 *
     * @param sapCpiInboundCustomerModel - Inbound customer model
	 */
	public void processConsumerReplicationNotificationFromMDM(final CustomerModel sapCpiInboundCustomerModel);


}
