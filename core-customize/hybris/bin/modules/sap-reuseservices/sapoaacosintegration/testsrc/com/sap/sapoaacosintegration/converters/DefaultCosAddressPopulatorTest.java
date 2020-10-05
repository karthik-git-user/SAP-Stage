/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.converters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.storelocator.data.AddressData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCosAddressPopulatorTest
{

	/**
	 * 
	 */
	private static final String CITY = "Bengaluru";
	/**
	 * 
	 */
	private static final String STREET = "Richmond street";
	/**
	 * 
	 */
	private static final String ZIP = "560001";
	private DefaultCosAddressPopulator addressPopulator = null;

	@Before
	public void setUp()
	{
		addressPopulator = new DefaultCosAddressPopulator();
	}

	@Test
	public void populateTest()
	{
		//SetUp
		final AddressModel source = new AddressModel();
		final AddressData target = new AddressData();

		final CountryModel countryModel = new CountryModel();
		countryModel.setIsocode("IN");

		source.setCountry(countryModel);
		source.setTown(CITY);
		source.setStreetname(STREET);
		source.setPostalcode(ZIP);



		//Execute
		addressPopulator.populate(source, target);

		//Verify
		Assert.assertEquals(CITY, target.getCity());
		Assert.assertEquals(STREET, target.getStreet());
		Assert.assertEquals(ZIP, target.getZip());


	}


}
