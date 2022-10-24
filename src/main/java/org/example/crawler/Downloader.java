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

        try {
            String name = RandomName.getAlphaNumericString(20);
            File file = new File("/Users/simondrienik/Documents/GitHub/vinf_project/contents/"+name+".html");
            file.createNewFile();
            String[] schemes = {"http","https"};
            UrlValidator urlValidator = new UrlValidator(schemes);
            if (urlValidator.isValid(url))
           {

                Connection.Response response = Jsoup.connect(url).execute();
                String html = response.body();

                BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/simondrienik/Documents/GitHub/vinf_project/contents/" + name + ".html"));
                writer.write(html);
                writer.close();

                Indexer indexer = new Indexer("/Users/simondrienik/Documents/GitHub/vinf_project/contents/" + name + ".html", url);
                indexer.createIndex("/Users/simondrienik/Documents/GitHub/vinf_project/contents/" + name + ".html");

            }

        } catch (IOException e) {
            System.out.println("Download error: " + e.getMessage());
        }

    }
}
