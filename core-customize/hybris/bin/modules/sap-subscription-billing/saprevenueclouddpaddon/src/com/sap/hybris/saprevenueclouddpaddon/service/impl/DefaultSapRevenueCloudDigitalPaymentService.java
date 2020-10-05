/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenueclouddpaddon.service.impl;

import com.hybris.charon.Charon;
import com.sap.hybris.saprevenueclouddpaddon.constants.SaprevenueclouddpaddonConstants;
import com.sap.hybris.saprevenueclouddpaddon.data.SapRcDigitalPaymentPollResultData;
import com.sap.hybris.saprevenueclouddpaddon.service.SapRevenueCloudDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.client.SapDigitalPaymentClient;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentPollRegisteredCardResult;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentTransactionResult;
import de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant;
import de.hybris.platform.cissapdigitalpayment.model.SAPDigitalPaymentClientModel;
import de.hybris.platform.cissapdigitalpayment.model.SAPDigitalPaymentConfigurationModel;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.service.impl.DefaultCisSapDigitalPaymentService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

/**
 * Default Implementation for {@link SapRevenueCloudDigitalPaymentService}
 */
public class DefaultSapRevenueCloudDigitalPaymentService implements SapRevenueCloudDigitalPaymentService {

    private static final Logger LOG = Logger.getLogger(DefaultCisSapDigitalPaymentService.class);


    private Map<String, String> sapDigiPayPollCardStatusMap;

    private ModelService modelService;
    private BaseStoreService baseStoreService;
    private SapDigitalPaymentService sapDigitalPaymentService;
    private UserService userService;
    private CartService cartService;

    private Converter<AddressModel, AddressData> addressConverter;
    private Converter<CCPaymentInfoData, CreditCardPaymentInfoModel> ccPaymentInfoReverseConverter;
    private Converter<CisSapDigitalPaymentPollRegisteredCardResult, CCPaymentInfoData> cisSapDigitalPaymentPaymentInfoConverter;
    private Converter<CisSapDigitalPaymentPollRegisteredCardResult, SapRcDigitalPaymentPollResultData> sapDigitalPaymentPollResultConverter;

    @Override
    public CreditCardPaymentInfoModel checkPaymentCardRegistration(String sessionId) {

        final SAPDigitalPaymentConfigurationModel sapDigiPayConfig = getBaseStoreService().getCurrentBaseStore().getSapDigitalPaymentConfiguration();
        CisSapDigitalPaymentPollRegisteredCardResult dpPollResult = pollRegisteredCard(sessionId, sapDigiPayConfig).toBlocking().last();

        validateParameterNotNullStandardMessage("dpPollResult", dpPollResult);
        CisSapDigitalPaymentTransactionResult transactionResult = dpPollResult.getCisSapDigitalPaymentTransactionResult();

        validateParameterNotNullStandardMessage("transactionResult", transactionResult);

        //If successful then save data credit card details
        CreditCardPaymentInfoModel creditCardPaymentInfoModel = null;
        String transactionStatus = sapDigiPayPollCardStatusMap.getOrDefault(transactionResult.getDigitalPaytTransResult(), SaprevenueclouddpaddonConstants.STAT_UNKNOWN);
        if(StringUtils.equalsAnyIgnoreCase(transactionStatus, SaprevenueclouddpaddonConstants.STAT_SUCCESS)){
            creditCardPaymentInfoModel = saveCCPaymentInfo(dpPollResult);
        }

        return creditCardPaymentInfoModel;
    }

    /**
     * Saves credit card details locally
     * @param cisSapDigitalPaymentPollRegisteredCardResult result from Digital Payments
     * @return If successful then instance of credit card model otherwise null
     */
    protected CreditCardPaymentInfoModel saveCCPaymentInfo(CisSapDigitalPaymentPollRegisteredCardResult cisSapDigitalPaymentPollRegisteredCardResult){
        validateParameterNotNullStandardMessage("cisSapDigitalPaymentPollRegisteredCardResult", cisSapDigitalPaymentPollRegisteredCardResult);

        final CCPaymentInfoData ccPaymentInfoData = getCisSapDigitalPaymentPaymentInfoConverter()
                .convert(cisSapDigitalPaymentPollRegisteredCardResult);
        ccPaymentInfoData.setSaved(true);

        //Set billing address as default billing address
        UserModel userModel = getUserService().getCurrentUser();
        AddressModel defaultPaymentAddressModel = userModel.getDefaultPaymentAddress();
        validateParameterNotNullStandardMessage("defaultPaymentAddressModel", defaultPaymentAddressModel);
        AddressData defaultPaymentAddressData = getAddressConverter().convert(defaultPaymentAddressModel);
        ccPaymentInfoData.setBillingAddress(defaultPaymentAddressData);

        //These parameters are required to populate extra data like current code, user, etc
        final Map<String, Object> params = new HashMap<>();
        params.put(CisSapDigitalPaymentConstant.USER, userModel);
        params.put(CisSapDigitalPaymentConstant.BASE_STORE, getBaseStoreService().getCurrentBaseStore());
        params.put(CisSapDigitalPaymentConstant.CART, getCartService().getSessionCart());
        final CreditCardPaymentInfoModel cardPaymentInfo = getSapDigitalPaymentService().createPaymentSubscription(ccPaymentInfoData, params);
        getModelService().save(cardPaymentInfo);

        return cardPaymentInfo;
    }

