/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbquotefacades;

import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.sap.saprevenuecloudorder.SaprevenuecloudorderStandalone;
import de.hybris.platform.util.RedeployUtilities;
import de.hybris.platform.util.Utilities;
import org.apache.log4j.Logger;


/**
 * Demonstration of how to write a standalone application that can be run directly from within eclipse or from the
 * commandline.<br>
 * To run this from commandline, just use the following command:<br>
 * <code>
 * java -jar bootstrap/bin/ybootstrap.jar "new com.sap.hybris.sapcpqsbquotefacades.SapcpqsbquotefacadesStandalone().run();"
 * </code> From eclipse, just run as Java Application. Note that you maybe need to add all other projects like
 * ext-commerce, ext-pim to the Launch configuration classpath.
 */
public class SapcpqsbquotefacadesStandalone
{
	private static final Logger LOG = Logger.getLogger(SaprevenuecloudorderStandalone.class);

	/**
	 * Main class to be able to run it directly as a java program.
	 *
	 * @param args the arguments from commandline
	 */
	public static void main(final String[] args)
	{
		new SapcpqsbquotefacadesStandalone().run();
	}

	public void run()
	{
		Registry.activateStandaloneMode();
		Registry.activateMasterTenant();

		final JaloSession jaloSession = JaloSession.getCurrentSession();
		LOG.info(String.format("Session ID: %s", jaloSession.getSessionID()));  //NOPMD
		LOG.info(String.format("User: %s", jaloSession.getUser()));  //NOPMD
		Utilities.printAppInfo();

		RedeployUtilities.shutdown();
	}
}
