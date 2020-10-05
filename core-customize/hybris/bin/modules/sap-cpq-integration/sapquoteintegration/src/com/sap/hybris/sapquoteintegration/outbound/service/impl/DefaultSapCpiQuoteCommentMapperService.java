/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.outbound.service.impl;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteCommentModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiQuoteCommentMapperService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;

/**
 * Default class to add comments to Outbound Quote
 *
 */
public class DefaultSapCpiQuoteCommentMapperService
        implements SapCpiQuoteCommentMapperService<CommentModel, SAPCpiOutboundQuoteCommentModel> {

	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

	@Override
    public void map(final CommentModel comment, final SAPCpiOutboundQuoteCommentModel scpiQuoteComment) {
        mapComments(comment, scpiQuoteComment);
    }

	protected CompanyModel getRootB2BUnit(final B2BCustomerModel customerModel) {
		final B2BUnitModel parent = (B2BUnitModel) b2bUnitService.getParent(customerModel);
		return b2bUnitService.getRootUnit(parent);
	}

    /**
     *
     */
    protected void mapComments(final CommentModel comment, final SAPCpiOutboundQuoteCommentModel scpiQuoteComment) {
        scpiQuoteComment.setCommentId(comment.getCode());
        scpiQuoteComment.setText(comment.getText());

        if (comment.getAuthor() instanceof B2BCustomerModel) {
            final B2BCustomerModel customer = (B2BCustomerModel) comment.getAuthor();
            scpiQuoteComment.setUserName(customer.getName());
            scpiQuoteComment.setEmail(customer.getUid());
            final CompanyModel rootB2BUnit = getRootB2BUnit(customer);
            scpiQuoteComment.setB2bUnitName(rootB2BUnit.getName());
        }else if(comment.getAuthor() instanceof EmployeeModel) {
            final EmployeeModel employee = (EmployeeModel) comment.getAuthor();
            scpiQuoteComment.setUserName(employee.getName());
            scpiQuoteComment.setEmail(employee.getUid());
        }
    }

	public B2BUnitService<B2BUnitModel, UserModel> getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(B2BUnitService<B2BUnitModel,UserModel > b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}
}
