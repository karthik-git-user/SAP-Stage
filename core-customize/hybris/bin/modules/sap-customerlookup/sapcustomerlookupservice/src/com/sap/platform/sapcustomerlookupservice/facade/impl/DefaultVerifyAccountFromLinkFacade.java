/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.facade.impl;

import com.sap.platform.sapcustomerlookupservice.events.CustomerLookupEvent;
import com.sap.platform.sapcustomerlookupservice.facade.VerifyAccountFromEmailLinkFacade;
import com.sap.platform.sapcustomerlookupservice.token.DefaultSecureTokenServiceForCMS;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.commerceservices.event.RegisterEvent;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * Verify customer email token and then make address visible
 */
public class DefaultVerifyAccountFromLinkFacade implements VerifyAccountFromEmailLinkFacade {

    private static final Logger LOG = Logger.getLogger(DefaultVerifyAccountFromLinkFacade.class);
    private DefaultSecureTokenServiceForCMS tokenServiceForCMS;
    private SecureTokenService secureTokenService;
    private UserService userService;
    private EventService eventService;
    private BaseSiteService baseSiteService;
    private CommerceCommonI18NService commerceCommonI18NService;
    private BaseStoreService baseStoreService;
    private ModelService modelService;

    public void setTokenServiceForCMS(DefaultSecureTokenServiceForCMS tokenServiceForCMS) {
        this.tokenServiceForCMS = tokenServiceForCMS;
    }

    public void setSecureTokenService(SecureTokenService secureTokenService) {
        this.secureTokenService = secureTokenService;
    }

    public void setCommerceCommonI18NService(CommerceCommonI18NService commerceCommonI18NService) {
        this.commerceCommonI18NService = commerceCommonI18NService;
    }

    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }


    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean verifyEmail(String token) throws TokenInvalidatedException {

        SecureToken data = null;
        try {
            data = secureTokenService.decryptData(token);
        } catch (IllegalArgumentException e) {
            LOG.error("Wrong Token provided. Cause = " + e.getMessage());
            throw new TokenInvalidatedException("Wrong token provided by the customer", e);
        }

        final CustomerModel customer = getUserService().getUserForUID(data.getData(), CustomerModel.class);
        if (customer == null) {
            LOG.error("Customer details not found in the request");
            throw new IllegalArgumentException("user for token not found");
        }


        if (tokenServiceForCMS.checkValidityOfToken(customer.getCmsLookupSecureToken())) {
            if (!token.equals(customer.getCmsLookupSecureToken())) {
                LOG.error("CMS Email Verification failed for customer : " + customer.getUid());
                throw new TokenInvalidatedException("Token verification failed for customer");
            }
            customer.setCmsLookupSecureToken(null);
            customer.setCmsEmailVerificationTimestamp(new Date());
            customer.setLoginDisabled(false);
            LOG.info("CMS Email verification done for Customer : " + customer.getUid());
            List<AddressModel> addressList = (List<AddressModel>) customer.getAddresses();
            for (AddressModel address : addressList) {
                if (address.getSapAddressUUID() != null) {
                    address.setVisibleInAddressBook(true);

                }

            }
            modelService.saveAll(customer);

            raiseCustomerMasterLookupEvent(new CustomerLookupEvent(), customer);
            return true;
        } else {
            raiseRequestForNewEmail(new RegisterEvent(), customer);
            LOG.info("New Email Generated for Customer as token expired. Sending Email to : " + customer.getUid());

        }

        return false;


    }

    private void raiseCustomerMasterLookupEvent(final AbstractCommerceUserEvent customerMasterLookupEvent, final CustomerModel customerModel) {

        eventService.publishEvent(initializeEvent(customerMasterLookupEvent, customerModel));

    }

    private void raiseRequestForNewEmail(final AbstractCommerceUserEvent customerRegisteredEvent, final CustomerModel customerModel) {

        eventService.publishEvent(initializeEvent(customerRegisteredEvent, customerModel));

    }

    protected AbstractCommerceUserEvent initializeEvent(final AbstractCommerceUserEvent event, final CustomerModel customerModel) {
        event.setBaseStore(baseStoreService.getCurrentBaseStore());
        event.setSite(baseSiteService.getCurrentBaseSite());
        event.setCustomer(customerModel);
        event.setLanguage(commerceCommonI18NService.getCurrentLanguage());
        event.setCurrency(commerceCommonI18NService.getCurrentCurrency());
        return event;
    }
}
