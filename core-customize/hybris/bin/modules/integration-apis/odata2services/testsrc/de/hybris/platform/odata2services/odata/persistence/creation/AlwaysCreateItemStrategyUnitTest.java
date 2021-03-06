/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2services.odata.persistence.creation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.ItemModelFactory;
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.servicelayer.exceptions.ModelCreationException;

import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AlwaysCreateItemStrategyUnitTest
{
	@Mock
	private ItemModelFactory modelFactory;
	@Mock
	private StorageRequest storageRequest;

	@InjectMocks
	private AlwaysCreateItemStrategy strategy;

	@Before
	public void setUp() throws EdmException
	{
		final EdmEntityType entityType = mock(EdmEntityType.class);
		when(entityType.getName()).thenReturn("AType");
		when(storageRequest.getEntityType()).thenReturn(entityType);
		when(storageRequest.getODataEntry()).thenReturn(mock(ODataEntry.class));
	}


	@Test
	public void testItemCreation() throws EdmException
	{
		final ItemModel itemModel = mock(ItemModel.class);
		when(modelFactory.createItem(storageRequest)).thenReturn(itemModel);

		final ItemModel item = strategy.createItem(storageRequest);

		assertThat(item).isNotNull()
						.isSameAs(itemModel);
	}

	@Test
	public void testItemCreationFails()
	{
		when(modelFactory.createItem(storageRequest)).thenThrow(new ModelCreationException("message", new RuntimeException()));

		assertThatThrownBy(() -> strategy.createItem(storageRequest))
				.isInstanceOf(InternalProcessingException.class)
				.hasFieldOrPropertyWithValue("errorCode", "internal_error")
				.hasMessageContaining("There was an error encountered during the processing of the integration object.");
	}
}
