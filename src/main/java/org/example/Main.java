package org.example;

import org.example.crawler.Crawler;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
       Crawler crawler = new Crawler();
       crawler.getURLsFromPage("https://sk.wikipedia.org/wiki/Nimnica", 0);
        try {
            new Crawler().safeURLs(crawler.getUrls());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}