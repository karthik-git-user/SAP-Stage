package de.hybris.platform.sap.saprevenuecloudorder.populators.subscription;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPricePlanData;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import org.springframework.beans.factory.annotation.Required;

public class DefaultSubscriptionPricePopulator<SOURCE extends SubscriptionPricePlanModel, TARGET extends ProductData> implements
        Populator<SOURCE, TARGET> {
    private Populator<SubscriptionPricePlanModel, SubscriptionPricePlanData> pricePlanOneTimeChargePopulator;
    private Populator<SubscriptionPricePlanModel, SubscriptionPricePlanData> pricePlanRecurringChargePopulator;
    private Converter<SubscriptionPricePlanModel, SubscriptionPricePlanData> pricePlanUsageChargeConverter;
    private SubscriptionCommercePriceService commercePriceService;
    @Override
    public void populate(SOURCE source, TARGET target) throws ConversionException {
        if (source != null)
        {
            final SubscriptionPricePlanData pricePlanData = getPricePlanUsageChargeConverter().convert(source);
            pricePlanData.setName(source.getName());
            getPricePlanOneTimeChargePopulator().populate(source, pricePlanData);
            getPricePlanRecurringChargePopulator().populate(source, pricePlanData);
            if (target.getPrice() != null)
            {
                pricePlanData.setCurrencyIso(target.getPrice().getCurrencyIso());
                pricePlanData.setMaxQuantity(target.getPrice().getMaxQuantity());
                pricePlanData.setMinQuantity(target.getPrice().getMinQuantity());
            }
            target.setPrice(pricePlanData);
        }
    }

    protected Populator<SubscriptionPricePlanModel, SubscriptionPricePlanData> getPricePlanOneTimeChargePopulator()
    {
        return pricePlanOneTimeChargePopulator;
    }

    @Required
    public void setPricePlanOneTimeChargePopulator(
            final Populator<SubscriptionPricePlanModel, SubscriptionPricePlanData> pricePlanOneTimeChargePopulator)
    {
        this.pricePlanOneTimeChargePopulator = pricePlanOneTimeChargePopulator;
    }

    protected Populator<SubscriptionPricePlanModel, SubscriptionPricePlanData> getPricePlanRecurringChargePopulator()
    {
        return pricePlanRecurringChargePopulator;
    }

    @Required
    public void setPricePlanRecurringChargePopulator(
            final Populator<SubscriptionPricePlanModel, SubscriptionPricePlanData> pricePlanRecurringChargePopulator)
    {
        this.pricePlanRecurringChargePopulator = pricePlanRecurringChargePopulator;
    }

    protected Converter<SubscriptionPricePlanModel, SubscriptionPricePlanData> getPricePlanUsageChargeConverter()
    {
        return pricePlanUsageChargeConverter;
    }

    @Required
    public void setPricePlanUsageChargeConverter(
            final Converter<SubscriptionPricePlanModel, SubscriptionPricePlanData> pricePlanUsageChargeConverter)
    {
        this.pricePlanUsageChargeConverter = pricePlanUsageChargeConverter;
    }

    protected SubscriptionCommercePriceService getCommercePriceService()
    {
        return commercePriceService;
    }

    @Required
    public void setCommercePriceService(final SubscriptionCommercePriceService commercePriceService)
    {
        this.commercePriceService = commercePriceService;
    }


}
