package com.justinleegrant.myluceneplayground;

import com.justinleegrant.myluceneplayground.products.Product;
import org.apache.lucene.document.*;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;

/**
 *
 */
public class ProductIndexer {
    private static final FacetsConfig facetsConfig = new FacetsConfig();

    public static void addProductToIndex(IndexWriter w, DirectoryTaxonomyWriter taxoWriter, Product product) throws IOException {
        Document document = new Document();

        // Add the manufacturer and product name, which can be typed in any number of ways (for example
        document.add(new TextField(Product.ProductFields.MANUFACTURER.toString(), product.getManufacturer(), Field.Store.YES));
        document.add(new TextField(Product.ProductFields.NAME.toString(), product.getName(), Field.Store.YES));

        // this is a StringField instead of a TextField as it is used for facets, where case must be kept and rooting should not be allowed
        document.add(new StringField(Product.ProductFields.PRODUCT_TYPE.toString(), product.getProductType().toString(), Field.Store.YES));

        //add to the faceting index.
        document.add(new FacetField(Product.ProductFields.PRODUCT_TYPE.toString(), product.getProductType().toString()));

        document.add(new IntField(Product.ProductFields.PRICE_DOLLARS.toString(), product.getPrice().getAmountMajorInt(), Field.Store.YES));
        document.add(new IntField(Product.ProductFields.PRICE_CENTS.toString(), product.getPrice().getAmountMinorInt(), Field.Store.YES));

        //user the config.build to add first to the facet index, then add the resulting doc to the standard index
        w.addDocument(facetsConfig.build(taxoWriter, document));
    }
}
