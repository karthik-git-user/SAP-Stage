/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchangemdm.interceptors;

import com.sap.hybris.sapcustomerb2c.outbound.DefaultAddressInterceptor;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;


/**
 * If address other that default was updated send address to Back End in case of
 * user replication is active and the address is related to a sap consumer. This
 * is indicated by the filled sap contact id.
 */
public class DefaultMDMAddressInterceptor extends DefaultAddressInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMDMAddressInterceptor.class);

	@Override
	public void onValidate(AddressModel addressModel, InterceptorContext ctx) throws InterceptorException {

	    if (!getCustomerExportService().isCustomerReplicationEnabled()) {
	        if (LOGGER.isDebugEnabled()) {
	          LOGGER.debug("'Replicate Registered Users' flag in 'SAP Global Configuration' is set to 'false'.");
	          LOGGER.debug("Address " + addressModel.getPk() + " was not sent to Data Hub.");
	        }
	        return;
	    }
	    
		if (isAddressChangeRelevantToMdm(addressModel)) {
			onValidateMdmChanges(addressModel, ctx);
		} else {
			super.onValidate(addressModel, ctx);
		}

	}

	protected boolean isAddressChangeRelevantToMdm(AddressModel addressModel) {

		if (!(addressModel.getOwner() instanceof CustomerModel)) {
			return false;
		}

		final CustomerModel customerModel = ((CustomerModel) addressModel.getOwner());

		// Check if the address is a default shipment address
		if (addressModel.equals(customerModel.getDefaultShipmentAddress())) {
			LOGGER.info(String.format("The changed address [%s] is a default address will  be sent to MDM!!",
					addressModel.getPk()));
			return true;
		} else {
			LOGGER.info(String.format("The changed address [%s] is not a default address and it will be sent to MDM!",
					addressModel.getPk()));
			return true;
		}

	}

	protected void onValidateMdmChanges(final AddressModel addressModel, final InterceptorContext ctx) {

		final CustomerModel customerModel = addressModel.getOwner() instanceof CustomerModel
				? ((CustomerModel) addressModel.getOwner())
				: null;
		if (ctx.isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS)) {
			LOGGER.warn("Changes in the default address that will not be sent to MDM!");
			return;
		}

		// Check if one of the address attributes has changed
		if (isModelModified(addressModel)) {
			LOGGER.info("The address [{}] has changed and will be sent to MDM!", addressModel.getPk());
			updateAndSendCustomerData(addressModel, customerModel);
		} else {
			LOGGER.info("The address [{}] has not changed and nothing will be sent to MDM!", addressModel.getPk());
		}
	}

	protected void updateAndSendCustomerData(final AddressModel addressModel, final CustomerModel customerModel) {

		final String baseStoreUid = getBaseStoreService().getCurrentBaseStore() != null
				? getBaseStoreService().getCurrentBaseStore().getUid()
				: null;
		
		String sessionLanguage = null;
		
		if (customerModel.getSessionLanguage() != null) {
			sessionLanguage = customerModel.getSessionLanguage().getIsocode();
		} else if (getStoreSessionFacade().getDefaultLanguage()!= null){
			sessionLanguage = getStoreSessionFacade().getDefaultLanguage().getIsocode();
		}

		getCustomerExportService().sendCustomerData(customerModel, baseStoreUid, sessionLanguage, addressModel);

	}

	protected boolean isModelModified(final AddressModel addressModel) {

		final List<String> modelAttributes = new ArrayList<>();

		modelAttributes.add(AddressModel.COUNTRY);
		modelAttributes.add(AddressModel.REGION);
		modelAttributes.add(AddressModel.TITLE);

		modelAttributes.add(AddressModel.STREETNAME);
		modelAttributes.add(AddressModel.STREETNUMBER);

		modelAttributes.add(AddressModel.TOWN);
		modelAttributes.add(AddressModel.POSTALCODE);
		modelAttributes.add(AddressModel.PHONE1);
		modelAttributes.add(AddressModel.LINE1);
		modelAttributes.add(AddressModel.LINE2);
		modelAttributes.add(AddressModel.FIRSTNAME);
		modelAttributes.add(AddressModel.LASTNAME);

		modelAttributes.add(AddressModel.TITLE);

		return !modelAttributes.stream()
				.filter(modelAttribute -> hasModelAttributeChanged(addressModel, modelAttribute)).findAny().isEmpty();

	}

	protected boolean hasModelAttributeChanged(final AddressModel addressModel, final String modelAttribute) {

		final ItemModelContextImpl context = (ItemModelContextImpl) addressModel.getItemModelContext();
		Object originalValue = context.isLoaded(modelAttribute) ? context.getOriginalValue(modelAttribute) : null;
		Object changedValue = addressModel.getProperty(modelAttribute);
		if (isNull(originalValue) && isNull(changedValue)) {
			return false;
		}
		boolean equals = !isNull(originalValue) ? originalValue.equals(addressModel.getProperty(modelAttribute))
				: false;
		if (!equals) {
			LOGGER.info("Address attribute [{}], has been  updated!", modelAttribute);
		}

		return !equals;

	}

	public static boolean isNull(Object obj) {
        if (obj != null) {
            return false;
        }
		return true;
	}

}
