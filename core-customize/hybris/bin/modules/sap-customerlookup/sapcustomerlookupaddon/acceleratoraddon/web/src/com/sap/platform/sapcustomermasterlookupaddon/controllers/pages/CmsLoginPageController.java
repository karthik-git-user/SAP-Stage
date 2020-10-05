/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomermasterlookupaddon.controllers.pages;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.platform.sapcustomerlookupservice.facade.CustomerMasterConsentFacade;
import com.sap.platform.sapcustomermasterlookupaddon.constants.SapcustomerlookupaddonWebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractLoginPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.ConsentForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.GuestForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.LoginForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.consent.data.AnonymousConsentData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static de.hybris.platform.commercefacades.constants.CommerceFacadesConstants.CONSENT_GIVEN;


/**
 * Login Controller. Handles login and register for the account flow.
 */
@Controller
@RequestMapping(value = "/login")
public class CmsLoginPageController extends AbstractLoginPageController {


    private static final Logger LOGGER = Logger.getLogger(CmsLoginPageController.class);


    @Resource(name = "consentFacade")
    protected CustomerMasterConsentFacade cmsConsentFacade;

    @Resource(name = "baseStoreService")
    protected BaseStoreService baseStoreService;

    @Resource(name = "httpSessionRequestCache")
    private HttpSessionRequestCache httpSessionRequestCache;



    @Override
    protected String getView() {
        return "pages/account/accountLoginPage";
    }

