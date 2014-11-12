package com.justinleegrant.myluceneplayground.products;

import org.joda.money.Money;

/**
 *
 */
public class ApplianceProduct extends AbstractProduct {
    public ApplianceProduct(String nameIn, String manufacturerIn, Money priceIn) {
        name = nameIn;
        price = priceIn;
        manufacturer = manufacturerIn;
        productType = ProductType.APPLIANCES;
    }
}
