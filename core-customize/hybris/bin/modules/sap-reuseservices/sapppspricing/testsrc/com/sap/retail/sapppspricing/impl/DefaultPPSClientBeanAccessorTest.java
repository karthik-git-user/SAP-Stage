/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import static org.junit.Assert.*;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;

import com.sap.ppengine.client.util.PPSClientBeanAccessor;
import com.sap.retail.sapppspricing.impl.DefaultPPSClientBeanAccessor;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultPPSClientBeanAccessorTest
{
	private DefaultPPSClientBeanAccessor cut;

	@Test
	public void testInstanceOf()
	{
		cut = new DefaultPPSClientBeanAccessor();
		assertTrue(cut instanceof PPSClientBeanAccessor);
	}
}
