/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.util.RedeployUtilities;
import de.hybris.platform.util.Utilities;


/**
 * Demonstration of how to write a standalone application that can be run directly from within eclipse or from the
 * commandline.<br>
 * To run this from commandline, just use the following command:<br>
 * <code>
 * java -jar bootstrap/bin/ybootstrap.jar "new sapcpqquoteintegration.SapcpqquoteintegrationStandalone().run();"
 * </code> From eclipse, just run as Java Application. Note that you maybe need to add all other projects like
 * ext-commerce, ext-pim to the Launch configuration classpath.
 */
public class SapcpqquoteintegrationStandalone
{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SapcpqquoteintegrationStandalone.class);
	
	/**
	 * Main class to be able to run it directly as a java program.
	 *
	 * @param args the arguments from commandline
	 */
	public static void main(final String[] args)
	{
		new SapcpqquoteintegrationStandalone().run();
	}

	public void run()
	{
		Registry.activateStandaloneMode();
		Registry.activateMasterTenant();

		final JaloSession jaloSession = JaloSession.getCurrentSession();
		final String sessionIDMessage = String.format("Session ID: %s", jaloSession.getSessionID());
		final String userMessage = String.format("User: %s", jaloSession.getUser());
		LOGGER.info(sessionIDMessage);
		LOGGER.info(userMessage);
		Utilities.printAppInfo();

		RedeployUtilities.shutdown();
	}
}