    @Override
    protected String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response) {
        if (httpSessionRequestCache.getRequest(request, response) != null) {
            return httpSessionRequestCache.getRequest(request, response).getRedirectUrl();
        }
        return "/";
    }

    @Override
    protected AbstractPageModel getCmsPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId("login");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String doLogin(@RequestHeader(value = "referer", required = false) final String referer, @RequestParam(value = "error", defaultValue = "false") boolean loginError, final Model model,
                          final HttpServletRequest request, final HttpServletResponse response, final HttpSession session)
            throws CMSItemNotFoundException {
        if (!loginError) {
            storeReferer(referer, request, response);
        } else {
            //Set the Error message to view in the UI and continue displaying the Login page.
            if (baseStoreService.getCurrentBaseStore().isCmsEmailVerificationEnabled()) {
                GlobalMessages.addErrorMessage(model, "account.confirmation.masterlookup.emailunverified.or.passwordincorrect");
            } else {
                GlobalMessages.addErrorMessage(model, "login.error.account.not.found.title");
            }
            loginError = false;
        }

        //Setting loginError to true to prevent duplicate error messages
        return getDefaultLoginPage(loginError, session, model);
    }

    protected void storeReferer(final String referer, final HttpServletRequest request, final HttpServletResponse response) {
        if (StringUtils.isNotBlank(referer) && !StringUtils.endsWith(referer, "/login")
                && StringUtils.contains(referer, request.getServerName())) {
            httpSessionRequestCache.saveRequest(request, response);
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String doRegister(@RequestHeader(value = "referer", required = false) final String referer, final RegisterForm form, final BindingResult bindingResult, final Model model,
                             final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel)
            throws CMSItemNotFoundException {
        getRegistrationValidator().validate(form, bindingResult);
        
        if (bindingResult.hasErrors()) {
            form.setTermsCheck(false);
            model.addAttribute(form);
            model.addAttribute(new LoginForm());
            model.addAttribute(new GuestForm());
            GlobalMessages.addErrorMessage(model, SapcustomerlookupaddonWebConstants.FORM_GLOBAL_ERROR);
            return handleRegistrationError(model);
        }

        final RegisterData data = new RegisterData();
        data.setFirstName(form.getFirstName());
        data.setLastName(form.getLastName());
        data.setLogin(form.getEmail());
        data.setPassword(form.getPwd());
        data.setTitleCode(form.getTitleCode());
        
        try {
            getCustomerFacade().register(data);
            getAutoLoginStrategy().login(form.getEmail().toLowerCase(), form.getPwd(), request, response);
            addEmailVerificationFlashMessage(redirectModel);

        } catch (final DuplicateUidException e) {
            LOGGER.debug("registration failed.");
            form.setTermsCheck(false);
            model.addAttribute(form);
            model.addAttribute(new LoginForm());
            model.addAttribute(new GuestForm());
            bindingResult.rejectValue("email", "registration.error.account.exists.title");
            GlobalMessages.addErrorMessage(model, SapcustomerlookupaddonWebConstants.FORM_GLOBAL_ERROR);
            return handleRegistrationError(model);
        }

        return processRegisterUserRequest(form.getConsentForm(), form.getEmail(), request, response, redirectModel);
    }

    @RequestMapping(value = "/register/termsandconditions", method = RequestMethod.GET)
    public String getTermsAndConditions(final Model model) throws CMSItemNotFoundException {
        final ContentPageModel pageForRequest = getCmsPageService().getPageForLabel("/termsAndConditions");
        storeCmsPageInModel(model, pageForRequest);
        setUpMetaDataForContentPage(model, pageForRequest);
        return "fragments/checkout/termsAndConditionsPopup";
    }

	private void addEmailVerificationFlashMessage(final RedirectAttributes redirectModel) {
		if (baseStoreService.getCurrentBaseStore().isCmsEmailVerificationEnabled()) {
		    GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
		            "registration.confirmation.message.emailverification.title");

		} else {
		    GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
		            "registration.confirmation.message.title");
		}
	}
    /**
     * This method takes data from the registration form and create a new customer account and attempts to log in using
     * the credentials of this new user.
     *
     * @return true if there are no binding errors or the account does not already exists.
     * @throws CMSItemNotFoundException
     */	
    protected String processRegisterUserRequest(final ConsentForm consentForm, final String email, final HttpServletRequest request, final HttpServletResponse response,
                                                final RedirectAttributes redirectModel) throws CMSItemNotFoundException // NOSONAR
    {
        // Consent form data
        try {
            if (consentForm != null && consentForm.getConsentGiven()) {
                cmsConsentFacade.giveConsentForEmailId(email.toLowerCase(), consentForm.getConsentTemplateId(),
                        consentForm.getConsentTemplateVersion());
            }
        } catch (final Exception e) {
            LOGGER.error("Error occurred while creating consents during registration", e);
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                    SapcustomerlookupaddonWebConstants.CONSENT_FORM_GLOBAL_ERROR);
        }

        // save anonymous-consent cookies as ConsentData
        final Cookie cookie = WebUtils.getCookie(request, WebConstants.ANONYMOUS_CONSENT_COOKIE);
        if (cookie != null) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                final List<AnonymousConsentData> anonymousConsentDataList = Arrays.asList(mapper.readValue(
                        URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.displayName()), AnonymousConsentData[].class));
                anonymousConsentDataList.stream().filter(consentData -> CONSENT_GIVEN.equals(consentData.getConsentState()))
                        .forEach(consentData -> cmsConsentFacade.giveConsentForEmailId(email.toLowerCase(), consentData.getTemplateCode(),
                                Integer.valueOf(consentData.getTemplateVersion())));
            } catch (final UnsupportedEncodingException e) {
                LOGGER.error(SapcustomerlookupaddonWebConstants.COOKIE_DATA_DECODE_ERROR, e);
            } catch (final IOException e) {
                LOGGER.error(SapcustomerlookupaddonWebConstants.COOKIE_DATA_MAPPING_ERROR, e);
            } catch (final Exception e) {
                LOGGER.error(SapcustomerlookupaddonWebConstants.ANONYMOUS_COOKIE_CREATE_ERROR, e);
            }
        }

        customerConsentDataStrategy.populateCustomerConsentDataInSession();

        return REDIRECT_PREFIX + getSuccessRedirect(request, response);
    }

}
