/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.core.sapsalesordersimulation.cache.service;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.key.CacheUnitValueType;


/**
 * The <code>GenericCacheKey</code> provides an easy way to handle cache keys, which consist of a list of objects. <br>
 * 
 */
public class GenericCacheKey implements CacheKey
{

	/**
	 * Default typecode used if no other is specified.
	 */
	public static final String DEFAULT_SAP_TYPECODE = "SAPObjects";

	/**
	 * Array of keys of the GenericCacheKey.
	 */
	private final Object[] keys;

	/**
	 * Typecode of the GenericCacheKey.
	 */
	private final Object typeCode;

	/**
	 * Tenant of the GenericCacheKey.
	 */
	private String tenant;

	/**
	 * Valuetype of the GenericCacheKey.
	 */
	private CacheUnitValueType valueType;

	/**
	 * Creates GenericCacheKey for given array of keys and a type code.
	 * 
	 * @param keys
	 *           array of key objects
	 * @param typeCode
	 *           typecode of the GenericCacheKey
	 */
	public GenericCacheKey(final Object[] keys, final String typeCode)
	{
		this.keys = keys;
		this.typeCode = typeCode;
	}

	/**
	 * Creates GenericCacheKey for given array of keys, type code, tenant and value type.
	 * 
	 * @param keys
	 *           array of key objects
	 * @param typeCode
	 *           typecode of the GenericCacheKey
	 * @param tenant
	 *           tenant of the GenericCacheKey
	 * @param valueType
	 *           valuetype of the GenericCacheKey
	 */
	public GenericCacheKey(final Object[] keys, final String typeCode, final String tenant, final CacheUnitValueType valueType)
	{
		this(keys, typeCode);
		this.tenant = tenant;
		this.valueType = valueType;
	}

	/**
	 * Creates GenericCacheKey for given key and type code.
	 * 
	 * @param key
	 *           key of the GenericCacheKey
	 * @param typeCode
	 *           typecode of the GenericCacheKey
	 */
	public GenericCacheKey(final Object key, final String typeCode)
	{
		this.keys = new Object[]
		{ key };
		this.typeCode = typeCode;
	}

	/**
	 * Creates GenericCacheKey for given key, type code, tenant and value type.
	 * 
	 * @param key
	 *           the key
	 * @param typeCode
	 *           typecode of the GenericCacheKey
	 * @param tenant
	 *           tenant of the GenericCacheKey
	 * @param valueType
	 *           valuetype of the GenericCacheKey
	 */
	public GenericCacheKey(final Object key, final String typeCode, final String tenant, final CacheUnitValueType valueType)
	{
		this(key, typeCode);
		this.tenant = tenant;
		this.valueType = valueType;
	}

	/**
	 * Returns the hash code for this object.
	 * 
	 * @return Hash code for the object
	 */
	@Override
	public int hashCode()
	{
		int ret = 0;

		for (int i = 0; i < keys.length; i++)
		{
			if (keys[i] != null)
			{
				ret = ret ^ keys[i].hashCode();
			}
		}
		return ret;
	}

	/**
	 * Returns true, if this object is equal to the provided one.
	 * 
	 * @param o
	 *           Object to compare this with
	 * @return <code>true</code> if both objects are equal, <code>false</code> if not.
	 */
	@Override
	public boolean equals(final Object o) {

		if (o==null || !(o.getClass().equals(this.getClass()))) {
			return false;
		}

		if (o == this) {
			return true;
		}

		final GenericCacheKey command = (GenericCacheKey) o;

		if (command.keys.length != keys.length) {
			return false;
		}

		return isKeysEqual(command);

	}

	private boolean isKeysEqual(final GenericCacheKey command) {
		for (int i = 0; i < keys.length; i++) {
			if ((keys[i]==null && command.keys[i]!=null) || (keys[i]!=null && command.keys[i]==null)) {
				return false;
			}
			
			
			if (keys[i]!=null && !(keys[i].equals(command.keys[i])) ) {
				return false;
			}
			
		
		}

		return true;
	}

	/**
	 * Returns the object as string. <br>
	 * 
	 * @return String which contains all important fields of the object
	 */
	@Override
	public String toString()
	{
		final StringBuilder ret = new StringBuilder();

		for (int i = 0; i < keys.length; i++)
		{
			ret.append(keys[i].toString());
			if (i < keys.length - 1)
			{
				ret.append('+');
			}
		}
		return ret.toString();
	}

	@Override
	public CacheUnitValueType getCacheValueType()
	{
		return valueType;
	}

	@Override
	public String getTenantId()
	{
		return tenant;
	}

	@Override
	public Object getTypeCode()
	{
		if (typeCode != null)
		{
			return typeCode.toString();
		}
		return DEFAULT_SAP_TYPECODE;
	}
}
