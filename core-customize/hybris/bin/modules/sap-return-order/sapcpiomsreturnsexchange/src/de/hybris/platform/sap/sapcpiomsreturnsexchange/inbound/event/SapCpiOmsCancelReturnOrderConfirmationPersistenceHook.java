/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapcpiomsreturnsexchange.inbound.event;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.returnsexchange.inbound.DataHubInboundCancelOrderHelper;


public class SapCpiOmsCancelReturnOrderConfirmationPersistenceHook implements PrePersistHook
{
	private DataHubInboundCancelOrderHelper sapDataHubInboundReturnOrderHelper;

	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		if (item instanceof ReturnRequestModel)
		{
			final ReturnRequestModel returnRequest = (ReturnRequestModel) item;
			getSapDataHubInboundReturnOrderHelper().processCancelOrderConfirmationFromDataHub(returnRequest.getCode());
			return Optional.empty();
		}

		return Optional.of(item);
	}

	/**
	 * @return the sapDataHubInboundReturnOrderHelper
	 */
	public DataHubInboundCancelOrderHelper getSapDataHubInboundReturnOrderHelper()
	{
		return sapDataHubInboundReturnOrderHelper;
	}

	/**
	 * @param sapDataHubInboundReturnOrderHelper
	 *           the sapDataHubInboundReturnOrderHelper to set
	 */
	@Required
	public void setSapDataHubInboundReturnOrderHelper(final DataHubInboundCancelOrderHelper sapDataHubInboundReturnOrderHelper)
	{
		this.sapDataHubInboundReturnOrderHelper = sapDataHubInboundReturnOrderHelper;
	}



}
