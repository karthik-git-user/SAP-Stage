package com.sap.hybris.sapcpqsbintegration.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapcpqsbintegrationServiceTest {
	
	@Mock
	MediaService mockMediaService;
	
	@Mock
	MediaModel mockMediaModel;
	
	@Mock
	private ModelService mockModelService;
	
	@Mock
	FlexibleSearchService mockFlexibleSearchService;
	
	@InjectMocks
	DefaultSapcpqsbintegrationService defaultSapcpqsbintegrationService;

	private static final String LOGO_CODE = "LOGO_CODE";
	private static final String URL = "URL";
	
	private final CatalogUnawareMediaModel catalogUnawareMediaModel = new CatalogUnawareMediaModel();
	
	@Before
	public void setUp() {
		when(mockFlexibleSearchService.searchUnique(any())).thenReturn(catalogUnawareMediaModel);
	}
	
	@Test
	public void getHybrisLogoUrlTest() {
		
		when(mockMediaService.getMedia(any())).thenReturn(mockMediaModel);
		when(mockMediaModel.getCode()).thenReturn("CODE");
		when(mockMediaModel.getURL()).thenReturn(URL);
		
		String result;
		result = defaultSapcpqsbintegrationService.getHybrisLogoUrl(LOGO_CODE);
		Assert.assertEquals(URL, result);
	}
	
	@Test
	public void createLogoTest() {
		doNothing().when(mockModelService).save(any());
		doNothing().when(mockMediaService).setStreamForMedia(any(), any());
		
		defaultSapcpqsbintegrationService.createLogo(LOGO_CODE);
		
		//Nothing to Assert
	}
}
