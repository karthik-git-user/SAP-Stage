/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.outbound.service;

import de.hybris.platform.comments.model.CommentModel;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteCommentModel;


/**
 * Provides mapping from {@link CommentModel} to {@link SAPCpiOutboundQuoteCommentModel}.
 *
 * @param <SOURCE> the parameter of the interface
 * @param <TARGET> the parameter of the interface
 */
public interface SapCpiQuoteCommentMapperService<SOURCE extends CommentModel, TARGET extends SAPCpiOutboundQuoteCommentModel>
{
	/**
	 * Performs mapping from source to target.
	 *
	 * @param source
	 *           Comment Model
	 * @param target
	 *           SAP CPI Outbound Quote Comment Model
	 */
	void map(SOURCE source, TARGET target);

}