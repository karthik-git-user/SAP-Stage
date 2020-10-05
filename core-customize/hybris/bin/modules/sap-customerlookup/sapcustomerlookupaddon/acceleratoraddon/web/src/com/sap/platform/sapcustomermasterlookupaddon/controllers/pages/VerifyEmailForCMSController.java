/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomermasterlookupaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractLoginPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sap.platform.sapcustomerlookupservice.facade.VerifyAccountFromEmailLinkFacade;
import com.sap.platform.sapcustomermasterlookupaddon.constants.SapcustomerlookupaddonWebConstants;


@Controller
@RequestMapping("/verify")
public class VerifyEmailForCMSController extends AbstractLoginPageController {


    @Resource(name = "verifyEmailFacade")
    private VerifyAccountFromEmailLinkFacade verifyEmailFacade;


    /**
     * @return the verifyEmailFacade
     */
    public VerifyAccountFromEmailLinkFacade getVerifyEmailFacade() {
        return verifyEmailFacade;
    }


    /**
     * @param verifyEmailFacade the verifyEmailFacade to set
     */
    public void setVerifyEmailFacade(final VerifyAccountFromEmailLinkFacade verifyEmailFacade) {
        this.verifyEmailFacade = verifyEmailFacade;
    }


    @RequestMapping(value = "/email", method = RequestMethod.GET)
    public String verifyEmail(@RequestParam(required = true) final String token, final Model model, final HttpSession session, @RequestParam(value = "error", defaultValue = "false") final boolean loginError, final RedirectAttributes redirectModel) throws CMSItemNotFoundException {

        if (!StringUtils.isBlank(token)) {
            try {
                if (getVerifyEmailFacade().verifyEmail(token)) {
                    GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER,
                            "account.confirmation.masterlookup.success");
                } else {
                    GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                            "account.confirmation.masterlookup.token.validity.failed");
                }
            } catch (final TokenInvalidatedException e) {
                GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                        "account.confirmation.masterlookup.failed");
            }
        }

        final String loginRedirectURL = new StringBuilder(REDIRECT_PREFIX).append("/login").toString();

        return loginRedirectURL;
    }

    @RequestMapping(value = "/confirmation", method = RequestMethod.GET)
    public String verifyUserLogin(final Model model, final HttpSession session,
                                  @RequestParam(value = "error", defaultValue = "false") final boolean loginError) throws CMSItemNotFoundException {

        GlobalMessages.addInfoMessage(model, "account.confirmation.masterlookup.token.validity.failed");
        return getDefaultLoginPage(loginError, session, model);
    }


    @Override
    protected AbstractPageModel getCmsPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId("login");
    }


    @Override
    protected String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response) {
        // XXX Auto-generated method stub
        return null;
    }


    @Override
    protected String getView() {
        return SapcustomerlookupaddonWebConstants.PAGES_ACCOUNT_ACCOUNT_LOGIN_PAGE;
    }


}
