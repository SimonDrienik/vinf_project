package org.example.crawler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

import org.checkerframework.checker.units.qual.A;
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
import java.util.regex.Pattern;

import static com.google.common.net.InternetDomainName.isValid;


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
                if (URL.startsWith("https://en.wikipedia.org/wiki/"))
                    urls.add(URL);
                for (Element page : pageURLs) {
                    getURLsFromPage(page.attr("abs:href"), depth);
                }

            } catch (IOException e) {

            }
        }
    }

    public void safeURLs(HashSet<String> urls) throws IOException {
        System.out.println("saving urls..");
        ObjectMapper mapper = new ObjectMapper();
         FileWriter fw = null;
        String jsonStr = mapper.writeValueAsString(urls);
        try {
            fw = new FileWriter("src/main/resources/urls/urls.json");
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

    public void loadUrls() throws IOException {
        List<String> listOfUrls;
        String[] arr = null;
        try {
            Path filePath = Path.of("src/main/resources/urls/urls.json");
            String content = Files.readString(filePath);
            content = content.substring(1, content.length() - 1);
            Pattern ptr = Pattern.compile(",");
            arr = ptr.split(content);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*System.out.println("dowloading content..");
        for (int i = 0; i < arr.length; i++) {
            Downloader downloader = new Downloader();
            String url = arr[i].substring(1, arr[i].length() - 1);
            downloader.downloadContent(url);
        }*/

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

    public HashSet<String> getUrls() {
        return urls;
    }
}
