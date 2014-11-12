package com.justinleegrant.myluceneplayground.products;

import org.joda.money.Money;

import java.util.List;

public interface Product {
    enum ProductFields {
        MANUFACTURER("Manufacturer"),
        NAME("Name"),
        PRICE_DOLLARS("Price-Dollars"),
        PRICE_CENTS("Price-Cents"),
        PRODUCT_TYPE("Product Type"),
        TAGS("Tags");

        String fieldName;

        private ProductFields(String fieldNameIn) {
            this.fieldName = fieldNameIn;
        }

        public String toString() {
            return fieldName;
        }
    }

    String getManufacturer();
    String getName();
    Money getPrice();
    ProductType getProductType();
    List<String> getTags();
}
