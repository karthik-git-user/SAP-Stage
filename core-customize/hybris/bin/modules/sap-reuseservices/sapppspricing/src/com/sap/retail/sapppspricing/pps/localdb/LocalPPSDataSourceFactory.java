/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.pps.localdb;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.sap.ppengine.dataaccess.localdb.AbstractDataSourceFactory;


/**
 * Factory for {@link org.apache.commons.dbcp.BasicDataSource} with automatic setting of validation query
 */
public class LocalPPSDataSourceFactory extends AbstractDataSourceFactory
{

	@Override
	public DataSource create()
	{
		// Not a Tomcat DataSource any more - this is not on the compile path... :-|
		final BasicDataSource result = new BasicDataSource();
		result.setDriverClassName(getDriverClassName());
		result.setUrl(getUrl());
		result.setUsername(getUsername());
		result.setPassword(getPassword());
		result.setInitialSize(getInitialSize());
		result.setMaxActive(getMaxActive());
		result.setValidationQuery(determineValidationQuery());
		return result;
	}
}
