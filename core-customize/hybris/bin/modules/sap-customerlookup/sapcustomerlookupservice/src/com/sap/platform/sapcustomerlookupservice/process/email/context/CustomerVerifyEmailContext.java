/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CustomerVerifyEmailContext extends AbstractEmailContext<StoreFrontCustomerProcessModel> {
    private static final Logger LOG = Logger.getLogger(CustomerVerifyEmailContext.class);
    private Converter<UserModel, CustomerData> customerConverter;
    private CustomerData customerData;
    private String token;
    private String verifyEmailTokenUrl;


    @Override
    public void init(final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel, final EmailPageModel emailPageModel) {
        super.init(storeFrontCustomerProcessModel, emailPageModel);
        customerData = getCustomerConverter().convert(getCustomer(storeFrontCustomerProcessModel));
        if (storeFrontCustomerProcessModel.getStore().isCmsEmailVerificationEnabled()) {
            setToken(storeFrontCustomerProcessModel.getCustomer().getCmsLookupSecureToken());
            verifyEmailTokenUrl = getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), getUrlEncodingAttributes(), true, "/verify/email",
                    "token=" + getURLEncodedToken());
        }
    }

    @Override
    protected BaseSiteModel getSite(final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel) {
        return storeFrontCustomerProcessModel.getSite();
    }

    @Override
    protected CustomerModel getCustomer(final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel) {
        return storeFrontCustomerProcessModel.getCustomer();
    }

    protected Converter<UserModel, CustomerData> getCustomerConverter() {
        return customerConverter;
    }


    public void setCustomerConverter(final Converter<UserModel, CustomerData> customerConverter) {
        this.customerConverter = customerConverter;
    }

    public CustomerData getCustomer() {
        return customerData;
    }

    @Override
    protected LanguageModel getEmailLanguage(final StoreFrontCustomerProcessModel businessProcessModel) {
        return businessProcessModel.getLanguage();
    }


    public void setToken(final String token) {
        this.token = token;
    }

    public String getVerifyEmailTokenUrl() {
        return verifyEmailTokenUrl;
    }

    public String getURLEncodedToken() {
        try {
            if (token != null) {
                return URLEncoder.encode(token, "UTF-8");
            }
            return null;
        } catch (UnsupportedEncodingException e) {
            LOG.error("Email verification token was not able to be encoded.", e);
            return null;
        }
    }

}
