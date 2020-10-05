/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.retail.oaa.model.model.ScheduleLineModel;
import com.sap.sapoaacosintegration.services.reservation.request.CosReservationRequest;
import com.sap.sapoaacosintegration.services.reservation.request.CosReservationRequestItem;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCosReservationRequestMapperTest
{

	@InjectMocks
	DefaultCosReservationRequestMapper mockDefaultCosReservationRequestMapper;

	private final Map<String, CosReservationRequestItem> mapReservationRequestItems = new HashMap<>();

	private final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
	private final AbstractOrderEntryModel abstractOrderEntryModelFirst = new AbstractOrderEntryModel();
	private final AbstractOrderEntryModel abstractOrderEntryModelSecond = new AbstractOrderEntryModel();
	private final List<AbstractOrderEntryModel> listAbstractOrderEntryModel = new ArrayList<>();
	private final ProductModel productModel = new ProductModel();
	private final ScheduleLineModel scheduleLineModel = new ScheduleLineModel();
	private final List<ScheduleLineModel> listScheduleLineModel = new ArrayList<>();
	private final UnitModel unitModel = new UnitModel();
	private final PointOfServiceModel pointOfServiceModel = new PointOfServiceModel();

	@Before
	public void setUp()
	{
		//ProductModel
		productModel.setCode("ABC");

		//UnitModel
		unitModel.setCode("EA");

		//ScheduleLineModel
		scheduleLineModel.setConfirmedQuantity(100d);

		//ListScheduleLineModel
		listScheduleLineModel.add(scheduleLineModel);

		//PointOfServiceModel
		pointOfServiceModel.setName("NAME");
		pointOfServiceModel.setType(PointOfServiceTypeEnum.STORE);

		//AbstractOrderEntryModel
		abstractOrderEntryModelFirst.setEntryNumber(10201);
		abstractOrderEntryModelFirst.setProduct(productModel);
		abstractOrderEntryModelFirst.setScheduleLines(listScheduleLineModel);
		abstractOrderEntryModelFirst.setUnit(unitModel);
		abstractOrderEntryModelFirst.setSapSource(pointOfServiceModel);
		abstractOrderEntryModelFirst.setSapVendor("VENDOR");

		abstractOrderEntryModelSecond.setEntryNumber(10202);
		abstractOrderEntryModelSecond.setProduct(productModel);
		abstractOrderEntryModelSecond.setScheduleLines(listScheduleLineModel);
		abstractOrderEntryModelSecond.setUnit(unitModel);
		abstractOrderEntryModelSecond.setSapSource(pointOfServiceModel);
		abstractOrderEntryModelSecond.setSapVendor(null);

		//ListAbstractOrderEntryModel
		listAbstractOrderEntryModel.add(abstractOrderEntryModelFirst);
		listAbstractOrderEntryModel.add(abstractOrderEntryModelSecond);

		//AbstractOrderModel
		abstractOrderModel.setEntries(listAbstractOrderEntryModel);

	}

	@Test
	public void testDefaultCosReservationRequestMapper()
	{
		//SetUp
		List<CosReservationRequestItem> listCosReservationRequestItem = new ArrayList();
		final CosReservationRequest request = new CosReservationRequest();
		final String cartItemId = "102341";

		//Execute
		listCosReservationRequestItem = mockDefaultCosReservationRequestMapper.mapOrderModelToReservationRequest(abstractOrderModel,
				request, cartItemId);

		//Verify
		Assert.assertNotNull(listCosReservationRequestItem);
		Assert.assertTrue(listCosReservationRequestItem.size() != 0);
	}
}
