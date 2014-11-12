package com.justinleegrant.myluceneplayground.products;

import org.joda.money.Money;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class AbstractProduct implements Product {
    protected String manufacturer;
    protected ProductType productType;
    protected String name;
    protected Money price;
    List<String> tags = new ArrayList<>();

    @Override
    public String getManufacturer() {
        return manufacturer;
    }

    @Override
    public String getName() { return name; }

    @Override
    public Money getPrice() {
        return price;
    }

    @Override
    public ProductType getProductType() {
        return productType;
    }

    public void addToTags(String tag) {
        tags.add(tag);
    }

    @Override
    public List<String> getTags() {
        return tags;
    }
}
