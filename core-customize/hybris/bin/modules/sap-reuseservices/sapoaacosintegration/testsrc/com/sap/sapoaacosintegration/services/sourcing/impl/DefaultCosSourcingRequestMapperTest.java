/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.sapoaacosintegration.services.common.util.CosServiceUtils;
import com.sap.sapoaacosintegration.services.config.CosConfigurationService;
import com.sap.sapoaacosintegration.services.sourcing.CosSourcingResultHandler;
import com.sap.sapoaacosintegration.services.sourcing.request.CosSourcingRequest;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCosSourcingRequestMapperTest
{
	@Mock
	CosConfigurationService cosConfigurationService;

	@Mock
	CosServiceUtils cosServiceUtils;

	@Mock
	CosSourcingResultHandler cosSourcingResultHandler;

	@Mock
	Converter<AddressModel, AddressData> addressConverter;

	@Mock
	GPS gps;

	@InjectMocks
	DefaultCosSourcingRequestMapper defaultCosSourcingRequestMapper;

	private final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
	private final AbstractOrderEntryModel abstractOrderEntryModelFirst = new AbstractOrderEntryModel();
	private final AbstractOrderEntryModel abstractOrderEntryModelSecond = new AbstractOrderEntryModel();
	private final List<AbstractOrderEntryModel> listAbstractOrderEntryModel = new ArrayList<>();
	private final PointOfServiceModel pointOfServiceModel = new PointOfServiceModel();
	private final ProductModel productModel = new ProductModel();
	private final UnitModel unitModel = new UnitModel();
	private final AddressData addressData = new AddressData();
	private final AddressModel addressModel = new AddressModel();
	private final Double latitude = 100.142431d;
	private final Double longitude = 200.27261d;
	private final String STRATEGY_ID = "STRATEGY";

	@Before
	public void setUp()
	{
		defaultCosSourcingRequestMapper.setConfigurationService(cosConfigurationService);
		defaultCosSourcingRequestMapper.setCosServiceUtils(cosServiceUtils);
		defaultCosSourcingRequestMapper.setCosSourcingResultHandler(cosSourcingResultHandler);
		defaultCosSourcingRequestMapper.setAddressConverter(addressConverter);

		//Mocks
		when(cosConfigurationService.getCosCasStrategyId()).thenReturn(STRATEGY_ID);
		when(addressConverter.convert(any(AddressModel.class), any(AddressData.class))).thenReturn(addressData);
		when(cosServiceUtils.fetchDestinationCoordinates(any(AddressData.class))).thenReturn(gps);
		when(cosServiceUtils.generateItemNumber()).thenReturn("12321");
		when(gps.getDecimalLatitude()).thenReturn(latitude);
		when(gps.getDecimalLongitude()).thenReturn(longitude);
		doNothing().when(cosSourcingResultHandler).populateScheduleLinesForPickupProduct(any(AbstractOrderEntryModel.class));

		//ProductModel
		productModel.setCode("ProductCode");

		//untiModel
		unitModel.setSapCode("SapCode");

		//AbstractOrderEntryModel
		abstractOrderEntryModelFirst.setCosOrderItemId("12321");
		abstractOrderEntryModelFirst.setDeliveryPointOfService(pointOfServiceModel);
		abstractOrderEntryModelFirst.setProduct(productModel);
		abstractOrderEntryModelFirst.setUnit(unitModel);
		abstractOrderEntryModelFirst.setQuantity((long) 1000);

		abstractOrderEntryModelSecond.setCosOrderItemId(null);
		abstractOrderEntryModelSecond.setDeliveryPointOfService(null);
		abstractOrderEntryModelSecond.setProduct(productModel);
		abstractOrderEntryModelSecond.setUnit(unitModel);
		abstractOrderEntryModelSecond.setQuantity((long) 1000);

		//ListAbstractOrderEntryModel
		listAbstractOrderEntryModel.add(abstractOrderEntryModelFirst);
		listAbstractOrderEntryModel.add(abstractOrderEntryModelSecond);

		//AbstractOrderModel
		abstractOrderModel.setEntries(listAbstractOrderEntryModel);
		abstractOrderModel.setDeliveryAddress(addressModel);

	}

	@Test
	public void prepareCosSourcingRequestTest()
	{
		//SetUp
		CosSourcingRequest cosSourcingRequest;

		//Execute
		cosSourcingRequest = defaultCosSourcingRequestMapper.prepareCosSourcingRequest(abstractOrderModel);

		//Verify
		Assert.assertNotNull(cosSourcingRequest);
		Assert.assertEquals(STRATEGY_ID, cosSourcingRequest.getStrategyId());
		Assert.assertEquals(latitude, (Double) cosSourcingRequest.getDestinationCoordinates().getLatitude());
		Assert.assertEquals(longitude, (Double) cosSourcingRequest.getDestinationCoordinates().getLongitude());
	}
}
