package org.example;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.example.crawler.Crawler;
import org.example.crawler.Downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {

        String input = "n";

        while (!Objects.equals(input, "q")){

            System.out.println("type crawler or query or q to quit\n");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            input = reader.readLine();

            if (Objects.equals(input, "crawler")){
                Crawler crawler = new Crawler();
                crawler.getURLsFromPage("https://sk.wikipedia.org/wiki/Nimnica", 0);
                try {
                    new Crawler().safeURLs(crawler.getUrls());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Crawler crawler2 = new Crawler();
                crawler2.loadUrls();
            }

            if (Objects.equals(input, "query")){
                System.out.println("type url\n");

                BufferedReader reader2 = new BufferedReader(
                        new InputStreamReader(System.in)
                );
                String input2 = reader.readLine();

            }
        }


    }
}