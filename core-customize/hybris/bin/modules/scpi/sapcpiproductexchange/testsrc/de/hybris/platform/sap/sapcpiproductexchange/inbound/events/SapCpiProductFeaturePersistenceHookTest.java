/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiproductexchange.inbound.events;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.*;
import de.hybris.platform.classification.ClassificationClassesResolverStrategy;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.*;

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCpiProductFeaturePersistenceHookTest {

    @InjectMocks
    private SapCpiProductFeaturePersistenceHook sapCpiProductFeaturePersistenceHook;

    private ProductModel productModel;

    @Mock
    private ProductFeatureModel productFeature;
    @Mock
    private ModelService modelService;

    @Mock
    private ClassificationSystemModel classificationSystemModel;
    @Mock
    private ClassificationClassModel dragonCar;
    @Mock
    private ClassificationAttributeModel size;
    @Mock
    private ClassAttributeAssignmentModel dragonCarSize;
    @Mock
    private CatalogVersionModel catalogVersionModel;
    @Mock
    private ClassificationSystemVersionModel classificationSystemVersion;
    @Mock
    private ClassificationClassesResolverStrategy classResolverStrategy;

    @Mock
    private ClassificationSystemService classificationSystemService;
    @Mock
    private ClassificationAttributeValueModel classificationAttributeValueModel;

    private final String PRODUCT_CODE = "WEC_DRAGON_CAR_96";
    private final String DRAGONCAR = "WEC_CDRAGON_CAR";
    private final int DARAGONCAR_SIZE_PK = 123456;
    private final String delimiterString ="|";

    @Before
    public void setup()
    {
        initializeClassificationClass(dragonCar, DRAGONCAR, DARAGONCAR_SIZE_PK, Arrays.asList(dragonCarSize));
        initializeClassAttributeAndAssignment(dragonCarSize, DARAGONCAR_SIZE_PK, dragonCar, size);
        initializeProductsAndCategoriesData();

    }
    @Test
    public void withoutSplittingSingleProductFeatureValue()
    {
        Mockito.when(dragonCarSize.getRange()).thenReturn(false);
        Mockito.when(dragonCarSize.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.ENUM);
        final String code = "Test, String";
        initializeAttributeValue(classificationAttributeValueModel,DARAGONCAR_SIZE_PK,code,classificationSystemVersion);

        Mockito.when(classificationSystemService.getAttributeValueForCode(classificationSystemVersion,"SIZE_"+code)).thenReturn(classificationAttributeValueModel);

        initializeProductFeatureModelValue(productFeature,"SIZE_"+code);
        initializeModelService(modelService,productFeature);
        Mockito.when(classResolverStrategy.resolve(productModel)).thenReturn(Sets.newHashSet(dragonCar));
        Mockito.when(classResolverStrategy.getAllClassAttributeAssignments(Sets.newHashSet(dragonCar))).thenReturn(Lists.newArrayList(dragonCarSize));

        sapCpiProductFeaturePersistenceHook.setCollectionDelimiter(delimiterString);

        final Optional<ItemModel> processedModel = sapCpiProductFeaturePersistenceHook.execute((ItemModel)productModel);
        assertThat(processedModel.isPresent()).isTrue();
        Assert.assertThat(processedModel.get(), equalTo(productModel));
        Assert.assertThat(((ProductModel)processedModel.get()).getFeatures().size(), equalTo(1));
    }
    @Test
    public void splitProductFeatureRangeValueByCollectionDelimiter()
    {
        initializeProductFeatureModelValue(productFeature,"SIZE_1.000000000000000E+03"+delimiterString+"0.000000000000000E+00");
        initializeModelService(modelService,productFeature);
        Mockito.when(classResolverStrategy.resolve(productModel)).thenReturn(Sets.newHashSet(dragonCar));
        Mockito.when(classResolverStrategy.getAllClassAttributeAssignments(Sets.newHashSet(dragonCar))).thenReturn(Lists.newArrayList(dragonCarSize));

        sapCpiProductFeaturePersistenceHook.setCollectionDelimiter(delimiterString);

        final Optional<ItemModel> processedModel = sapCpiProductFeaturePersistenceHook.execute((ItemModel)productModel);
        assertThat(processedModel.isPresent()).isTrue();
        Assert.assertThat(processedModel.get(), equalTo(productModel));
        Assert.assertThat(((ProductModel)processedModel.get()).getFeatures().size(), equalTo(2));
    }

    private void initializeProductsAndCategoriesData() {
        productModel = new ProductModel();
        productModel.setCode(PRODUCT_CODE);
        productModel.setCatalogVersion(catalogVersionModel);
        productModel.setFeatures(Lists.newArrayList(productFeature));
    }

    private void initializeClassAttributeAndAssignment(final ClassAttributeAssignmentModel assignment, final int pk,
                                                    final ClassificationClassModel parentClass, final ClassificationAttributeModel attribute)
    {
        Mockito.when(classificationSystemVersion.getPk()).thenReturn(PK.fromLong(pk));
        Mockito.when(attribute.getName()).thenReturn("Size");
        Mockito.when(assignment.getPk()).thenReturn(PK.fromLong(pk));
        Mockito.when(assignment.getClassificationClass()).thenReturn(parentClass);
        Mockito.when(assignment.getClassificationAttribute()).thenReturn(attribute);
        Mockito.when(assignment.getRange()).thenReturn(true);
        Mockito.when(assignment.getMandatory()).thenReturn(false);
        Mockito.when(assignment.getClassificationAttribute().getCode()).thenReturn("SIZE");
        Mockito.when(assignment.getSystemVersion()).thenReturn(classificationSystemVersion);
        Mockito.when(assignment.getSystemVersion().getCatalog()).thenReturn(classificationSystemModel);
        Mockito.when(assignment.getSystemVersion().getCatalog().getId()).thenReturn("ERP_CLASSIFICATION_001");
        Mockito.when(assignment.getMultiValued()).thenReturn(true);
        Mockito.when(assignment.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.NUMBER);
    }

    private void initializeAttributeValue(final ClassificationAttributeValueModel attributeValueModel,final int pk, final String code, final ClassificationSystemVersionModel classificationSystemVersion)
    {
        Mockito.when(attributeValueModel.getPk()).thenReturn(PK.fromLong(pk));
        Mockito.when(attributeValueModel.getCode()).thenReturn(code);
        Mockito.when(attributeValueModel.getSystemVersion()).thenReturn(classificationSystemVersion);
    }
    private void initializeProductFeatureModelValue(final ProductFeatureModel productFeature, final String value)
    {
        Mockito.when(productFeature.getProduct()).thenReturn(productModel);
        Mockito.when(productFeature.getQualifier()).thenReturn("ERP_CLASSIFICATION_001/ERP_IMPORT/SIZE");
        Mockito.when(productFeature.getValuePosition()).thenReturn(1);
        Mockito.when(productFeature.getValue()).thenReturn(value);
    }

    private void initializeClassificationClass(final ClassificationClassModel classificationClass, final String name,
                                               final long pk, final List<ClassAttributeAssignmentModel> classAttributeAssignments)
    {
        Mockito.when(classificationClass.getName()).thenReturn(name);
        Mockito.when(classificationClass.getDeclaredClassificationAttributeAssignments()).thenReturn(classAttributeAssignments);
        Mockito.when(classificationClass.getCatalogVersion()).thenReturn(classificationSystemVersion);
        Mockito.when(classificationClass.getPk()).thenReturn(PK.fromLong(pk));
    }

    private void initializeModelService(final ModelService modelService, final ProductFeatureModel productFeature)
    {
        ProductFeatureModel feature = new ProductFeatureModel();
        feature.setProduct(productFeature.getProduct());
        feature.setQualifier(productFeature.getQualifier());
        feature.setAuthor(productFeature.getAuthor());
        Mockito.when(modelService.clone(productFeature)).thenReturn(feature);
    }

}
