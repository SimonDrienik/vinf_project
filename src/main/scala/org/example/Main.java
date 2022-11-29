package org.example;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.example.crawler.Crawler;
import org.example.crawler.Downloader;
import org.example.crawler.Indexer;
import org.example.crawler.Queries;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {

        String input = "n";

        while (!Objects.equals(input, "e")){

            System.out.println("type [c]crawler, [d]download, i[index], [q]query or e to exit\n");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            input = reader.readLine();

            if (Objects.equals(input, "c")){
                Crawler crawler = new Crawler();
                System.out.println("crawling..");
                crawler.getURLsFromPage("https://en.wikipedia.org/wiki/Slovakia", 0);
                try {
                    new Crawler().safeURLs(crawler.getUrls());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (Objects.equals(input, "d")){
                System.out.println("test");
                Crawler crawler2 = new Crawler();
                crawler2.loadUrls();
            }

            if (Objects.equals(input, "i")){
                File dir = new File("src/main/resources/contents/");
                File[] directoryListing = dir.listFiles();
                Indexer indexer = new Indexer();
                System.out.println("indexing..");
                if (directoryListing != null) {
                    for (File child : directoryListing) {
                        String name = child.getName();
                        indexer.createIndex("src/main/resources/contents/"+name, "https://en.wikipedia.org/wiki/"+name);
                    }
                } else {
                    System.out.println("something went wrong.");
                }
                indexer.close();

            }

            if (Objects.equals(input, "q")){
                System.out.println("type category {locality, person, company}\n");

                BufferedReader reader2 = new BufferedReader(
                        new InputStreamReader(System.in)
                );
                String input2 = "n";
                input2 = reader2.readLine();
                Queries queries = new Queries(input2);
                List<Document> documents = queries.searchCategory();
                for (Document document : documents) {
                    System.out.println(document.get("name"));
                }
            }
        }
    }
}