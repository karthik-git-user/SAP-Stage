/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceproduct.inbound.events;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;

/**
 * Hook for handling skills
 * @deprecated since 2003 due to skills are handled in a different way
 */
@Deprecated(since = "2003", forRemoval = true)
public class DefaultSapCpiServiceProductPersistenceHook implements PrePersistHook
{
	private static final int MAX_LENGTH_FORTY = 40;
	private static final String SKILL_DELIMITER = "||";
	private static final String SHORT = "SHORT:";
	private static final String LONG = "LONG:";
	private static final String ENDOFSKILLS = ":ENDOFSKILLS";
	private static final String SKILLS = "SKILLS:";
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapCpiServiceProductPersistenceHook.class);

	/**
	 * Execute method
	 * @param item incoming item
	 * @deprecated since 2003 due to skills are handled in a different way
	 */
	@Override
	@Deprecated(since = "2003", forRemoval = true)
	public Optional<ItemModel> execute(ItemModel item) {
		
		LOG.info("Executing Pre-Persistent Hook for Service Product");
		if(item instanceof ProductModel) {
			
			ProductModel product = (ProductModel) item;
			if(product.getServiceCode()==null || product.getServiceCode().isEmpty()) {
				return Optional.of(item);
			}
			processSkills(product);
			
		}
		return Optional.of(item);
	}
	
	/**
	 * Process skills method
	 * @param product product
	 * @deprecated since 2003 due to skills are handled in a different way
	 */
	@Deprecated(since = "2003", forRemoval = true)
	protected void processSkills(final ProductModel product) {

		if(!isValidSkillsData(product.getSkillsDescription())) {
			product.setSkillsDescription(null);
			return;
		}
		String skillsDescription = StringUtils.substringBetween(product.getSkillsDescription(), SKILLS, ENDOFSKILLS);
		String skillsSummary = StringUtils.substringBetween(skillsDescription, SHORT + SKILL_DELIMITER, SKILL_DELIMITER + LONG);
		
		skillsDescription = SKILLS + skillsDescription.replace(SKILL_DELIMITER, System.lineSeparator()) + ENDOFSKILLS;
		if(skillsSummary != null && skillsSummary.length() <= MAX_LENGTH_FORTY) {
			product.setSkillSummary(skillsSummary);
		}
		product.setSkillsDescription(skillsDescription);
		
	}

	/**
	 * Check skills data
	 * @param skillsDescription skills description
	 * @return result
	 * @deprecated since 2003 due to skills are handled in a different way
	 */
	@Deprecated(since = "2003", forRemoval = true)
	protected boolean isValidSkillsData(String skillsDescription) {

		return !(skillsDescription == null || skillsDescription.isEmpty() || !skillsDescription.contains(SKILLS) || !skillsDescription.contains(ENDOFSKILLS) || !skillsDescription.contains(LONG) || !skillsDescription.contains(SHORT));//NOSONAR
	}

}
