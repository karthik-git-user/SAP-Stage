/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.populators;

import org.springframework.util.Assert;

import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class SapCpqQuotePopulator extends AbstractOrderPopulator<QuoteModel, QuoteData> {

	@Override
	public void populate(QuoteModel source, QuoteData target) throws ConversionException {

		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		
		target.setProposalMessage(source.getProposalMessage());
		
	}

}
