package com.justinleegrant.myluceneplayground.products;

import org.joda.money.Money;

public class AppleProduct extends AbstractProduct {

    public AppleProduct(String nameIn, Money priceIn) {
        name = nameIn;
        price = priceIn;
        manufacturer = "Apple";
        productType = ProductType.ELECTRONICS;
    }

}
