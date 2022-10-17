package org.example.crawler;

import java.io.*;
import java.util.HashSet;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.util.*;

import java.io.IOException;
import java.util.HashSet;


public class Crawler {

    private static final int MAX_DEPTH = 2;
    private HashSet<String> urls;

    public Crawler(){
        urls = new HashSet<>();
    }

    public void getURLsFromPage(String URL, int depth) {
        if (!(urls.contains(URL)) && depth < MAX_DEPTH){
            try {
                Document document = Jsoup.connect(URL).get();
                Elements pageURLs = document.select("a[href]");
                depth++;
                urls.add(URL);
                for (Element page : pageURLs) {
                    getURLsFromPage(page.attr("abs:href"), depth);
                }

            } catch (IOException e) {
                System.out.println("404 not found\n");
            }
        }
    }

    public void safeURLs(HashSet<String> urls) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
         FileWriter fw = null;
        String jsonStr = mapper.writeValueAsString(urls);
        try {
            fw = new FileWriter("/Users/simondrienik/Documents/GitHub/vinf_project/urls.txt");
            fw.write(jsonStr);
        }
        catch (IOException e) {
            e.getStackTrace();
        } finally {
            try {
                assert fw != null;
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public HashSet<String> getUrls() {
        return urls;
    }
}
