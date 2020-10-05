/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



@JsonIgnoreProperties(ignoreUnknown = true)
public class Subscription
{

	private String subscriptionId;
	private String validFrom;
	private String validUntil;
	private String createdAt;
	private String changedAt;
	private Customer customer;
	private Market market;
	private List<Snapshot> snapshots;
	private MetaData metaData;
	private String documentNumber;
	private String cancellationReason;
	private String effectiveExpirationDate;
	private Payment payment;
	private String status;

	private RenewalTerm renewalTerm;
	private boolean unlimited;
	private CancellationPolicy cancellationPolicy;
	private String withdrawnAt;

	public String getEffectiveExpirationDate() {
		return effectiveExpirationDate;
	}

	public void setEffectiveExpirationDate(String effectiveExpirationDate) {
		this.effectiveExpirationDate = effectiveExpirationDate;
	}

	public String getCancellationReason()
	{
		return cancellationReason;
	}

	public void setCancellationReason(final String cancellationReason)
	{
		this.cancellationReason = cancellationReason;
	}

	public String getDocumentNumber()
	{
		return documentNumber;
	}

	public void setDocumentNumber(final String documentNumber)
	{
		this.documentNumber = documentNumber;
	}

	public MetaData getMetaData()
	{
		return metaData;
	}

	public void setMetaData(final MetaData metaData)
	{
		this.metaData = metaData;
	}

	public String getValidFrom()
	{
		return validFrom;
	}

	public void setValidFrom(final String validFrom)
	{
		this.validFrom = validFrom;
	}

	public String getValidUntil()
	{
		return validUntil;
	}

	public void setValidUntil(final String validUntil)
	{
		this.validUntil = validUntil;
	}

	public String getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(final String createdAt)
	{
		this.createdAt = createdAt;
	}

	public String getChangedAt()
	{
		return changedAt;
	}

	public void setChangedAt(final String changedAt)
	{
		this.changedAt = changedAt;
	}

	public Customer getCustomer()
	{
		return customer;
	}

	public void setCustomer(final Customer customer)
	{
		this.customer = customer;
	}

	public Market getMarket()
	{
		return market;
	}

	public void setMarket(final Market market)
	{
		this.market = market;
	}

	public List<Snapshot> getSnapshots()
	{
		return snapshots;
	}

	public void setSnapshots(final List<Snapshot> snapshots)
	{
		this.snapshots = snapshots;
	}

	public String getSubscriptionId()
	{
		return subscriptionId;
	}

	public void setSubscriptionId(final String subscriptionId)
	{
		this.subscriptionId = subscriptionId;
	}

	public RenewalTerm getRenewalTerm() {
		return renewalTerm;
	}

	public void setRenewalTerm(RenewalTerm renewalTerm) {
		this.renewalTerm = renewalTerm;
	}

	public boolean isUnlimited() {
		return unlimited;
	}

	public void setUnlimited(boolean unlimited) {
		this.unlimited = unlimited;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public CancellationPolicy getCancellationPolicy() {
		return cancellationPolicy;
	}

	public void setCancellationPolicy(CancellationPolicy cancellationPolicy) {
		this.cancellationPolicy = cancellationPolicy;
	}

	public String getWithdrawnAt() {
		return withdrawnAt;
	}

	public void setWithdrawnAt(String withdrawnAt) {
		this.withdrawnAt = withdrawnAt;
	}

	@Override
	public String toString() {
		return "Subscription{" +
				"subscriptionId='" + subscriptionId + '\'' +
				", validFrom='" + validFrom + '\'' +
				", validUntil='" + validUntil + '\'' +
				", createdAt='" + createdAt + '\'' +
				", changedAt='" + changedAt + '\'' +
				", customer=" + customer +
				", market=" + market +
				", snapshots=" + snapshots +
				", metaData=" + metaData +
				", documentNumber='" + documentNumber + '\'' +
				", cancellationReason='" + cancellationReason + '\'' +
				", effectiveExpirationDate='" + effectiveExpirationDate + '\'' +
				", payment=" + payment +
				", status='" + status + '\'' +
				", renewalTerm=" + renewalTerm +
				", unlimited=" + unlimited +
				", cancellationPolicy=" + cancellationPolicy +
				", withdrawnAt='" + withdrawnAt + '\'' +
				'}';
	}
}