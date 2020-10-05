/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaintegration.inbound;



import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SapCpiInboundPosPersistenceHookTest
{
	@InjectMocks
	private final SapCpiInboundPosPersistenceHook sapCpiInboundPosPersistenceHook = new SapCpiInboundPosPersistenceHook();

	@Mock
	private ModelService modelService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExecute() {
		final String testLocationName = "testPOS";
		final String baseStoreId ="apparel-uk";


		final BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setUid(baseStoreId);
		final PointOfServiceModel posModel = new PointOfServiceModel();
		posModel.setName(testLocationName);
		posModel.setType(PointOfServiceTypeEnum.STORE);

		final AddressModel addressModel = new AddressModel();
		addressModel.setOwner(posModel);
		addressModel.setStreetname("Dachauer strasse");
		addressModel.setStreetnumber("5");
		addressModel.setPostalcode("80636");
		addressModel.setTown("Muenchen");
		final CountryModel country=new CountryModel();
		country.setIsocode("DE");
		addressModel.setCountry(country);

		posModel.setAddress(addressModel);

		when(modelService.isNew(any())).thenReturn(true);
		final Optional<ItemModel> result = sapCpiInboundPosPersistenceHook.execute(posModel);

		if (result.isPresent())
		{
			Assert.assertTrue(true);

		} else {
			Assert.assertTrue(false);
		}
	}
}
