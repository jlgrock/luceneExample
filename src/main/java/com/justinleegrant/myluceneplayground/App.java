package com.justinleegrant.myluceneplayground;

import com.justinleegrant.myluceneplayground.products.AppleProduct;
import com.justinleegrant.myluceneplayground.products.ApplianceProduct;
import com.justinleegrant.myluceneplayground.products.MicrosoftProduct;
import com.justinleegrant.myluceneplayground.products.Product;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.joda.money.Money;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static final Directory directory = new RAMDirectory();
    private static final Directory taxoDir = new RAMDirectory();

    private static IndexWriter createStandardIndex() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, config);
        return indexWriter;
    }

    private static DirectoryTaxonomyWriter createFacetIndex() throws IOException {
        // Writes facet ords to a separate directory from the main index
        DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(directory);
        return taxoWriter;
    }

    private static void addMockDataToIndex(IndexWriter indexWriter, DirectoryTaxonomyWriter taxoWriter) throws IOException {
        AppleProduct iPadAir = new AppleProduct("iPad Air", Money.parse("USD 499.99"));
        AppleProduct iPadRetina = new AppleProduct("iPad Retina", Money.parse("USD 399.99"));
        AppleProduct iPadMini = new AppleProduct("iPad Mini", Money.parse("USD 299.99"));
        AppleProduct iPhone = new AppleProduct("iPhone", Money.parse("USD 699.99"));
        AppleProduct iPhonePlus = new AppleProduct("iPhone Plus", Money.parse("USD 799"));
        AppleProduct macBookPro = new AppleProduct("Macbook Pro", Money.parse("USD 1299.99"));
        AppleProduct macBookProRetina = new AppleProduct("Macbook Pro Retina", Money.parse("USD 2099.99"));

        MicrosoftProduct windowsPhone = new MicrosoftProduct("Windows Phone", Money.parse("USD 599.99"));
        MicrosoftProduct xbox = new MicrosoftProduct("Xbox", Money.parse("USD 399.99"));

        ApplianceProduct fridge1 = new ApplianceProduct("WRS325FDAM", "Whirlpool", Money.parse("USD 988.20"));
        ApplianceProduct fridge2 = new ApplianceProduct("BI36UFD", "Sub Zero", Money.parse("USD 6399.00"));
        ApplianceProduct stove = new ApplianceProduct("FFGF3047LS", "Frigidaire", Money.parse("USD 487.80"));

        //add to standard index
        ProductIndexer.addProductToIndex(indexWriter, taxoWriter, iPadAir);
        ProductIndexer.addProductToIndex(indexWriter, taxoWriter, iPadRetina);
        ProductIndexer.addProductToIndex(indexWriter, taxoWriter, iPadMini);
        ProductIndexer.addProductToIndex(indexWriter, taxoWriter, iPhone);
        ProductIndexer.addProductToIndex(indexWriter, taxoWriter, iPhonePlus);
        ProductIndexer.addProductToIndex(indexWriter, taxoWriter, macBookPro);
        ProductIndexer.addProductToIndex(indexWriter, taxoWriter, macBookProRetina);

        ProductIndexer.addProductToIndex(indexWriter, taxoWriter, windowsPhone);
        ProductIndexer.addProductToIndex(indexWriter, taxoWriter, xbox);

        ProductIndexer.addProductToIndex(indexWriter, taxoWriter, fridge1);
        ProductIndexer.addProductToIndex(indexWriter, taxoWriter, fridge2);
        ProductIndexer.addProductToIndex(indexWriter, taxoWriter, stove);
    }

    private static Query createQuery() throws IOException, ParseException {
        System.out.println("SIMPLE QUERY");
        String manufacturer = Product.ProductFields.MANUFACTURER.toString();
        Query query = new QueryParser(manufacturer, new StandardAnalyzer()).parse("MICROSOFT");
        return query;
    }

    private static Query createWildcardQuery() throws IOException, ParseException {
        System.out.println("WILCARD QUERY");
        Term term = new Term(Product.ProductFields.NAME.toString(), "i*");
        Query query = new WildcardQuery(term);
        return query;
    }

//TODO
//    private static Query createMultiTermQuery() throws IOException, ParseException {
//        BooleanQuery booleanQuery = new BooleanQuery();
//        Query query1 = new TermQuery(new Term("bodytext", "<text>"));
//        Query query2 = new TermQuery(new Term("title", "<text>"));
//        booleanQuery.add(query1, BooleanClause.Occur.SHOULD);
//        booleanQuery.add(query2, BooleanClause.Occur.SHOULD);
//        return booleanQuery;
//    }

    private static void printIndex() throws IOException {
        System.out.println("PRINT INDEX");
        Query query = new MatchAllDocsQuery();
        executeQuery(query);
        printFacets(queryFacets(query));
    }

    private static void executeQuery(final Query query) throws IOException {
        System.out.println(String.format("EXECUTE QUERY (query=%s)", query.toString()));
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(directory));
        TopDocs results = indexSearcher.search(query, 10);
        System.out.println("Total Results = " + results.totalHits);
        printResults(indexSearcher, results);
    }

    private static void printResults(IndexSearcher indexSearcher, TopDocs results) throws IOException {
        for (ScoreDoc scoredoc : results.scoreDocs) {
            Document doc = indexSearcher.doc(scoredoc.doc);
            System.out.println(String.format("DocName: %s", doc.get(Product.ProductFields.NAME.toString())));
        }
    }

    /** User runs a query and counts facets. */
    private static List<FacetResult> queryFacets(Query query) throws IOException {
        DirectoryReader indexReader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);

        FacetsCollector fc = new FacetsCollector();

        // MatchAllDocsQuery is for "browsing" (counts facets
        // for all non-deleted docs in the index); normally
        // you'd use a "normal" query:
        FacetsCollector.search(searcher, query, 10, fc);

        // Retrieve results
        List<FacetResult> results = new ArrayList<>();

        Facets facets = new FastTaxonomyFacetCounts(taxoReader, new FacetsConfig(), fc);
        results.add(facets.getTopChildren(10, Product.ProductFields.PRODUCT_TYPE.toString()));

        indexReader.close();
        taxoReader.close();

        return results;
    }

    public static void main(String[] args) throws Exception {
        IndexWriter indexWriter = createStandardIndex();
        DirectoryTaxonomyWriter facetIndexWriter = createFacetIndex();

        addMockDataToIndex(indexWriter, facetIndexWriter);
        indexWriter.close();
        facetIndexWriter.close();

        printIndex();
        System.out.println();

        Query query = createQuery();
        executeQuery(query);
        printFacets(queryFacets(query));
        System.out.println();

        Query query2 = createWildcardQuery();
        executeQuery(query2);
        printFacets(queryFacets(query2));
        System.out.println();
    }

    private static void printFacets(List<FacetResult> facetResults) {
        for (FacetResult facetResult : facetResults) {
            System.out.printf("FACET: %s", facetResult);
        }
        System.out.println();
    }

}
