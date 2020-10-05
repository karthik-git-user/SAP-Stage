/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbquotefacades.populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sapcpqsbintegration.data.CpqPricingParameterData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.data.*;
import de.hybris.platform.subscriptionfacades.data.OneTimeChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.PerUnitUsageChargeData;
import de.hybris.platform.subscriptionfacades.data.RecurringChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPricePlanData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeData;
import de.hybris.platform.subscriptionfacades.data.VolumeUsageChargeData;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.PerUnitUsageChargeModel;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeModel;
import de.hybris.platform.subscriptionservices.model.VolumeUsageChargeModel;

import java.util.*;
import java.util.stream.Collectors;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapcpqsbquotefacades.service.SapSubscriptionBillingEffectivePriceService;
import com.sap.hybris.sapcpqsbintegration.model.CpqPricingParameterModel;
import com.sap.hybris.sapcpqsbintegration.model.CpqSubscriptionDetailModel;
import com.sap.hybris.sapcpqsbintegration.service.SapCpqSbFetchQuoteDiscountsService;

public class DefaultSapCpqSubscriptionPricePlanPopulator < SOURCE extends AbstractOrderEntryModel, TARGET extends OrderEntryData >
		implements Populator < SOURCE, TARGET > {

	private Converter < OneTimeChargeEntryModel,
			OneTimeChargeEntryData > oneTimeChargeEntryConverter;
	private Converter < RecurringChargeEntryModel,
			RecurringChargeEntryData > recurringChargeEntryConverter;
	private Converter < PerUnitUsageChargeModel,
			PerUnitUsageChargeData > perUnitUsageChargeConverter;
	private Converter < VolumeUsageChargeModel,
			VolumeUsageChargeData > volumeUsageChargeConverter;
	private Converter < UsageChargeModel,
			UsageChargeData > usageChargeConverter;
	private Converter < CpqPricingParameterModel,
			CpqPricingParameterData > cpqPricingPrameterConverter;
	private SapSubscriptionBillingEffectivePriceService sapSubscriptionBillingEffectivePriceService;
	private SapCpqSbFetchQuoteDiscountsService sapCpqSbFetchQuoteDiscounts;

	private static final String NON_ALPHANUMERIC = "[^a-zA-Z0-9]";
	private static final String DATE_PATTERN = "yyyy-MM-dd";

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException {
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		final ProductModel product = source.getProduct();
		final SubscriptionPricePlanData discountedSubscriptionPricePlanData = new SubscriptionPricePlanData();
		Date effectiveDate = new Date();


		// Discounted Value populator
		populateDiscounts(source,discountedSubscriptionPricePlanData);


		//Populating Pricing Parameters
		if (!CollectionUtils.isEmpty(source.getCpqSubscriptionDetails())) {
			for (final CpqSubscriptionDetailModel cpqSubscriptionDetail : source.getCpqSubscriptionDetails()) {
				if (cpqSubscriptionDetail.getEffectiveDate() != null) {
					target.setEffectiveDate(dateToString(cpqSubscriptionDetail.getEffectiveDate()));
					effectiveDate = cpqSubscriptionDetail.getEffectiveDate();
				}
				if (cpqSubscriptionDetail.getContractEndDate() != null) {
					target.setContractEndDate(dateToString(cpqSubscriptionDetail.getContractEndDate()));
				}
				if (cpqSubscriptionDetail.getMinimumContractEndDate() != null) {
					target.setMinimumContractEndDate(dateToString(cpqSubscriptionDetail.getMinimumContractEndDate()));
				}
				if (cpqSubscriptionDetail.getContractLength() != null) {
					target.setContractLength(cpqSubscriptionDetail.getContractLength());
				}
				if (cpqSubscriptionDetail.getMinimumContractLength() != null) {
					target.setMinimumContractLength(cpqSubscriptionDetail.getMinimumContractLength());
				}
				if (cpqSubscriptionDetail.getSubscriptionPricePlanId() != null) {
					target.setSubscriptionPricePlanId(cpqSubscriptionDetail.getSubscriptionPricePlanId());
				}

				if (!CollectionUtils.isEmpty(cpqSubscriptionDetail.getPricingParameters())) {
					final List<CpqPricingParameterData> cpqPricingParameters = Converters
							.convertAll(cpqSubscriptionDetail.getPricingParameters(), getCpqPricingPrameterConverter());
					for (final CpqPricingParameterData cpqPricingParameter : cpqPricingParameters) {
						target.setCpqPricingParameters(cpqPricingParameter);
					}
				}


			}
		}

		//Fetching Discounted values for Ordered status
		if(source instanceof QuoteEntryModel) {
			final AbstractOrderModel order = source.getOrder();
			if (((QuoteModel) order).getState().equals(QuoteState.BUYER_ORDERED) && order.getCode() != null && product.getSubscriptionCode() != null) {
				String quoteId = order.getCode();
				QuoteModel vendorQuoteDiscounts = sapCpqSbFetchQuoteDiscounts.getCurrentQuoteVendorDiscounts(quoteId);
				if (!CollectionUtils.isEmpty(vendorQuoteDiscounts.getEntries())) {
					vendorQuoteDiscounts.getEntries().forEach(entry -> {
						if(source.getEntryNumber() == entry.getEntryNumber()) {
							populateDiscounts(entry, discountedSubscriptionPricePlanData);
						}
					});
				}
			}
		}

		target.setDiscountSubscriptionPricePlan(discountedSubscriptionPricePlanData);

		//Initial Price population with effective Date
		if (effectiveDate != null && product.getSubscriptionCode() != null) {

			final SubscriptionPricePlanModel subscriptionEffectivePricePlan = sapSubscriptionBillingEffectivePriceService
					.getSubscriptionEffectivePricePlan(product, effectiveDate);

			final SubscriptionPricePlanData subscriptionPricePlanData = new SubscriptionPricePlanData();

			if (subscriptionEffectivePricePlan != null) {

				// Populating OneTime Charges
				if (CollectionUtils.isNotEmpty(subscriptionEffectivePricePlan.getOneTimeChargeEntries())) {

					//Convert One time charge model to data
					List < OneTimeChargeEntryModel > oneTimeChargeEntryModelList = new ArrayList < > (subscriptionEffectivePricePlan.getOneTimeChargeEntries());
					final List < OneTimeChargeEntryData > oneTimeChargeEntries = Converters.convertAll(oneTimeChargeEntryModelList, oneTimeChargeEntryConverter);

					//Normalize
					List < OneTimeChargeEntryData > discountedChargeEntries = discountedSubscriptionPricePlanData.getOneTimeChargeEntries();
					if (discountedChargeEntries == null) {
						discountedChargeEntries = new LinkedList < > ();
					}
					normalizeOneTimeChargeEntries(oneTimeChargeEntries, discountedChargeEntries);

					// Set in target
					subscriptionPricePlanData.setOneTimeChargeEntries(oneTimeChargeEntries);

				}

				// Populating recurring Charges
				if (CollectionUtils.isNotEmpty(subscriptionEffectivePricePlan.getRecurringChargeEntries())) {

					//Convert recurring charges model to data
					List < RecurringChargeEntryModel > recurringChargeEntryModelList = new ArrayList < > (subscriptionEffectivePricePlan.getRecurringChargeEntries());
					final List < RecurringChargeEntryData > recurringChargeEntries = Converters.convertAll(recurringChargeEntryModelList, recurringChargeEntryConverter);

					//Normalize
					List < RecurringChargeEntryData > discountedChargeEntries = discountedSubscriptionPricePlanData.getRecurringChargeEntries();
					if (discountedChargeEntries == null) {
						discountedChargeEntries = new LinkedList < > ();
					}
					normalizeRecurringChargeEntries(recurringChargeEntries, discountedChargeEntries);

					//Set in target
					subscriptionPricePlanData.setRecurringChargeEntries(recurringChargeEntries);
				}


				//Populating Usage Charges
				if (CollectionUtils.isNotEmpty(subscriptionEffectivePricePlan.getUsageCharges())) {

					//Convert usage charge model to data
					final List < UsageChargeData > initialUsageCharges = subscriptionEffectivePricePlan.getUsageCharges().stream().map(
							charge -> {
								if (charge instanceof VolumeUsageChargeModel) {
									return volumeUsageChargeConverter.convert((VolumeUsageChargeModel) charge);
								} else if (charge instanceof PerUnitUsageChargeModel) {
									return perUnitUsageChargeConverter.convert((PerUnitUsageChargeModel) charge);
								}
								return null;
							}
					).collect(Collectors.toList());

					//Normalize
					List < UsageChargeData > discountedUsageCharges = discountedSubscriptionPricePlanData.getUsageCharges();
					if (discountedUsageCharges == null) {
						discountedUsageCharges = new LinkedList < > ();
					}
					normalizeUsageCharges(initialUsageCharges, discountedUsageCharges);

					//Set to target
					subscriptionPricePlanData.setUsageCharges(initialUsageCharges);

				}
			}
			target.setInitialSubscriptionPricePlan(subscriptionPricePlanData);
		}
	}

	//<editor-fold desc="Private methods">
	private void normalizeUsageCharges(List < UsageChargeData > initialUsageChargeList, List < UsageChargeData > discountedUsageChargeList) {

		//Map initial and discounted usage charge based on billing id
		Map < String, Pair < UsageChargeData, UsageChargeData >> map = new HashMap < > ();
		for (UsageChargeData initialUsageChargeData: initialUsageChargeList) {
			String subscriptionBillingId = getSubscriptionBillingId(initialUsageChargeData);
			Pair < UsageChargeData, UsageChargeData > pair = MutablePair.of(initialUsageChargeData, null);
			map.put(subscriptionBillingId, pair);
		}
		for (UsageChargeData discountedUsageChargeData: discountedUsageChargeList) {
			String subscriptionBillingId = getSubscriptionBillingId(discountedUsageChargeData);
			Pair < UsageChargeData, UsageChargeData > pair = map.get(subscriptionBillingId);
			pair.setValue(discountedUsageChargeData);
		}

		//Normalize all usage charge data
		map.forEach((subscriptionBillingId, pair) -> {
			//If no discount then set initial value
			if (pair.getRight() == null) {
				pair.setValue(pair.getLeft()); //NOSONAR
			}

			if (pair.getLeft() instanceof VolumeUsageChargeData) {
				VolumeUsageChargeData initialUsageChargeData = (VolumeUsageChargeData) pair.getLeft();
				VolumeUsageChargeData discountedUsageChargeData = (VolumeUsageChargeData) pair.getRight();
				normalizeVolumeUsage(initialUsageChargeData, discountedUsageChargeData);
			} else {
				PerUnitUsageChargeData initialUsageChargeData = (PerUnitUsageChargeData) pair.getLeft();
				PerUnitUsageChargeData discountedUsageChargeData = (PerUnitUsageChargeData) pair.getRight();
				normalizePerUnitUsage(initialUsageChargeData, discountedUsageChargeData);
			}
		});

		//Sort initial usage charge data on type then subscription billing id
		initialUsageChargeList.sort((o1, o2) -> {
			String o1ClassName = o1.getClass().getName();
			String o2ClassName = o2.getClass().getName();

			if (o1ClassName.equals(o2ClassName)) {
				String sbO1 = getSubscriptionBillingId(o1);
				String sbO2 = getSubscriptionBillingId(o2);
				return StringUtils.compare(sbO1, sbO2);
			} else {
				return StringUtils.compare(o1ClassName, o2ClassName);
			}
		});

		//Sort discounted usage charge data based on intial charge data
		List < UsageChargeData > normalizedDiscountedUsageChargeDataList = new ArrayList < > (initialUsageChargeList.size());
		for (UsageChargeData usageChargeData: initialUsageChargeList) {
			String subscriptionBillingId = getSubscriptionBillingId(usageChargeData);
			Pair < UsageChargeData, UsageChargeData > pair = map.get(subscriptionBillingId);
			normalizedDiscountedUsageChargeDataList.add(pair.getRight());
		}
		discountedUsageChargeList.clear();
		discountedUsageChargeList.addAll(normalizedDiscountedUsageChargeDataList);


	}

	private String getSubscriptionBillingId(UsageChargeData usageChargeData) {
		String subscriptionBillingId = null;
		if (usageChargeData instanceof VolumeUsageChargeData) {
			subscriptionBillingId = ((VolumeUsageChargeData) usageChargeData).getSubscriptionBillingId();
		} else {
			subscriptionBillingId = ((PerUnitUsageChargeData) usageChargeData).getSubscriptionBillingId();
		}
		return subscriptionBillingId;
	}

	//Normalize Volume Charges
	private void normalizeVolumeUsage(VolumeUsageChargeData initialData, VolumeUsageChargeData discountedData) {
		List < UsageChargeEntryData > initialUsageChargeDataEntryList = initialData.getUsageChargeEntries();
		List < UsageChargeEntryData > discountedUsageChargeEntryList = new ArrayList<>(discountedData.getUsageChargeEntries());
		Map < String, Pair < UsageChargeEntryData, UsageChargeEntryData >> map = new HashMap < > ();

		//Creating mappings based on tierStart
		for (UsageChargeEntryData initialUsageChargeEntryData: initialUsageChargeDataEntryList) {
			String tierStart = getTierStart(initialUsageChargeEntryData);
			Pair < UsageChargeEntryData, UsageChargeEntryData > pair = MutablePair.of(initialUsageChargeEntryData, null);
			map.put(tierStart, pair);
		}
		for (UsageChargeEntryData discountedUsageChargeEntryData: discountedUsageChargeEntryList) {
			String tierStart = getTierStart(discountedUsageChargeEntryData);
			Pair < UsageChargeEntryData, UsageChargeEntryData > pair = map.get(tierStart);
			pair.setValue(discountedUsageChargeEntryData);
		}

		//Defaulting null discounts with initial charges
		map.forEach((tierStart, pair) -> {
			if (pair.getRight() == null) {
				pair.setValue(pair.getLeft()); //NOSONAR
			}
		});

		//Populating discount based on mappings above and order of initial data

		discountedUsageChargeEntryList.clear();
		for (UsageChargeEntryData initialUsageChargeEntryData: initialUsageChargeDataEntryList) {
			String tierStart = getTierStart(initialUsageChargeEntryData);
			Pair < UsageChargeEntryData, UsageChargeEntryData > pair = map.get(tierStart);
			discountedUsageChargeEntryList.add(pair.getRight());
		}
	}

	private String getTierStart(UsageChargeEntryData usageChargeEntryData) {
		if (usageChargeEntryData instanceof TierUsageChargeEntryData) {
			int tierStart = ((TierUsageChargeEntryData) usageChargeEntryData).getTierStart();
			return String.valueOf(tierStart);
		} else {
			return "overage_tier_start";
		}
	}

	//Normalize PerUnit Charges(Block, Tier, Percentage)
	private void normalizePerUnitUsage(PerUnitUsageChargeData initialData, PerUnitUsageChargeData discountedData) {
		List < UsageChargeEntryData > initialUsageChargeDataEntryList = initialData.getUsageChargeEntries();
		List < UsageChargeEntryData > discountedUsageChargeEntryList = new ArrayList<>( discountedData.getUsageChargeEntries() );
		Map < String, Pair < UsageChargeEntryData, UsageChargeEntryData >> map = new HashMap < > ();

		//Block Charges
		if (StringUtils.equals(initialData.getUsageChargeType().getCode(), "block_usage_charge")) {
			//Assumption: Maximum only one entry will com in discount and initial
			if (initialUsageChargeDataEntryList.size() != discountedUsageChargeEntryList.size()) {
				discountedUsageChargeEntryList.addAll(initialUsageChargeDataEntryList);
			}
		}
		//Tier Charges
		else if (StringUtils.equals(initialData.getUsageChargeType().getCode(), "each_respective_tier")) {
			//Creating mappings based on tierStart
			for (UsageChargeEntryData initialUsageChargeEntryData: initialUsageChargeDataEntryList) {
				String tierStart = getTierStart(initialUsageChargeEntryData);
				Pair < UsageChargeEntryData, UsageChargeEntryData > pair = MutablePair.of(initialUsageChargeEntryData, null);
				map.put(tierStart, pair);
			}
			for (UsageChargeEntryData discountedUsageChargeEntryData: discountedUsageChargeEntryList) {
				String tierStart = getTierStart(discountedUsageChargeEntryData);
				Pair < UsageChargeEntryData, UsageChargeEntryData > pair = map.get(tierStart);
				pair.setValue(discountedUsageChargeEntryData);
			}

			//Defaulting null discounts with initial charges
			map.forEach((tierStart, pair) -> {
				if (pair.getRight() == null) {
					pair.setValue(pair.getLeft());
				}
			});

			//Populating discount based on mappings above and order of initial data
			discountedUsageChargeEntryList.clear();
			for (UsageChargeEntryData initialUsageChargeEntryData: initialUsageChargeDataEntryList) {
				String tierStart = getTierStart(initialUsageChargeEntryData);
				Pair < UsageChargeEntryData, UsageChargeEntryData > pair = map.get(tierStart);
				discountedUsageChargeEntryList.add(pair.getRight());
			}
		}


		//Percentage Charges
		else if (StringUtils.equals(initialData.getUsageChargeType().getCode(), "percentage_usage_charge")) {
			//Assumption: Maximum only one entry can come in discount and initial
			if (initialUsageChargeDataEntryList.size() != discountedUsageChargeEntryList.size()) {
				discountedUsageChargeEntryList.addAll(initialUsageChargeDataEntryList);
			}
		}



	}

	private void normalizeOneTimeChargeEntries(List < OneTimeChargeEntryData > initialOneTimeChargeEntryList, List < OneTimeChargeEntryData > discountedOneTimeChargeEntryList) {

		initialOneTimeChargeEntryList.sort(
				(o1, o2) -> StringUtils.compare(o1.getSubscriptionBillingId(), o2.getSubscriptionBillingId())
		);

		discountedOneTimeChargeEntryList.sort(
				(o1, o2) -> StringUtils.compare(o1.getSubscriptionBillingId(), o2.getSubscriptionBillingId())
		);

		//Initialize empty discounted one time charges same as initial one time charges
		if (initialOneTimeChargeEntryList.size() != discountedOneTimeChargeEntryList.size()) {
			List < OneTimeChargeEntryData > normalizedDiscountedOneTimeChargeEntries = new ArrayList < > (initialOneTimeChargeEntryList.size());
			int j = 0;
			for (OneTimeChargeEntryData initialOneTimeChargeEntryData: initialOneTimeChargeEntryList) {
				String initialSubscriptionBillingId = initialOneTimeChargeEntryData.getSubscriptionBillingId();
				String discountSubscriptionBillingId = j < discountedOneTimeChargeEntryList.size() ? discountedOneTimeChargeEntryList.get(j).getSubscriptionBillingId() : EMPTY;

				OneTimeChargeEntryData data = initialOneTimeChargeEntryData;
				if (StringUtils.equals(initialSubscriptionBillingId, discountSubscriptionBillingId)) {
					data = discountedOneTimeChargeEntryList.get(j++);
				}

				normalizedDiscountedOneTimeChargeEntries.add(data);
			}

			discountedOneTimeChargeEntryList.clear();
			discountedOneTimeChargeEntryList.addAll(normalizedDiscountedOneTimeChargeEntries);
		}

	}


	private void normalizeRecurringChargeEntries(List < RecurringChargeEntryData > initialRecurringChargeEntryList, List < RecurringChargeEntryData > discountedRecurringChargeEntryList) {

		initialRecurringChargeEntryList.sort(
				(o1, o2) -> StringUtils.compare(o1.getSubscriptionBillingId(), o2.getSubscriptionBillingId())
		);

		discountedRecurringChargeEntryList.sort(
				(o1, o2) -> StringUtils.compare(o1.getSubscriptionBillingId(), o2.getSubscriptionBillingId())
		);

		//Initialize empty discounted recurring charges same as initial recurring charges
		if (initialRecurringChargeEntryList.size() != discountedRecurringChargeEntryList.size()) {
			List < RecurringChargeEntryData > normalizedDiscountedRecurringChargeEntries = new ArrayList < > (initialRecurringChargeEntryList.size());
			int j = 0;
			for (RecurringChargeEntryData initialRecurringChargeEntryData: initialRecurringChargeEntryList) {
				String initialSubscriptionBillingId = initialRecurringChargeEntryData.getSubscriptionBillingId();
				String discountSubscriptionBillingId = j < discountedRecurringChargeEntryList.size() ? discountedRecurringChargeEntryList.get(j).getSubscriptionBillingId() : EMPTY;

				RecurringChargeEntryData data = initialRecurringChargeEntryData;
				if (StringUtils.equals(initialSubscriptionBillingId, discountSubscriptionBillingId)) {
					data = discountedRecurringChargeEntryList.get(j++);
				}

				normalizedDiscountedRecurringChargeEntries.add(data);
			}

			discountedRecurringChargeEntryList.clear();
			discountedRecurringChargeEntryList.addAll(normalizedDiscountedRecurringChargeEntries);
		}


	}
	//</editor-fold>


    private SubscriptionPricePlanData populateDiscounts(final AbstractOrderEntryModel source, final SubscriptionPricePlanData discountedSubscriptionPricePlanData ){
		 if (!CollectionUtils.isEmpty(source.getCpqSubscriptionDetails())) {

			// Populating OneTime Charges
			for (final CpqSubscriptionDetailModel cpqSubscriptionDetail: source.getCpqSubscriptionDetails()) {


			if (!CollectionUtils.isEmpty(cpqSubscriptionDetail.getOneTimeChargeEntries())) {
				List < OneTimeChargeEntryModel > oneTimeChargeEntryModelList = new ArrayList < > (cpqSubscriptionDetail.getOneTimeChargeEntries());
				final List < OneTimeChargeEntryData > oneTimeChargeEntryDataList = Converters.convertAll(oneTimeChargeEntryModelList, getOneTimeChargeEntryConverter());
				discountedSubscriptionPricePlanData.setOneTimeChargeEntries(oneTimeChargeEntryDataList);

			}
			//Default empty list if there are no Discounted One Time Charges present
			else{
				discountedSubscriptionPricePlanData.setOneTimeChargeEntries(new LinkedList<>());
			}
		}

			// Populating Recurring Charges
			for (final CpqSubscriptionDetailModel cpqSubscriptionDetail: source.getCpqSubscriptionDetails()) {

			if (!CollectionUtils.isEmpty(cpqSubscriptionDetail.getRecurringChargeEntries())) {
				List < RecurringChargeEntryModel > recurringChargeEntryModelList = new ArrayList < > (cpqSubscriptionDetail.getRecurringChargeEntries());
				recurringChargeEntryModelList.sort(
						(o1, o2) -> StringUtils.compare(o1.getSubscriptionBillingId(), o2.getSubscriptionBillingId())
				);

				final List < RecurringChargeEntryData > recurringChargeEntries = Converters
						.convertAll(recurringChargeEntryModelList, getRecurringChargeEntryConverter());
				discountedSubscriptionPricePlanData.setRecurringChargeEntries(recurringChargeEntries);
			}
			//Default empty list if there are no Discounted Recurring Charges present
			else{
				discountedSubscriptionPricePlanData.setRecurringChargeEntries(new LinkedList<>());
			}
		}

			// Populating Usage Charges
			for (final CpqSubscriptionDetailModel cpqSubscriptionDetail: source.getCpqSubscriptionDetails()) {

			if (!CollectionUtils.isEmpty(cpqSubscriptionDetail.getUsageCharges())) {

				final List < UsageChargeData > usageCharges = new LinkedList<>();
				final List < UsageChargeData > volumeUsageCharges = new LinkedList<>();
				final List < UsageChargeData > perUnitUsageCharges = new LinkedList<>();

				for (final UsageChargeModel usageCharge: cpqSubscriptionDetail.getUsageCharges()) {

					if (usageCharge instanceof VolumeUsageChargeModel) {
						volumeUsageCharges.add(getVolumeUsageChargeConverter().convert((VolumeUsageChargeModel) usageCharge));
						volumeUsageCharges.sort(
								(o1, o2) -> StringUtils.compare(((VolumeUsageChargeData) o1).getSubscriptionBillingId(), ((VolumeUsageChargeData) o2).getSubscriptionBillingId())
						);
					} else if (usageCharge instanceof PerUnitUsageChargeModel) {
						perUnitUsageCharges.add(getPerUnitUsageChargeConverter().convert((PerUnitUsageChargeModel) usageCharge));
						perUnitUsageCharges.sort((d1, d2) -> {
							PerUnitUsageChargeData o1 = (PerUnitUsageChargeData) d1;
							PerUnitUsageChargeData o2 = (PerUnitUsageChargeData) d2;

							if (StringUtils.equals(o1.getUsageChargeType().getCode(), o2.getUsageChargeType().getCode())) {
								return StringUtils.compare(o1.getSubscriptionBillingId(), o2.getSubscriptionBillingId());
							} else {
								return StringUtils.compare(o1.getUsageChargeType().getCode(), o2.getUsageChargeType().getCode());
							}
						});
					}
				}
				usageCharges.addAll(volumeUsageCharges);
				usageCharges.addAll(perUnitUsageCharges);

				discountedSubscriptionPricePlanData.setUsageCharges(usageCharges);
			}
			//Default empty list if there are no Discounted Usage Charges present
			else{
				discountedSubscriptionPricePlanData.setUsageCharges(new LinkedList<>());
			}
		}


		 }else{
			//Default empty list if there are no Discounted Charges present
			discountedSubscriptionPricePlanData.setOneTimeChargeEntries(new LinkedList<>());
			discountedSubscriptionPricePlanData.setRecurringChargeEntries(new LinkedList<>());
			discountedSubscriptionPricePlanData.setUsageCharges(new LinkedList<>());
		}
		return discountedSubscriptionPricePlanData;
}


	//<editor-fold desc="Setters and Getters">
	public Converter < CpqPricingParameterModel,
			CpqPricingParameterData > getCpqPricingPrameterConverter() {
		return cpqPricingPrameterConverter;
	}

	public void setCpqPricingPrameterConverter(
			final Converter < CpqPricingParameterModel, CpqPricingParameterData > cpqPricingPrameterConverter) {
		this.cpqPricingPrameterConverter = cpqPricingPrameterConverter;
	}

	protected Converter < OneTimeChargeEntryModel,
			OneTimeChargeEntryData > getOneTimeChargeEntryConverter() {
		return oneTimeChargeEntryConverter;
	}

	@Required
	public void setOneTimeChargeEntryConverter(
			final Converter < OneTimeChargeEntryModel, OneTimeChargeEntryData > oneTimeChargeEntryConverter) {
		this.oneTimeChargeEntryConverter = oneTimeChargeEntryConverter;
	}

	protected Converter < RecurringChargeEntryModel,
			RecurringChargeEntryData > getRecurringChargeEntryConverter() {
		return recurringChargeEntryConverter;
	}

	@Required
	public void setRecurringChargeEntryConverter(
			final Converter < RecurringChargeEntryModel, RecurringChargeEntryData > recurringChargeEntryConverter) {
		this.recurringChargeEntryConverter = recurringChargeEntryConverter;
	}

	protected Converter < PerUnitUsageChargeModel,
			PerUnitUsageChargeData > getPerUnitUsageChargeConverter() {
		return perUnitUsageChargeConverter;
	}

	@Required
	public void setPerUnitUsageChargeConverter(
			final Converter < PerUnitUsageChargeModel, PerUnitUsageChargeData > perUnitUsageChargeConverter) {
		this.perUnitUsageChargeConverter = perUnitUsageChargeConverter;
	}

	protected Converter < VolumeUsageChargeModel,
			VolumeUsageChargeData > getVolumeUsageChargeConverter() {
		return volumeUsageChargeConverter;
	}

	@Required
	public void setVolumeUsageChargeConverter(
			final Converter < VolumeUsageChargeModel, VolumeUsageChargeData > volumeUsageChargeConverter) {
		this.volumeUsageChargeConverter = volumeUsageChargeConverter;
	}

	public SapSubscriptionBillingEffectivePriceService getSapSubscriptionBillingEffectivePriceService() {
		return sapSubscriptionBillingEffectivePriceService;
	}

	public void setSapSubscriptionBillingEffectivePriceService(
			final SapSubscriptionBillingEffectivePriceService sapSubscriptionBillingEffectivePriceService) {
		this.sapSubscriptionBillingEffectivePriceService = sapSubscriptionBillingEffectivePriceService;
	}
	public SapCpqSbFetchQuoteDiscountsService getSapCpqSbFetchQuoteDiscounts() {
		return sapCpqSbFetchQuoteDiscounts;
	}

	public void setSapCpqSbFetchQuoteDiscounts(SapCpqSbFetchQuoteDiscountsService sapCpqSbFetchQuoteDiscounts) {
		this.sapCpqSbFetchQuoteDiscounts = sapCpqSbFetchQuoteDiscounts;
	}


	public static String dateToString(final Date currentDate) {
		if (currentDate != null) {
			final DateFormat format = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
			return format.format(currentDate);
		}
		return null;
	}

	// </editor-fold>


}
