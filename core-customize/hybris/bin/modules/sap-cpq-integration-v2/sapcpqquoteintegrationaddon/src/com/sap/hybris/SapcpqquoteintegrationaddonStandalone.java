/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris;

import org.apache.log4j.Logger;

import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.util.RedeployUtilities;
import de.hybris.platform.util.Utilities;


/**
 * Demonstration of how to write a standalone application that can be run directly from within eclipse or from the
 * commandline.<br>
 * To run this from commandline, just use the following command:<br>
 * <code>
 * java -jar bootstrap/bin/ybootstrap.jar "new com.sap.hybris.SapcpqquoteintegrationaddonStandalone().run();"
 * </code> From eclipse, just run as Java Application. Note that you maybe need to add all other projects like
 * ext-commerce, ext-pim to the Launch configuration classpath.
 */
public class SapcpqquoteintegrationaddonStandalone
{
	private static final Logger LOG = Logger.getLogger(SapcpqquoteintegrationaddonStandalone.class.getName());
	/**
	 * Main class to be able to run it directly as a java program.
	 * 
	 * @param args
	 *           the arguments from commandline
	 */
	public static void main(final String[] args)
	{
		new SapcpqquoteintegrationaddonStandalone().run();
	}

	public void run()
	{
		Registry.activateStandaloneMode();
		Registry.activateMasterTenant();

		final JaloSession jaloSession = JaloSession.getCurrentSession();
		LOG.info("Session ID: " + jaloSession.getSessionID());
		LOG.info("User: " + jaloSession.getUser());
		Utilities.printAppInfo();

		RedeployUtilities.shutdown();
	}
}
