package com.sap.hybris.sapcpqsbquotefacades.service.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSapcpqsbquotefacadesServiceTest {

	@Mock
	MediaService mediaService;

	@Mock
	ModelService modelService;

	@Mock
	FlexibleSearchService flexibleSearchService;

	@InjectMocks
	DefaultSapcpqsbquotefacadesService defaultSapcpqsbquotefacadesService;

	private static final String logoCode = "LOGO_CODE";

	@Before
	public void setUp(){

		//mock
		when(flexibleSearchService.searchUnique(any())).thenReturn(new CatalogUnawareMediaModel());

	}
	
	@Test
	public void testCreateLogo() {

		//Execute
		defaultSapcpqsbquotefacadesService.createLogo(logoCode);
	}

}
