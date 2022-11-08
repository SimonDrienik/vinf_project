package org.example.crawler;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Downloader {

    public Downloader(){

    }

    public void downloadContent(String url){
        if (url.startsWith("https://en.wikipedia.org/wiki/")) {
            try {
                String suburl = url.substring(30);
                File file = new File("src/main/resources/contents/" + suburl + ".html");
                file.createNewFile();
                String[] schemes = {"https"};
                UrlValidator urlValidator = new UrlValidator(schemes);
                if (urlValidator.isValid(url)) {

                    Connection.Response response = Jsoup.connect(url).execute();
                    String html = response.body();

                    BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/contents/" + suburl + ".html"));
                    writer.write(html);
                    writer.close();

                    //Indexer indexer = new Indexer("src/main/resources/contents/"+suburl+".html", url);
                    //indexer.createIndex("src/main/resources/contents/"+suburl+".html");

                }

            } catch (IOException e) {
                System.out.println("Download error: " + e.getMessage());
            }
        }
    }
}
