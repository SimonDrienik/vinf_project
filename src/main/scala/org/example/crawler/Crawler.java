package org.example.crawler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaSparkContext$;
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

    private static final int MAX_DEPTH = 999999999;
    private HashSet<String> urls;

    public Crawler(){
        urls = new HashSet<>();
    }
    int counter = 0;
    public void getURLsFromPage(String URL, int depth) {
        if (!(urls.contains(URL))){
            try {
                Document document = Jsoup.connect(URL).get();
                Elements pageURLs = document.select("a[href]");
                depth++;
                if (URL.startsWith("https://en.wikipedia.org/wiki/") && !(URL.contains("#cite"))) {
                    urls.add(URL);
                    counter++;
                    System.out.println("page count:" + counter);
                }
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
            listOfUrls = List.of(ptr.split(content));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SparkConf sparkConf = new SparkConf().setAppName("Download web sites of RDD")
                .setMaster("local[3]").set("spark.executor.memory","2g");
        // start a spark context
        JavaRDD<String> rdd;
        try (JavaSparkContext sc = new JavaSparkContext(sparkConf)) {

            System.out.println("Number of partitions : " + 3);
            System.out.println("dowloading content..");

            long start = System.currentTimeMillis();
            rdd = sc.parallelize(listOfUrls, 3);
            rdd.foreach(item -> {
                Downloader downloader = new Downloader();
                downloader.downloadContent(item);
            });
            long end = System.currentTimeMillis();
            System.out.println("Completed download of data in sec: " + (end - start) / 1000);
        }
    }

    public HashSet<String> getUrls() {
        return urls;
    }
}
