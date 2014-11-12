package com.justinleegrant.myluceneplayground.products;

import org.joda.money.Money;

/**
 *
 */
public class MicrosoftProduct extends AbstractProduct {
    public MicrosoftProduct(String nameIn, Money priceIn) {
        name = nameIn;
        price = priceIn;
        manufacturer = "Microsoft";
        productType = ProductType.ELECTRONICS;
    }
}
