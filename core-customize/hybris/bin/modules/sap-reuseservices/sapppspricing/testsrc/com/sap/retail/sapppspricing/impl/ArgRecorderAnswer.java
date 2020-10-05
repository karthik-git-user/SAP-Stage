/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class ArgRecorderAnswer<T> implements Answer<T>
{
	private Object[] args;

	public T answer(final InvocationOnMock invocation) throws Throwable
	{
		args = invocation.getArguments();
		return null;
	}

	@SuppressWarnings("unchecked")
	public <X> X getArg(final int index)
	{
		return (X) args[index];
	}

	public int getArgCount()
	{
		return args == null ? 0 : args.length;
	}
}
