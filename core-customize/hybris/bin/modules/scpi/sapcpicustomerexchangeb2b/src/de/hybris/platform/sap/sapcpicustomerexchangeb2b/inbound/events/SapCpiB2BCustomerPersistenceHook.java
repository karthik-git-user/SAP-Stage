/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchangeb2b.inbound.events;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Pre-persistence hook to be triggered from the BP relationship confirmation after creating a new B2B customer from the B2B storefront to
 * replace the value of customerID by sapBusinessPartnerID (PARNR) and the value of sapBusinessPartnerID by customerID(BP ID).
 */
public class SapCpiB2BCustomerPersistenceHook implements PrePersistHook {

  private static final Logger LOG = LoggerFactory.getLogger(SapCpiB2BCustomerPersistenceHook.class);

  private ModelService modelService;
  private FlexibleSearchService flexibleSearchService;

  @Override
  public Optional<ItemModel> execute(ItemModel item) {

    if (item instanceof B2BCustomerModel) {

      LOG.info("The persistence hook sapCpiB2BCustomerPersistenceHook is called!");
      final B2BCustomerModel inboundB2BCustomerModel = (B2BCustomerModel) item;

      // Retrieve B2B Customer
      final B2BCustomerModel b2bCustomerModel = readB2BCustomer(inboundB2BCustomerModel.getCustomerID());

      // Switch the values of customerID(BP ID) <-> sapBusinessPartnerID(PARNR)
      b2bCustomerModel.setSapBusinessPartnerID(inboundB2BCustomerModel.getCustomerID());
      b2bCustomerModel.setCustomerID(inboundB2BCustomerModel.getSapBusinessPartnerID());

      getModelService().save(b2bCustomerModel);

      return Optional.empty();

    }

    return Optional.of(item);

  }


  protected B2BCustomerModel readB2BCustomer(final String customerId) {

    final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("SELECT {c:pk} FROM {B2BCustomer AS c} WHERE  {c.customerID} = ?customerID");
    flexibleSearchQuery.addQueryParameter("customerID", customerId);

    final B2BCustomerModel b2bCustomer = getFlexibleSearchService().searchUnique(flexibleSearchQuery);
    if (b2bCustomer == null) {
      throw new IllegalArgumentException(String.format("Error while reading B2B Customer with customer ID [%s]!", customerId));
    }
    return b2bCustomer;

  }

  protected ModelService getModelService() {
    return modelService;
  }

  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  public FlexibleSearchService getFlexibleSearchService() {
    return flexibleSearchService;
  }

  public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
    this.flexibleSearchService = flexibleSearchService;
  }

}
