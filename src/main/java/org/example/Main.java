package org.example;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.example.crawler.Crawler;
import org.example.crawler.Downloader;
import org.example.crawler.Queries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {

        String input = "n";

        while (!Objects.equals(input, "q")){

            System.out.println("type crawler or query or q to quit\n");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            input = reader.readLine();

            if (Objects.equals(input, "crawler")){
                Crawler crawler = new Crawler();
                System.out.println("crawling..");
                crawler.getURLsFromPage("https://en.wikipedia.org/wiki/Slovakia", 0);
                try {
                    new Crawler().safeURLs(crawler.getUrls());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Crawler crawler2 = new Crawler();
                crawler2.loadUrls();
            }

            if (Objects.equals(input, "query")){
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