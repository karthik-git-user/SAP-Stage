/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchangemdm.service.impl;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundAddressModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.sap.sapcpicustomerexchange.service.impl.SapCpiCustomerDefaultConversionService;
import de.hybris.platform.sap.sapcpicustomerexchangemdm.constants.SapcpicustomerexchangemdmConstants;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * SapCpiMDMCustomerDefaultConversionService
 */
public class SapCpiMDMCustomerDefaultConversionService extends SapCpiCustomerDefaultConversionService {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
			.ofPattern(SapcpicustomerexchangemdmConstants.DATE_TIME_FORMAT);

	/**
	 * Converts Customer And Address Model to Customer Out Bound Integration Object
	 *
	 * @param customerModel
	 * @param addressModel
	 * @param baseStoreUid
	 * @param sessionLanguage
	 * @return SAPCpiOutboundAddressModel
	 */
	@Override
	public SAPCpiOutboundCustomerModel convertCustomerToSapCpiCustomer(final CustomerModel customerModel,
			final AddressModel addressModel, final String baseStoreUid, final String sessionLanguage) {

		final SAPCpiOutboundCustomerModel sapCpiOutboundCustomer = super.convertCustomerToSapCpiCustomer(customerModel,
				addressModel, baseStoreUid, sessionLanguage);
		sapCpiOutboundCustomer.setSapConsumerID(customerModel.getSapConsumerID());
		sapCpiOutboundCustomer.setSapCpiOutboundAddress(populateOutboundAddresses(customerModel, addressModel));
		return sapCpiOutboundCustomer;
	}

	/**
	 * @param customerModel
	 * @param customerAddress
	 * @return Collection<SAPCpiOutboundAddressModel>
	 */
	protected Collection<SAPCpiOutboundAddressModel> populateOutboundAddresses(final CustomerModel customerModel,
			final AddressModel customerAddress) {

		final Set<SAPCpiOutboundAddressModel> sapCpiMDMOutboundAddress = new HashSet<>();
		List<AddressModel> addressList = new ArrayList<>(customerModel.getAddresses());

		if (customerAddress!=null && !addressList.contains(customerAddress)) {
			addressList.add(customerAddress);
		}

		for (final AddressModel address : addressList) {
			sapCpiMDMOutboundAddress.add(populateOutboundAddress(address, customerModel));
		}

		return sapCpiMDMOutboundAddress;

	}

	/**
	 * Populating sapCpiOutboundAddress Integration Object Form AddressModel
	 *
	 * @param address
	 * @param customerModel
	 * @return SAPCpiOutboundAddressModel
	 */
	protected SAPCpiOutboundAddressModel populateOutboundAddress(final AddressModel address,
			final CustomerModel customerModel) {

		final SAPCpiOutboundAddressModel sapCpiOutboundAddressModel = getModelService()
				.create(SAPCpiOutboundAddressModel.class);
		final String addressCountryIsoCode = address.getCountry() != null ? address.getCountry().getIsocode() : null;
		final String addressRegionIsoCode = address.getRegion() != null ? address.getRegion().getIsocodeShort() : null;
		sapCpiOutboundAddressModel.setSapAddressUUID(address.getSapAddressUUID());
		sapCpiOutboundAddressModel.setApartment(address.getAppartment());
		sapCpiOutboundAddressModel.setBuilding(address.getBuilding());
		sapCpiOutboundAddressModel.setCellphone(address.getCellphone());
		sapCpiOutboundAddressModel.setCountryIsoCode(addressCountryIsoCode);
		sapCpiOutboundAddressModel.setDistrict(address.getDistrict());
		sapCpiOutboundAddressModel.setEmail(address.getEmail());
		sapCpiOutboundAddressModel.setFaxNumber(address.getFax());
		sapCpiOutboundAddressModel.setFirstName(address.getFirstname());
		sapCpiOutboundAddressModel.setLastName(address.getLastname());
		sapCpiOutboundAddressModel.setMiddleName(address.getMiddlename());
		sapCpiOutboundAddressModel.setMiddleName2(address.getMiddlename2());
		sapCpiOutboundAddressModel.setTelNumber(address.getPhone1());
		sapCpiOutboundAddressModel.setPhone2(address.getPhone2());
		sapCpiOutboundAddressModel.setPobox(address.getPobox());
		sapCpiOutboundAddressModel.setPostalCode(address.getPostalcode());
		sapCpiOutboundAddressModel.setRegionIsoCode(addressRegionIsoCode);
		sapCpiOutboundAddressModel.setStreetName(address.getStreetname());
		sapCpiOutboundAddressModel.setStreetNumber(address.getStreetnumber());
		sapCpiOutboundAddressModel.setTitleCode(address.getTitle() != null ? address.getTitle().getCode() : null);
		sapCpiOutboundAddressModel.setCity(address.getTown());
		sapCpiOutboundAddressModel.setGender(address.getGender() != null ? address.getGender().toString() : null);
		sapCpiOutboundAddressModel.setCreationtime(address.getCreationtime());
		final String addressFormattedCreatedTime = address.getCreationtime() != null
				? ZonedDateTime.ofInstant(address.getCreationtime().toInstant(), ZoneOffset.UTC).format(DATE_FORMATTER)
				: null;
		sapCpiOutboundAddressModel.setCreateDate(addressFormattedCreatedTime);
		final String addressFormattedDOB = address.getDateOfBirth() != null
				? ZonedDateTime.ofInstant(address.getDateOfBirth().toInstant(), ZoneOffset.UTC).format(DATE_FORMATTER)
				: null;
		sapCpiOutboundAddressModel.setDateOfBirth(addressFormattedDOB);
		if (address.equals(customerModel.getDefaultShipmentAddress())) {
			sapCpiOutboundAddressModel.setDefaultAddress("True");
		} else {
			sapCpiOutboundAddressModel.setDefaultAddress("False");
		}

		return sapCpiOutboundAddressModel;

	}

}
