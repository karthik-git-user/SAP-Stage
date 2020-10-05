/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.actions;

import de.hybris.platform.cissapdigitalpayment.model.SapDigitPayPollCardProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;


/**
 *
 * Action class to check the the session validity before start the poling process
 */
public class SapDigitalPayCheckSessionAction extends AbstractSimpleDecisionAction<SapDigitPayPollCardProcessModel>
{

	@Override
	public Transition executeAction(final SapDigitPayPollCardProcessModel pollCardProcess)
	{
		Transition returnValue = Transition.OK;
		if (null == pollCardProcess.getSessionId())
		{
			returnValue = Transition.NOK;
		}
		return returnValue;

	}


}
