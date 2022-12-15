package org.example.crawler;

import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaSparkContext$;
import org.apache.spark.sql.execution.datasources.json.JsonOutputWriter;
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

    /**
     * MAX_DEPTH is limit for crawler, it indicates max depth of search graph. Can be changed
     */
    private static final int MAX_DEPTH = 20;

    /**
     * urls is hashset that stores pernamently all crawled links in crawler process.
     */
    private HashSet<String> urls;

    public Crawler(){
        urls = new HashSet<String>(6000000);
    }

    /**
     * counter is actual count of urls crawled... you can change limit of crawled urls in  line 65 in condition
     */
    private int counter = 0;
    private Elements pageURLs2 = null;
    private Document document = null;

    /**
     *
     * @param URL is actual URL from which content, another urls are crawled...
     * @param depth is actual depth of search graph of crawling...
     * method runs, if URL is not already in list of crawled urls && depth is less then MAX_DEPTH && url is from wiki page and not contains #cite in suburl and count of crawled urls is not more then 70001...
     * Downloading content [document], selecting all links [pageURLs2] is done with JSOUP library...
     * URL is saved do list of urls [urls], counter is count of urls already crawled...
     * If pageURLs2 is not empty: new recursive call is called with all newly crawled urls from pageURLs2...
     */
    public void getURLsFromPage(String URL, int depth) {
            if (!(urls.contains(URL)) && depth < MAX_DEPTH && URL.startsWith("https://en.wikipedia.org/wiki") && !(URL.contains("#cite")) && counter < 70001) {
                try {
                        depth++;
                        document = Jsoup.connect(URL).get();
                        pageURLs2 = document.select("a[href]");
                        urls.add(URL);
                        counter++;
                        System.out.println(counter);

                    if (pageURLs2 != null)
                        for (Element element : pageURLs2) {
                            getURLsFromPage(element.attr("abs:href"), depth);
                        }

                } catch (IOException e) {
                    System.out.println("crawler error: " + e.getMessage());
                }
            }
    }

    /**
     *
     * @param urls is hashset that is filled with crawled urls...
     * This method maps content of urls as json format and save it to urls.json  that is used for downloading content of html files...
     */
    public void safeURLs(HashSet<String> urls) throws IOException {
        System.out.println("saving urls..");
        ObjectMapper mapper = new ObjectMapper();
         FileWriter fw = null;
        System.out.println("count:" + urls.size());
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

    /**
     * load urls from json file, split it and save it to listOfUrls...<p>
     * loads sparkConfig  with params as memory allocations [3g] ...<p>
     * Start sparkContext for parallel downloading...<p>
     * With JavaRDD run parallel processing, that is applied on method downloader with every item fromlistOfUrls. ...<p>
     * listOfUrl is splited to 3 partitions. In 3 parallel processes runs downloader.downloadContent(item) method with param of  every item of each partition in foreach loop...<p>
     */
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
                .setMaster("local[3]").set("spark.executor.memory","3g");
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
