/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import com.sap.ppengine.client.util.PPSClientBeanAccessor;
import com.sap.ppengine.client.util.impl.PPSClientBeanAccessorImpl;


/**
 * PPS bean accessor as a subclass of the one provided by PPS. Just introduced to avoid too strong coupling - the PPS
 * class is located in the client-impl module which would not be present in case of a remote scenario. Consumers of this
 * class should not rely on this class being an instance of {@link PPSClientBeanAccessorImpl}. They should only rely on
 * this class implementing {@link PPSClientBeanAccessor}.
 */
public class DefaultPPSClientBeanAccessor extends PPSClientBeanAccessorImpl
{

}
