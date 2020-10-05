/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomermasterlookupaddon.controllers.pages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.platform.sapcustomerlookupservice.facade.CustomerMasterConsentFacade;
import com.sap.platform.sapcustomermasterlookupaddon.constants.SapcustomerlookupaddonWebConstants;
import com.sap.platform.sapcustomermasterlookupaddon.controllers.imported.CheckoutController;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.ConsentForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.GuestRegisterForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.consent.data.AnonymousConsentData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static de.hybris.platform.commercefacades.constants.CommerceFacadesConstants.CONSENT_GIVEN;

/**
 * Overrding the default behavior of checkout controller for Customer master lookup addon
 */
@Controller
@RequestMapping(value = "/checkout")
public class CustomerLookupCheckoutController extends CheckoutController {

    @Resource
    private UserService userService;

    private static final Logger LOG = Logger.getLogger(CustomerLookupCheckoutController.class);

    private static final String CONSENT_FORM_GLOBAL_ERROR = "consent.form.global.error";

    @Resource(name = "consentFacade")
    protected CustomerMasterConsentFacade cmsConsentFacade;


    protected String processRegisterGuestUserRequest(final GuestRegisterForm form, final BindingResult bindingResult,
                                                     final Model model, final HttpServletRequest request, final HttpServletResponse response,
                                                     final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        if (bindingResult.hasErrors()) {
            form.setTermsCheck(false);
            GlobalMessages.addErrorMessage(model, "form.global.error");
            return processOrderCode(form.getOrderCode(), model, request, redirectModel);
        }
        try {
            getCustomerFacade().changeGuestToCustomer(form.getPwd(), form.getOrderCode());
            getAutoLoginStrategy().login(getCustomerFacade().getCurrentCustomer().getUid(), form.getPwd(), request, response);
            userService.setCurrentUser(userService.getAnonymousUser());
            getSessionService().removeAttribute(WebConstants.ANONYMOUS_CHECKOUT);
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
                    "registration.confirmation.message.title");
        } catch (final DuplicateUidException e) {
            // User already exists
            LOG.debug("guest registration failed.");
            form.setTermsCheck(false);
            model.addAttribute(new GuestRegisterForm());
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                    "guest.checkout.existingaccount.register.error", new Object[]
                            {form.getUid()});
            return REDIRECT_PREFIX + request.getHeader("Referer");
        }

        // Consent form data
        try {
            final ConsentForm consentForm = form.getConsentForm();
            if (consentForm != null && consentForm.getConsentGiven()) {
                cmsConsentFacade.giveConsentForEmailId(form.getUid(), consentForm.getConsentTemplateId(), consentForm.getConsentTemplateVersion());

            }
        } catch (final Exception e) {
            LOG.error("Error occurred while creating consents during registration", e);
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, CONSENT_FORM_GLOBAL_ERROR);
        }

        // save anonymous-consent cookies as ConsentData
        final Cookie cookie = WebUtils.getCookie(request, WebConstants.ANONYMOUS_CONSENT_COOKIE);
        if (cookie != null) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                final List<AnonymousConsentData> anonymousConsentDataList = Arrays.asList(mapper.readValue(
                        URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.displayName()), AnonymousConsentData[].class));
                anonymousConsentDataList.stream().filter(consentData -> CONSENT_GIVEN.equals(consentData.getConsentState()))
                        .forEach(consentData -> consentFacade.giveConsent(consentData.getTemplateCode(),
                                Integer.valueOf(consentData.getTemplateVersion())));
            } catch (final UnsupportedEncodingException e) {
                LOG.error(SapcustomerlookupaddonWebConstants.COOKIE_DATA_DECODE_ERROR, e);
            } catch (final IOException e) {
                LOG.error(SapcustomerlookupaddonWebConstants.COOKIE_DATA_MAPPING_ERROR, e);
            } catch (final Exception e) {
                LOG.error(SapcustomerlookupaddonWebConstants.ANONYMOUS_COOKIE_CREATE_ERROR, e);
            }
        }

        customerConsentDataStrategy.populateCustomerConsentDataInSession();

        return REDIRECT_PREFIX + "/";
    }
}