    /**
     * Polling the card details entered at SAP Digital payments screen.
     *
     * @param sessionId
     *           - Session ID received during the card registration
     * @return CisSapDigitalPaymentPollRegisteredCardResult - Poll card response wrapped in Observable<>
     * @param sapDigiPayConfig
     *           - SAP Digital payment configuration
     *
     */
    protected Observable<CisSapDigitalPaymentPollRegisteredCardResult> pollRegisteredCard(final String sessionId,
                                                                                       final SAPDigitalPaymentConfigurationModel sapDigiPayConfig)
    {
        SapDigitalPaymentClient sapDigitalPaymentClient = getCisSapDigitalPaymentClient(sapDigiPayConfig);
        return sapDigitalPaymentClient.pollRegisteredCard(sessionId).map(
                registeredCard -> {
                    LOG.info("Successfully polled the registered card");
                    return registeredCard;
                }
        ).doOnError(DefaultSapRevenueCloudDigitalPaymentService::logError);
    }


    /**
     * Creates the Map<String,String> with all the properties required to create a SapDigitalPaymentClient.
     *
     * @param sapDigitalPaymentConfig
     *           - SAP Digital payment configuration model
     *
     * @return Map<String, String>
     */
    protected Map<String, String> createDigitalPaymentConfigurationMap(
            final SAPDigitalPaymentConfigurationModel sapDigitalPaymentConfig)
    {
        final Map<String, String> sapDigitalPaymentConfigMap = new HashMap<>();
        try
        {
            validateParameterNotNull(sapDigitalPaymentConfig, "Sap Digital payment configuration cannot be null");

            final SAPDigitalPaymentClientModel sapDpClientModel = sapDigitalPaymentConfig.getSapDigitalpaymentClient();
            if (null != sapDpClientModel)
            {
                sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_CLIENT_ID_KEY,
                        sapDpClientModel.getClientId());
                sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_CLIENT_SECRET_KEY,
                        sapDpClientModel.getClientSecret());
                sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_GRANT_TYPE_KEY,
                        StringUtils.join(sapDpClientModel.getAuthorizedGrantTypes(), ","));
                sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_SCOPE,
                        StringUtils.join(sapDpClientModel.getScope(), ","));

                sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_URL_KEY,
                        sapDpClientModel.getTokenUrl());
            }



            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_URL_KEY,
                    sapDigitalPaymentConfig.getBaseUrl());

            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_COMPANY_CODE_KEY,
                    sapDigitalPaymentConfig.getCompanyCode());
            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_CUSTOMER_COUNTRY_KEY,
                    sapDigitalPaymentConfig.getCustomerCountry());
            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_PAYMENT_METHOD_KEY,
                    sapDigitalPaymentConfig.getPaymentMethod());
            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_CUSTOM_PARAM_KEY,
                    sapDigitalPaymentConfig.getCustomParam());

            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_RETRIES_KEY,
                    String.valueOf(sapDigitalPaymentConfig.getMaxRetry()));
            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_RETRIES_INTERVAL_KEY,
                    String.valueOf(sapDigitalPaymentConfig.getRetryInterval()));
            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_TIMEOUT_KEY,
                    String.valueOf(sapDigitalPaymentConfig.getTimeOut()));
        }
        catch (final RuntimeException e)
        {
            if (LOG.isDebugEnabled())
            {
                LOG.debug(e);
            }
            LOG.error("Error while reading the SAP Digital payment configurations. Configuration details might be missing"
                    + e.getMessage());
        }
        return sapDigitalPaymentConfigMap;

    }



    /**
     * Create the Sap Digital payment charon client from the SAP digital payment configuration
     *
     * @param sapDigitalPaymentConfig
     *           - sap digital payment configuration
     * @return SapDigitalPaymentClient - SAP Digital payment client
     */
    protected SapDigitalPaymentClient getCisSapDigitalPaymentClient(final SAPDigitalPaymentConfigurationModel sapDigitalPaymentConfig)
    {
        return Charon.from(SapDigitalPaymentClient.class).config(createDigitalPaymentConfigurationMap(sapDigitalPaymentConfig))
                .build();
    }

    /**
     * @return the cisSapDigitalPaymentPaymentInfoConverter
     */
    public Converter<CisSapDigitalPaymentPollRegisteredCardResult, CCPaymentInfoData> getCisSapDigitalPaymentPaymentInfoConverter()
    {
        return cisSapDigitalPaymentPaymentInfoConverter;
    }

    /**
     * @param cisSapDigitalPaymentPaymentInfoConverter
     *           the cisSapDigitalPaymentPaymentInfoConverter to set
     */
    public void setCisSapDigitalPaymentPaymentInfoConverter(
            final Converter<CisSapDigitalPaymentPollRegisteredCardResult, CCPaymentInfoData> cisSapDigitalPaymentPaymentInfoConverter)
    {
        this.cisSapDigitalPaymentPaymentInfoConverter = cisSapDigitalPaymentPaymentInfoConverter;
    }

    /**
     * @return the sapDigitalPaymentService
     */
    public de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService getSapDigitalPaymentService()
    {
        return sapDigitalPaymentService;
    }

    /**
     * @param sapDigitalPaymentService
     *           the sapDigitalPaymentService to set
     */
    public void setSapDigitalPaymentService(final de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService sapDigitalPaymentService)
    {
        this.sapDigitalPaymentService = sapDigitalPaymentService;
    }

    /**
     * @return the modelService
     */
    public ModelService getModelService() {
        return this.modelService;
    }

    /**
     * @return ccPaymentInfoReverseConverter
     */
    public Converter<CCPaymentInfoData, CreditCardPaymentInfoModel> getCcPaymentInfoReverseConverter() {
        return ccPaymentInfoReverseConverter;
    }

    /**
     * @param modelService model service
     */
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * @param ccPaymentInfoReverseConverter payment info converter
     */
    public void setCcPaymentInfoReverseConverter(Converter<CCPaymentInfoData, CreditCardPaymentInfoModel> ccPaymentInfoReverseConverter) {
        this.ccPaymentInfoReverseConverter = ccPaymentInfoReverseConverter;
    }

    /**
     * @return baseStoreService
     */
    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    /**
     * @param baseStoreService base store service
     */
    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    /**
     * @return sapDigiPayPollCardStatusMap
     */
    public Map<String, String> getSapDigiPayPollCardStatusMap() {
        return sapDigiPayPollCardStatusMap;
    }

    /**
     * @param sapDigiPayPollCardStatusMap Digital payments poll card status mapping
     */
    public void setSapDigiPayPollCardStatusMap(Map<String, String> sapDigiPayPollCardStatusMap) {
        this.sapDigiPayPollCardStatusMap = sapDigiPayPollCardStatusMap;
    }

    /**
     * @return userService
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * @param userService user service
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * @return cartService
     */
    public CartService getCartService() {
        return cartService;
    }

    /**
     * @param cartService cart service
     */
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * @return addressConverter
     */
    public Converter<AddressModel, AddressData> getAddressConverter() {
        return addressConverter;
    }

    /**
     * @param addressConverter addressConverter
     */
    public void setAddressConverter(Converter<AddressModel, AddressData> addressConverter) {
        this.addressConverter = addressConverter;
    }

    /**
     * @return sapDigitalPaymentPollResultConverter
     */
    public Converter<CisSapDigitalPaymentPollRegisteredCardResult, SapRcDigitalPaymentPollResultData> getSapDigitalPaymentPollResultConverter() {
        return sapDigitalPaymentPollResultConverter;
    }

    /**
     * @param sapDigitalPaymentPollResultConverter digital payment poll result converter
     */
    public void setSapDigitalPaymentPollResultConverter(Converter<CisSapDigitalPaymentPollRegisteredCardResult, SapRcDigitalPaymentPollResultData> sapDigitalPaymentPollResultConverter) {
        this.sapDigitalPaymentPollResultConverter = sapDigitalPaymentPollResultConverter;
    }



    /**
     * Logs the error received
     */
    private static void logError(final Throwable error)
    {
        LOG.error("Error while fetching the response" + error);
    }
}
