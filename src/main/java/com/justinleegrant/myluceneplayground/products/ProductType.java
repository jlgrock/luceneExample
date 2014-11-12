package com.justinleegrant.myluceneplayground.products;

public enum ProductType {
    HOME_AND_GARDEN("Home and Garden"),
    ELECTRONICS("Electronics"),
    GROCERY("Grocery"),
    MEDIA("Media"),
    APPLIANCES("Appliances");

    String name;

    private ProductType(final String nameIn) {
        name = nameIn;
    }

    public String toString() {
        return name;
    }
}
